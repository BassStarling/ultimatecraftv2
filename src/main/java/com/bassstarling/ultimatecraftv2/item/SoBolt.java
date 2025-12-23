package com.bassstarling.ultimatecraftv2.item;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SoBolt extends Item {
    public SoBolt(Properties p_41383_) {
        super(p_41383_);
    }
    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level,
                                List<Component> tooltip, TooltipFlag flag) {

        if (stack.hasTag()) {
            var tag = stack.getTag();

            if (tag.contains("bolt_type")) {
                tooltip.add(Component.literal(
                        "用途: " + tag.getString("bolt_type")));
            }

            if (tag.contains("material")) {
                tooltip.add(Component.literal(
                        "素材: " + tag.getString("material")));
            }
        }
    }
}
