package com.bassstarling.ultimatecraftv2.datagen;

import com.bassstarling.ultimatecraftv2.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        // "ultimatecraftv2" は自分のMOD IDに変えてください
        super(output, "ultimatecraftv2", existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (Map.Entry<ModItems.MoldType, RegistryObject<Item>> entry : ModItems.MOLDS.entrySet()) {
            String name = entry.getValue().getId().getPath();

            // 存在チェックを完全に無視するために、直接Builderを操作します
            this.getBuilder(name)
                    .parent(getExistingFile(new ResourceLocation("item/generated")))
                    // ここで getExistingFile を使わず ResourceLocation を直接指定するのがコツです
                    .texture("layer0", new ResourceLocation("ultimatecraftv2", "item/" + name));
        }
    }
}
