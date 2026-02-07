package com.bassstarling.ultimatecraftv2.compat.rei;

import com.bassstarling.ultimatecraftv2.fluid.ModFluids;
import com.bassstarling.ultimatecraftv2.recipe.CastingRecipe;
import com.bassstarling.ultimatecraftv2.recipe.IndustrialRecipe;
import com.bassstarling.ultimatecraftv2.recipe.ModRecipes;
import com.bassstarling.ultimatecraftv2.registry.ModBlocks;
import com.bassstarling.ultimatecraftv2.registry.ModItems;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@EmiEntrypoint
public class IndustrialEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory CRUSHER_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "crusher"),
                    EmiStack.of(ModBlocks.CRUSHER_BLOCK.get()));

    public static final EmiRecipeCategory INDUSTRIAL_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "industrial_crafting"),
            EmiStack.of(ModBlocks.INDUSTRIAL_WORKBENCH.get())
    );

    public static final EmiRecipeCategory CASTING_CRAFTING_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "casting_crafting"),
            EmiStack.of(ModBlocks.CASTING_MACHINE.get())
    );

    public static final EmiRecipeCategory OXYGEN_CONVERTER_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "converter"),
            EmiStack.of(ModBlocks.OXYGEN_CONVERTER.get())
    );

    public static final EmiRecipeCategory COKE_OVEN_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "coke_oven"),
            EmiStack.of(ModBlocks.COKEOVEN.get())
    );

    public static final EmiRecipeCategory ELECTROLYZER_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("ultimatecraftv2", "electrolyzer.json"),
                    EmiStack.of(ModBlocks.ELECTROLYZER.get()));

    public static final EmiRecipeCategory ARC_FURNACE_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "arc_furnace"),
                    EmiStack.of(ModBlocks.ARC_FURNACE.get()));

    public static final EmiRecipeCategory WASHING_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "washing"),
                    EmiStack.of(ModBlocks.WASHING_MACHINE.get()));

    public static final EmiRecipeCategory CALCINER_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "calciner"),
                    EmiStack.of(ModBlocks.ELETRICCALCINER.get()));

    public static final EmiRecipeCategory ELECTROLYTIC_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "electrolytic"),
                    EmiStack.of(ModBlocks.ELECTROLYTICFURNACE.get()));

    public static final EmiRecipeCategory CASTING_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "casting"),
                    EmiStack.of(ModBlocks.CASTING_MACHINE.get()));

    public static final EmiRecipeCategory CHEMICAL_CATEGORY =
            new EmiRecipeCategory(new ResourceLocation("ultimatecraftv2", "chemical_reaction"),
                    EmiStack.of(ModItems.QUICK_LIME.get())); // カテゴリアイコンを生石灰に

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(OXYGEN_CONVERTER_CATEGORY);
        registry.addWorkstation(OXYGEN_CONVERTER_CATEGORY, EmiStack.of(ModBlocks.OXYGEN_CONVERTER.get()));

        registry.addRecipe(new OxygenConverterEmiRecipe());

        registry.addCategory(INDUSTRIAL_CATEGORY);

        registry.addWorkstation(INDUSTRIAL_CATEGORY, EmiStack.of(ModBlocks.INDUSTRIAL_WORKBENCH.get()));

        RecipeManager manager = registry.getRecipeManager();
        for (IndustrialRecipe recipe : manager.getAllRecipesFor(ModRecipes.INDUSTRIAL_TYPE.get())) {
            registry.addRecipe(new IndustrialEmiRecipe(recipe));
        }

        registry.addCategory(CASTING_CRAFTING_CATEGORY);

        registry.addWorkstation(CASTING_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.CASTING_MACHINE.get()));

        for (CastingRecipe recipe : manager.getAllRecipesFor(ModRecipes.CASTING_TYPE.get())) {
            registry.addRecipe(new CastingEmiRecipe(recipe));
        }

        registry.addCategory(COKE_OVEN_CATEGORY);

        registry.addWorkstation(COKE_OVEN_CATEGORY, EmiStack.of(ModBlocks.COKEOVEN.get()));

        // 通常レシピ
        registry.addRecipe(new CokeOvenEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "coke_oven/coal_coke"),
                EmiIngredient.of(Ingredient.of(Items.COAL)),
                EmiStack.of(ModItems.COKE.get()),
                EmiStack.of(ModFluids.SOURCE_TAR.get(), 250),
                false // isBucket = false
        ));

// バケツレシピ
        registry.addRecipe(new CokeOvenEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "coke_oven/tar_bucket"),
                EmiIngredient.of(Ingredient.of(Items.COAL), 4),
                EmiStack.of(ModItems.COKE.get(), 4),
                EmiStack.of(ModFluids.SOURCE_TAR.get(), 1000), // 1000mB
                true // isBucket = true
        ));

        registry.addCategory(ELECTROLYZER_CATEGORY);
        registry.addWorkstation(ELECTROLYZER_CATEGORY, EmiStack.of(ModBlocks.ELECTROLYZER.get()));

        // レシピ登録
        registry.addRecipe(new ElectrolyzerEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "electrolyzer.json/oxygen_bottle")
        ));

        registry.addCategory(ARC_FURNACE_CATEGORY);
        registry.addWorkstation(ARC_FURNACE_CATEGORY, EmiStack.of(ModBlocks.ARC_FURNACE.get()));

        // レシピの登録
        registry.addRecipe(new ArcFurnaceEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "arc_furnace/graphite_electrode")
        ));

        registry.addRecipe(new CokeOvenEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "coke_oven/coke_electrode"),
                EmiStack.of(ModItems.UNFIRED_ELECTRODE.get()), // 入力
                EmiStack.of(ModItems.COKE_ELECTRODE.get()),    // 出力アイテム
                EmiStack.EMPTY,                               // 出力液体 (ここをEMPTYに!)
                false                                         // バケツモードOFF
        ));

        registry.addCategory(CRUSHER_CATEGORY);
        registry.addWorkstation(CRUSHER_CATEGORY, EmiStack.of(ModBlocks.CRUSHER_BLOCK.get()));

        // 1. ボーキサイトの粉砕
        registry.addRecipe(new CrusherEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "crusher/bauxite_powder"),
                EmiStack.of(ModItems.RAW_BAUXITE.get()),
                EmiStack.of(ModItems.COARSE_BAUXITE_POWDER.get())
        ));

        // 2. コークスの粉砕（今回追加分）
        registry.addRecipe(new CrusherEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "crusher/coke_dust"),
                EmiStack.of(ModItems.COKE.get()),
                EmiStack.of(ModItems.COKE_DUST.get())
        ));

        registry.addCategory(WASHING_CATEGORY);
        registry.addWorkstation(WASHING_CATEGORY, EmiStack.of(ModBlocks.WASHING_MACHINE.get()));

        // 1. ボーキサイト粉の洗浄
        registry.addRecipe(new WashingMachineEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "washing/washed_bauxite_powder"),
                EmiStack.of(ModItems.COARSE_BAUXITE_POWDER.get()),
                EmiStack.of(ModItems.WASHED_BAUXITE_POWDER.get())
        ));

        // 2. アルミナの洗浄（高純度化）
        registry.addRecipe(new WashingMachineEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "washing/high_purity_alumina"),
                EmiStack.of(ModItems.ALUMINA.get()),
                EmiStack.of(ModItems.HIGH_PURITY_ALUMINA.get())
        ));

        registry.addCategory(CALCINER_CATEGORY);
        registry.addWorkstation(CALCINER_CATEGORY, EmiStack.of(ModBlocks.ELETRICCALCINER.get()));

        // アルミナの仮焼レシピ
        registry.addRecipe(new ElectricCalcinerEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "calciner/alumina"),
                EmiStack.of(ModItems.WASHED_BAUXITE_POWDER.get()),
                EmiStack.of(ModItems.ALUMINA.get())
        ));

        registry.addCategory(ELECTROLYTIC_CATEGORY);
        registry.addWorkstation(ELECTROLYTIC_CATEGORY, EmiStack.of(ModBlocks.ELECTROLYTICFURNACE.get()));

        // 電解精錬レシピの登録
        registry.addRecipe(new ElectrolyticFurnaceEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "electrolytic/aluminum_smelting")
        ));

        registry.addRecipe(new MoltenAluminumExtractionEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "electrolytic/collect_aluminum")
        ));

        registry.addCategory(CASTING_CATEGORY);
        registry.addWorkstation(CASTING_CATEGORY, EmiStack.of(ModBlocks.CASTING_MACHINE.get()));

        // アルミニウムの鋳造レシピ
        registry.addRecipe(new CastingMachineEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "casting/aluminum_ingot")
        ));

        registry.addCategory(CHEMICAL_CATEGORY);

        // レシピ登録
        registry.addRecipe(new SlakedLimeEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "chemical/slaked_lime")
        ));

        registry.addRecipe(new ElectrolyzerEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "electrolysis/sodium_hydroxide"),
                EmiStack.of(ModItems.BRINE_BOTTLE.get()),
                EmiStack.of(ModItems.SPARK_STONE.get()), // ここでティア4であることをアイコン等で示すと親切です
                EmiStack.of(ModItems.SODIUM_HYDROXIDE_SOLUTION_BOTTLE.get())
        ));

        registry.addRecipe(new EmiCraftingRecipe(
                List.of(EmiStack.of(ModItems.SODIUM_HYDROXIDE_SOLUTION_BOTTLE.get())),
                EmiStack.of(ModItems.SODIUM_BICARBONATE_SOLUTION_BOTTLE.get()),
                new ResourceLocation("ultimatecraftv2", "chemical/carbonation"),
                false // シャッフルなし
        ) {
            @Override
            public void addWidgets(WidgetHolder widgets) {
                super.addWidgets(widgets);
                // 右クリックが必要であることをアイコンやテキストで補足
                widgets.addText(Component.literal("右クリックで空気と反応"), 0, 0, 0xAAAAAA, false);
            }
        });

        registry.addRecipe(new ElectrolyzerEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "calciner/porous_insulation"),
                EmiStack.of(ModItems.FOAMED_ALUMINA.get()),
                EmiStack.of(ModItems.SPARK_STONE.get()),
                EmiStack.of(ModBlocks.POROUS_INSULATION_BLOCK.get())
        ));

        registry.addRecipe(new CokeOvenEmiRecipe(
                new ResourceLocation("ultimatecraftv2", "coke_oven/nickel_smelting"), // id
                EmiStack.of(ModItems.RAW_NICKEL.get()),                             // input
                EmiStack.of(ModItems.NICKEL_INGOT.get()),                           // resultItem
                EmiStack.of(ModFluids.SOURCE_TAR.get(), 0),                       // resultFluid
                false                                                               // isBucket (boolean)
        ));

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModBlocks.INDUSTRIAL_WORKBENCH.get()));
    }
}