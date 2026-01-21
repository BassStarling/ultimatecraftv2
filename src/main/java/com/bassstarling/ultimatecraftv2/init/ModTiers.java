package com.bassstarling.ultimatecraftv2.init;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class ModTiers {
    // 鉄パイプ用のカスタム素材定義
    public static final Tier PIPE = new ForgeTier(
            2,                   // 1. 採掘レベル (2=鉄相当)
            500,                 // 2. 耐久値 (鉄は250, ダイヤは1561)
            6.0f,                // 3. 採掘速度
            2.0f,                // 4. 攻撃力ボーナス (素材自体の基礎攻撃力)
            14,                  // 5. エンチャント補正 (高いほど良いエンチャントが出やすい)
            BlockTags.NEEDS_STONE_TOOL, // 6. 破壊可能なブロックのタグ
            () -> Ingredient.of(ModItems.ALUMINIUM_INGOT.get()) // 7. 修理に使うアイテム
    );
}