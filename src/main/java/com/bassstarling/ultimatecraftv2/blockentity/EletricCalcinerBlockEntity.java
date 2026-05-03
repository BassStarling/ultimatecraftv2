package com.bassstarling.ultimatecraftv2.blockentity;

import com.bassstarling.ultimatecraftv2.item.SparkStone;
import com.bassstarling.ultimatecraftv2.registry.ModArmorMaterials;
import com.bassstarling.ultimatecraftv2.registry.ModBlockEntities;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EletricCalcinerBlockEntity extends BlockEntity {

    public EletricCalcinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELETRIC_CALCINER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EletricCalcinerBlockEntity be) {
        if (level.isClientSide) return;

        // 判定範囲（ブロックの直上 0.5マスの高さ）
        AABB box = new AABB(
                pos.getX(), pos.getY() + 1, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1.5, pos.getZ() + 1
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);

        ItemEntity sparkStoneEntity = null;
        ItemEntity inputEntity = null;
        ItemEntity sulfurEntity = null;
        ItemStack resultStack = ItemStack.EMPTY;

        // 1. スパークストーンの判定 (Tier 4 以上が必要な高熱処理と想定)
        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.is(ModItems.SPARK_STONE.get()) && SparkStone.getTier(stack) >= 4) {
                sparkStoneEntity = item;
                break;
            }
        }

        if (sparkStoneEntity == null) return;

        // 2. 入力アイテムの判定
        for (ItemEntity item : items) {
            if (item == sparkStoneEntity) continue;

            ItemStack stack = item.getItem();

            // パターンA: 洗浄済みボーキサイト粉末 → アルミナ
            if (stack.is(ModItems.WASHED_BAUXITE_POWDER.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.ALUMINA.get());
                break;
            }
            // パターンB: 泡状アルミナ → 多孔質断熱ブロック
            else if (stack.is(ModItems.FOAMED_ALUMINA.get().asItem())) {
                inputEntity = item;
                resultStack = new ItemStack(ModBlocks.POROUS_INSULATION_BLOCK.get().asItem());
                break;
            }
            // パターンC: 耐火粘土 → 耐火煉瓦
            else if (stack.is(ModItems.FIRE_CLAY_BALL.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.FIREBRICK.get());
                break;
            }
            // パターンD: 水酸化アルミニウム(白色綿毛状固体) → アルミナ
            else if (stack.is(ModItems.WHITE_FLUFFY_SOLID_OF_ALUMINIUM_HYDROXIDE.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.ALUMINA.get());
                break;
            }
            // パターンE: 未加硫ゴムブロック+硫黄の粉 → ゴムブロック
            else if (stack.is(ModBlocks.RAW_RUBBER_BLOCK.get().asItem())) {
                // 未加硫ゴムを見つけたら、同じ範囲内に硫黄があるか探す
                for (ItemEntity secondItem : items) {
                    if (secondItem.getItem().is(ModItems.SULFUR_DUST.get())) {
                        inputEntity = item;       // 未加硫ゴム
                        sulfurEntity = secondItem; // 硫黄粉末
                        resultStack = new ItemStack(ModBlocks.RUBBER_BLOCK.get());
                        break;
                    }
                }
                if (inputEntity != null) break;
            }
            // パターンF: 木炭の粉 → 活性炭
            else if (stack.is(ModItems.CHARCOAL_DUST.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.ACTIVATED_CARBON.get());
                break;
            }
            // パターンG: 鉄の容器+辰砂 → 水銀入り容器+硫黄
            else if (stack.is(ModItems.CINNABAR.get())) {
                // 鉄の容器が一緒に落ちているか確認
                ItemEntity flaskEntity = null;
                for (ItemEntity secondItem : items) {
                    if (secondItem.getItem().is(ModItems.IRON_CONTAINER.get())) {
                        flaskEntity = secondItem;
                        break;
                    }
                }

                // 容器がない場合は精錬が始まらない（または蒸気が漏れるだけにする）
                if (flaskEntity != null) {
                    inputEntity = item;           // 辰砂
                    sulfurEntity = flaskEntity;   // 便宜上 sulfurEntity 変数を「2つ目の消費アイテム」として流用
                    resultStack = new ItemStack(ModItems.MERCURY_CONTAINER.get());

                    // 副産物の硫黄を生成
                    ItemStack sulfurDust = new ItemStack(ModItems.SULFUR_DUST.get());
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, sulfurDust));

                    // 周囲への毒性（蒸気漏れ）判定
                    applyMercuryVaporEffect(level, pos);

                    break;
                }
            }
            // パターンH: 硫黄粉末 → 二酸化硫黄 (完全燃焼)
            else if (stack.is(ModItems.SULFUR_DUST.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.SULFUR_DIOXIDE_DUST.get());
                break;
            }
            // パターンI: タングステン酸入りバケツ → 酸化タングステン入りバケツ (分解)
            else if (stack.is(ModItems.TUNGSTIC_ACID_BUCKET.get())) {
                inputEntity = item;
                resultStack = new ItemStack(ModItems.TUNGSTEN_OXIDE_BUCKET.get());
                break;
            }
            // パターンJ: 酸化タングステン入りバケツ + コークス粉末 → タングステン入りバケツ (還元)
            else if (stack.is(ModItems.TUNGSTEN_OXIDE_BUCKET.get())) {
                // コークス(炭素)が一緒に落ちているか確認
                for (ItemEntity secondItem : items) {
                    // コークスの粉末アイテムを想定
                    if (secondItem.getItem().is(ModItems.COKE_DUST.get())) {
                        inputEntity = item;           // 酸化タングステンバケツ
                        sulfurEntity = secondItem;     // コークス（変数sulfurEntityを第2材料として流用）
                        resultStack = new ItemStack(ModItems.TUNGSTEN_BUCKET.get());
                        break;
                    }
                }
                if (inputEntity != null) break;
            }
        }

        // 3. 処理実行
        if (inputEntity != null && !resultStack.isEmpty()) {
            // 消費処理
            sparkStoneEntity.getItem().shrink(1);
            inputEntity.getItem().shrink(1);

            if (sparkStoneEntity.getItem().isEmpty()) sparkStoneEntity.discard();
            if (inputEntity.getItem().isEmpty()) inputEntity.discard();

            if (sulfurEntity != null) {
                sulfurEntity.getItem().shrink(1);
                if (sulfurEntity.getItem().isEmpty()) sulfurEntity.discard();
            }

            if (sparkStoneEntity.getItem().isEmpty()) sparkStoneEntity.discard();
            if (inputEntity.getItem().isEmpty()) inputEntity.discard();

            // 成果物のドロップ
            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 1.1,
                    pos.getZ() + 0.5,
                    resultStack
            ));

            // 演出
            level.playSound(null, pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.8F, 1.0F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05);
                // 精錬中っぽい煙も追加
                serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 2, 0.05, 0.05, 0.05, 0.02);
            }
        }
    }

    private static void applyMercuryVaporEffect(Level level, BlockPos pos) {
        AABB toxicArea = new AABB(pos).inflate(5.0);
        level.getEntitiesOfClass(Player.class, toxicArea).forEach(p -> {
            ItemStack helmet = p.getItemBySlot(EquipmentSlot.HEAD);
            boolean hasGasMask = !helmet.isEmpty() &&
                    helmet.getItem() instanceof ArmorItem armor &&
                    armor.getMaterial() == ModArmorMaterials.HAZMAT;
            if (!hasGasMask) {
                p.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0));
            }
        });
    }
}