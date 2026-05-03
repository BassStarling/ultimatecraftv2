package com.bassstarling.ultimatecraftv2.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import net.minecraft.Util;

import java.util.EnumMap;

public enum ModArmorMaterials implements ArmorMaterial {
    // 列挙型の定義: HAZMAT
    HAZMAT("hazmat",
            15, // 耐久値の倍率 (革より少し丈夫なくらい)
            Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                // 各部位の物理防御力 (防弾チョッキではないので低めに設定)
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.HELMET, 1);
            }),
            9, // エンチャントのしやすさ (ゴム素材なので低め)
            SoundEvents.ARMOR_EQUIP_LEATHER, // 装備時の音 (革のバサッという音を流用)
            0.0F, // タフネス (追加のダメージ軽減なし)
            0.0F, // ノックバック耐性なし
            // 修理素材 (遅延評価にするため Supplier を使用)
            // ModItems.RUBBER はご自身で追加したゴム系のアイテム名に置き換えてください
            () -> Ingredient.of(ModItems.RUBBER_BALL.get())),

    DUST("dust_mask",
                 5, // 耐久倍率: かなり低め（簡易的なので）
         Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 0);      // マスク用なので他は0でOK
        map.put(ArmorItem.Type.LEGGINGS, 0);
        map.put(ArmorItem.Type.CHESTPLATE, 0);
        map.put(ArmorItem.Type.HELMET, 1);     // マスク本体の防御力
    }),
            15, // エンチャントのしやすさ: 布製なので高め
    SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F, 0.0F,
            // 修理素材: 不織布などで修理できるように設定
            () -> Ingredient.of(ModItems.NON_WOVEN_FABRIC.get()));

    // バニラ準拠の部位ごとの基本耐久値マップ
    // この値に上記の耐久値倍率(15)を掛けたものが実際の耐久値になります
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
    });

    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionFunctionForType, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        // 重要: ModIDをプレフィックスにつけることで、自作テクスチャを正しく参照させます
        this.name = "ultimatecraftv2:" + name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionFunctionForType = protectionFunctionForType;
        this.enchantmentValue = enchantmentValue;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.protectionFunctionForType.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}