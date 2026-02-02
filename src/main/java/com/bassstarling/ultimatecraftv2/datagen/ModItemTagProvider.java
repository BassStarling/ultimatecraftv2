package com.bassstarling.ultimatecraftv2.datagen;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    // タグの定義（他から参照できるように public static にします）
    public static final TagKey<Item> MOLD_TAG = ItemTags.create(new ResourceLocation("ultimatecraftv2", "mold"));

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, "ultimatecraftv2", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        var tagAppender = this.tag(MOLD_TAG);
        for (RegistryObject<Item> moldItem : ModItems.MOLDS.values()) {
            tagAppender.add(moldItem.get());
        }
    }
}
