package com.bassstarling.ultimatecraftv2.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class UniversalElectrolyzerRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final FluidStack inputFluid;
    private final Ingredient inputItem;
    private final FluidStack outputFluid;
    private final ItemStack outputItem;
    private final int processTime;
    private final int energyUsage;

    public UniversalElectrolyzerRecipe(ResourceLocation id, FluidStack inputFluid, Ingredient inputItem,
                                       FluidStack outputFluid, ItemStack outputItem, int processTime, int energyUsage) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.inputItem = inputItem;
        this.outputFluid = outputFluid;
        this.outputItem = outputItem;
        this.processTime = processTime;
        this.energyUsage = energyUsage;
    }

    // レシピが一致するか判定
    public boolean matches(FluidStack fluid, ItemStack stack) {
        return inputFluid.getFluid().isSame(fluid.getFluid()) &&
                inputFluid.getAmount() <= fluid.getAmount() &&
                inputItem.test(stack);
    }

    // Getters
    public FluidStack getInputFluid() { return inputFluid; }
    public FluidStack getOutputFluid() { return outputFluid; }
    public ItemStack getOutputItem() { return outputItem.copy(); }
    public int getProcessTime() { return processTime; }
    public int getEnergyUsage() { return energyUsage; }

    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return Serializer.INSTANCE; }
    @Override public RecipeType<?> getType() { return Type.INSTANCE; }

    // バニラ互換用（今回は使用しないので最小限）
    @Override public boolean matches(SimpleContainer pInv, Level pLevel) { return false; }
    @Override public ItemStack assemble(SimpleContainer pInv, RegistryAccess pRegistryAccess) { return outputItem.copy(); }
    @Override public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }
    @Override public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return outputItem.copy(); }

    // Type & Serializer 登録用
    public static class Type implements RecipeType<UniversalElectrolyzerRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "universalelectrolyzer";
    }

    public static class Serializer implements RecipeSerializer<UniversalElectrolyzerRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation("ultimatecraftv2", "universal_electrolytic");

        @Override
        public UniversalElectrolyzerRecipe fromJson(ResourceLocation id, JsonObject json) {
            // Forge標準の読み込み方法
            FluidStack inputFluid = deserializeFluid(json.get("inputFluid").getAsJsonObject());
            Ingredient inputItem = Ingredient.fromJson(json.get("inputItem"));
            FluidStack outputFluid = deserializeFluid(json.get("outputFluid").getAsJsonObject());
            ItemStack outputItem = ShapedRecipe.itemStackFromJson(json.get("outputItem").getAsJsonObject());

            int time = json.get("processTime").getAsInt();
            int energy = json.get("energyUsage").getAsInt();

            return new UniversalElectrolyzerRecipe(id, inputFluid, inputItem, outputFluid, outputItem, time, energy);
        }

        // ヘルパーメソッド（JSONからFluidStackを作る）
        private static FluidStack deserializeFluid(JsonObject json) {
            ResourceLocation fluidId = new ResourceLocation(json.get("fluid").getAsString());
            int amount = json.get("amount").getAsInt();
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
            return new FluidStack(fluid, amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, UniversalElectrolyzerRecipe recipe) {
            recipe.inputFluid.writeToPacket(buf);
            recipe.inputItem.toNetwork(buf);
            recipe.outputFluid.writeToPacket(buf);
            buf.writeItem(recipe.outputItem);
            buf.writeInt(recipe.processTime);
            buf.writeInt(recipe.energyUsage);
        }

        @Override
        public UniversalElectrolyzerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidStack inputFluid = FluidStack.readFromPacket(buf);
            Ingredient inputItem = Ingredient.fromNetwork(buf);
            FluidStack outputFluid = FluidStack.readFromPacket(buf);
            ItemStack outputItem = buf.readItem();
            return new UniversalElectrolyzerRecipe(id, inputFluid, inputItem, outputFluid, outputItem, buf.readInt(), buf.readInt());
        }
    }
}