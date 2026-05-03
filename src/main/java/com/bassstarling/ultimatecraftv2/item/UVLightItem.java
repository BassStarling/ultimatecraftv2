package com.bassstarling.ultimatecraftv2.item;

import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UVLightItem extends Item {
    public static final String ENERGY_TAG = "battery_energy";
    public static final String ON_TAG = "is_on"; // ON/OFF状態管理用

    public UVLightItem(Properties properties) {
        super(properties.stacksTo(1).durability(LithiumIonBatteryItem.MAX_ENERGY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack light = player.getItemInHand(hand);
        ItemStack offhandItem = player.getOffhandItem();
        CompoundTag nbt = light.getOrCreateTag();

        // 1. 電池の取り出し (スニーク + 右クリック)
        if (player.isShiftKeyDown()) {
            if (light.getDamageValue() < light.getMaxDamage()) {
                if (!level.isClientSide) {
                    ItemStack spentBattery = new ItemStack(ModItems.LITHIUM_ION_BATTERY.get());
                    spentBattery.setDamageValue(light.getDamageValue());
                    player.getInventory().add(spentBattery);

                    light.setDamageValue(light.getMaxDamage());
                    nbt.putBoolean(ON_TAG, false); // 電池を抜いたら強制OFF
                    level.playSound(null, player.blockPosition(), SoundEvents.IRON_DOOR_CLOSE, SoundSource.PLAYERS, 0.5f, 2.0f);
                }
                return InteractionResultHolder.sidedSuccess(light, level.isClientSide);
            }
        }

        // 2. 電池の装填 (通常右クリック + 左手に電池 + ライトが空)
        if (offhandItem.getItem() instanceof LithiumIonBatteryItem && light.getDamageValue() >= light.getMaxDamage()) {
            if (!level.isClientSide) {
                light.setDamageValue(offhandItem.getDamageValue());
                offhandItem.shrink(1);
                level.playSound(null, player.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 1.0f, 1.2f);
            }
            return InteractionResultHolder.sidedSuccess(light, level.isClientSide);
        }

        // 3. ライトのON/OFF切り替え (通常右クリック / 電池が入っている時)
        if (light.getDamageValue() < light.getMaxDamage()) {
            boolean currentState = nbt.getBoolean(ON_TAG);
            nbt.putBoolean(ON_TAG, !currentState);

            if (!level.isClientSide) {
                float pitch = !currentState ? 1.5f : 1.0f; // ONは高め、OFFは低めの音
                level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 0.3f, pitch);
            }
            return InteractionResultHolder.sidedSuccess(light, level.isClientSide);
        }

        return InteractionResultHolder.pass(light);
    }

    // UVLightItem.java 内

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player) {
            CompoundTag nbt = stack.getTag();

            // 「ON状態」かつ「エネルギーがある」かつ「手に持っている」場合
            if (nbt != null && nbt.getBoolean(ON_TAG) && stack.getDamageValue() < stack.getMaxDamage() && selected) {

                // 20ticks(1秒)ごとにエネルギー消費とスキャンを実行
                if (level.getGameTime() % 20 == 0) {
                    stack.setDamageValue(stack.getDamageValue() + 1);

                    // バッテリー切れチェック
                    if (stack.getDamageValue() >= stack.getMaxDamage()) {
                        nbt.putBoolean(ON_TAG, false);
                    } else {
                        // 検知ロジック実行
                        searchScheelite(player, level);
                    }
                }
            }
        }
    }

    private void searchScheelite(Player player, Level level) {
        BlockPos center = player.blockPosition();
        int range = 32;

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            if (level.getBlockState(pos).is(ModBlocks.SCHEELITE_ORE.get())) {
                // ブロックの各面に対してパーティクルを出す
                for (Direction direction : Direction.values()) {
                    // その面が空気などに接している場合のみ出すとより負荷が低い
                    if (!level.getBlockState(pos.relative(direction)).isSolidRender(level, pos.relative(direction))) {
                        double px = pos.getX() + 0.5 + direction.getStepX() * 0.55;
                        double py = pos.getY() + 0.5 + direction.getStepY() * 0.55;
                        double pz = pos.getZ() + 0.5 + direction.getStepZ() * 0.55;

                        ((ServerLevel) level).sendParticles(
                                ParticleTypes.ELECTRIC_SPARK,
                                px, py, pz,
                                2, // 個数は少なめでOK
                                0.1, 0.1, 0.1,
                                0.02
                        );
                    }
                }
            }
        }
    }

    // アイテムが光っている（エンチャントのようなエフェクト）かどうかでON/OFFを視覚化
    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(ON_TAG);
    }
}