package com.bassstarling.ultimatecraftv2.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class DisposableArcFurnaceBlockItem extends BlockItem {
    public DisposableArcFurnaceBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        // NBTに "BlockStateTag"（loot tableのcopy_stateで入る場所）
        // または直接の "spent" タグがあるか確認
        boolean isSpent = false;
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            // ルートテーブルの copy_state 関数を使った場合は BlockStateTag の中に入る
            if (tag.contains("BlockStateTag") && tag.getCompound("BlockStateTag").getString("spent").equals("true")) {
                isSpent = true;
            }
            // 手動で付与したタグも一応チェック
            else if (tag.getBoolean("spent")) {
                isSpent = true;
            }
        }

        if (isSpent) {
            // 「(使用済み)」というテキストを付加、または専用の翻訳キーを返す
            return Component.translatable(this.getDescriptionId(stack))
                    .append(Component.literal(" "))
                    .append(Component.translatable("tooltip.ultimatecraftv2.spent").withStyle(ChatFormatting.RED));
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag() && stack.getTag().contains("BlockStateTag")) {
            if (stack.getTag().getCompound("BlockStateTag").getString("spent").equals("true")) {
                tooltip.add(Component.translatable("tooltip.ultimatecraftv2.repair_hint").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}