package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.List;

public class DistillerBlockEntity extends BlockEntity {
    // パラメータ
    private int airRate = 10;        // 送風率 (10-100)
    private float currentTemp = 20f; // 現在の表示温度
    private int fuel = 0;            // 燃料残量 (mB)
    private int dangerLevel = 0;     // 累積ダメージ (0-100)
    private int tarAmount = 0;       // タール残量 (mB)

    // 1秒(20ticks)に1回の判定用
    private int timer = 0;

    public DistillerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.DISTILLER_BE.get(), p_155229_, p_155230_);
    }

    public void serverTick() {
        this.timer++;

        // アイテムスキャンは毎Tick（または5Tickおき）でOK
        if (this.timer % 5 == 0) {
            this.scanItems(this.level, this.worldPosition);
        }

        if (this.fuel > 0) {
            // 1秒(20ticks)に一度だけ重い処理と燃料消費を行う
            if (this.timer >= 20) {
                // 燃料消費：送風率と同じ値を1秒に1回引く
                // これで送風100%のとき、毎秒100消費されるようになります
                this.fuel = Math.max(0, this.fuel - this.airRate);

                updateThermodynamics(this.level, this.worldPosition);
                processDistillation();

                this.timer = 0;
                this.setChanged();
            }
        } else {
            // 燃料切れ時の冷却（これも1秒おき）
            if (this.timer >= 20) {
                if (this.currentTemp > 20) this.currentTemp -= 5f;
                this.timer = 0;
            }
        }
    }

    private void scanItems(Level level, BlockPos pos) {
        // ブロックの少し上（y+1.0 〜 y+2.0）を広くスキャンする
        AABB area = new AABB(pos).inflate(0.5, 1.0, 0.5);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity itemEntity : items) {
            if (!itemEntity.isAlive()) continue; // すでに消えかかっているアイテムは無視

            ItemStack stack = itemEntity.getItem();

            // 燃料の処理
            int burnTime = ForgeHooks.getBurnTime(stack, null);
            if (burnTime > 0 && !stack.is(Items.LAVA_BUCKET)) {
                if (this.fuel + 100 <= 10000) { // 余裕がある時だけ吸い込む
                    this.fuel += 100; // テスト用に固定値
                    stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.5f, 1.2f);
                    return; // 1tickに1個ずつ処理するのが安全
                }
            }

            // タールバケツの処理
            if (stack.is(ModItems.TAR_BUCKET.get())) {
                if (this.tarAmount + 1000 <= 10000) {
                    this.tarAmount += 1000;

                    // バケツを空にする
                    itemEntity.discard();
                    ItemEntity emptyBucket = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, new ItemStack(Items.BUCKET));
                    level.addFreshEntity(emptyBucket);

                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return;
                }
            }
        }
    }

    private void updateThermodynamics(Level level, BlockPos pos) {
        // 目標温度の計算
        float targetTemp = (this.airRate * 3) + 70;

        // 慣性の再現 ( lerp: 20%ずつ近づく )
        this.currentTemp += (targetTemp - this.currentTemp) * 0.2f;

        // 350℃超えの過熱演出・判定
        if (this.currentTemp > 350f) {
            // 累積ダメージ増加
            this.dangerLevel += 10;

            // アイテムが燃える音を再生 (位置, 音の種類, カテゴリ, 音量, ピッチ)
            level.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 1.0f, 1.0f);

            // 煙のパーティクル（さらに激しく）
            ((ServerLevel)level).sendParticles(ParticleTypes.LARGE_SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                    5, 0.1, 0.1, 0.1, 0.05);

            if (this.dangerLevel >= 100) {
                explode(level, pos);
            }
        } else {
            // 安全圏なら累積ダメージをゆっくり回復
            this.dangerLevel = Math.max(0, this.dangerLevel - 5);
        }
    }

    // 内部タンク（mB単位）
    private int output1, output2, output3;
    private final int MAX_STORAGE = 10000;

    private void processDistillation() {
        // 原料（タール）がない、または出力がいっぱいなら何もしない
        if (this.tarAmount <= 0) return;

        // 1tickあたりの変換量 (温度や送風量に応じて速くしても面白い)
        int processSpeed = 2;

        // 温度帯による分岐
        if (this.currentTemp >= 100f && this.currentTemp <= 180f) {
            // Output 1: ベンゼン / ガソリン帯
            transferTarToOutput(1, processSpeed);
        }
        else if (this.currentTemp > 180f && this.currentTemp <= 250f) {
            // Output 2: ナフサ / 灯油帯
            transferTarToOutput(2, processSpeed);
        }
        else if (this.currentTemp > 250f && this.currentTemp <= 350f) {
            // Output 3: 廃油 / 軽油帯
            transferTarToOutput(3, processSpeed);
        }
        else if (this.currentTemp > 350f) {
            // 350度を超えた「過熱」状態
            // タールは激しく消費されるが、すべて「廃液(Output 3)」になり、一部は消失（焦げ）
            transferTarToOutput(3, processSpeed * 2);
        }
    }

    private void transferTarToOutput(int tankIndex, int amount) {
        // タールが足りない場合は全量を処理
        int actualAmount = Math.min(this.tarAmount, amount);

        switch (tankIndex) {
            case 1 -> {
                if (this.output1 + actualAmount <= MAX_STORAGE) {
                    this.tarAmount -= actualAmount;
                    this.output1 += actualAmount;
                }
            }
            case 2 -> {
                if (this.output2 + actualAmount <= MAX_STORAGE) {
                    this.tarAmount -= actualAmount;
                    this.output2 += actualAmount;
                }
            }
            case 3 -> {
                if (this.output3 + actualAmount <= MAX_STORAGE) {
                    this.tarAmount -= actualAmount;
                    this.output3 += actualAmount;
                }
            }
        }
    }

    public void tryCollectFluid(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        // バケツを持っていない、または空バケツでないなら終了
        if (!heldItem.is(Items.BUCKET)) return;

        // 優先順位: Output 1 > Output 2 > Output 3
        if (this.output1 >= 1000) {
            fillBucket(player, hand, ModItems.BENZENE_BUCKET.get());
            this.output1 -= 1000;
        } else if (this.output2 >= 1000) {
            fillBucket(player, hand, ModItems.NAPHTHA_BUCKET.get());
            this.output2 -= 1000;
        } else if (this.output3 >= 1000) {
            fillBucket(player, hand, ModItems.WASTE_OIL_BUCKET.get());
            this.output3 -= 1000;
        }
    }

    private void fillBucket(Player player, InteractionHand hand, Item fluidBucket) {
        ItemStack currentBucket = player.getItemInHand(hand);
        currentBucket.shrink(1); // 空バケツを減らす

        ItemStack filledBucket = new ItemStack(fluidBucket);
        if (!player.getInventory().add(filledBucket)) {
            player.drop(filledBucket, false); // インベントリがいっぱいなら足元に
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private void explode(Level level, BlockPos pos) {
        // 1. 物理的な爆発（ブロック破壊を伴う）
        // 威力 2.0f は、チェストや周囲の機械を適度に破壊しつつ、広範囲を更地にしすぎない絶妙なラインです
        level.explode(null,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                2.0f, // 爆発の威力
                true, // 炎を発生させる（タールが飛び散った演出）
                Level.ExplosionInteraction.BLOCK // ブロックを破壊する
        );

        // 2. プレイヤーへのダメージ処理（3ブロック以内のエンティティのみ）
        AABB explosionArea = new AABB(pos).inflate(4.0); // 4ブロックの範囲をスキャン
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, explosionArea);

        for (LivingEntity entity : entities) {
            double distance = entity.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            // 3ブロック以内 (3^2 = 9)
            if (distance <= 9.0) {
                // 直接的なダメージを与える
                entity.hurt(level.damageSources().explosion(null, null), 20.0f); // 即死級ダメージ
            }
            // 4ブロック離れている (4^2 = 16)
            else if (distance >= 16.0) {
                // 何もしない、またはノックバックのみ
            }
        }

        // 3. ブロックを消去
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    // 送風率の取得
    public int getAirRate() {
        return this.airRate;
    }

    // 送風率の設定（同期処理付き）
    public void setAirRate(int rate) {
        this.airRate = rate;
        // データの変更をマーク（保存用）
        this.setChanged();
        // サーバーからクライアントへパケットを飛ばして同期する（重要！）
        if (this.level != null && !this.level.isClientSide) {
            level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // 燃料残量の取得
    public int getFuel() {
        return this.fuel;
    }

    // 現在の温度の取得
    public float getTemp() {
        return this.currentTemp;
    }

    // タール残量の取得
    public int getTarAmount() {
        return this.tarAmount;
    }

    // 各出力タンクの取得（ステータス表示用にあると便利）
    public int getOutput1() { return this.output1; }
    public int getOutput2() { return this.output2; }
    public int getOutput3() { return this.output3; }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("airRate", this.airRate);
        nbt.putFloat("currentTemp", this.currentTemp);
        nbt.putInt("fuel", this.fuel);
        nbt.putInt("tarAmount", this.tarAmount);
        nbt.putInt("output1", this.output1);
        nbt.putInt("output2", this.output2);
        nbt.putInt("output3", this.output3);
        nbt.putInt("dangerLevel", this.dangerLevel);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.airRate = nbt.getInt("airRate");
        this.currentTemp = nbt.getFloat("currentTemp");
        this.fuel = nbt.getInt("fuel");
        this.tarAmount = nbt.getInt("tarAmount");
        this.output1 = nbt.getInt("output1");
        this.output2 = nbt.getInt("output2");
        this.output3 = nbt.getInt("output3");
        this.dangerLevel = nbt.getInt("dangerLevel");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

//Output 1 (100 ~ 180°C): ベンゼン（ゴムの道）
//Output 2 (181 ~ 250°C): ナフサ（燃料の道）
//Output 3 (251 ~ 350°C): 廃液（道路の道）