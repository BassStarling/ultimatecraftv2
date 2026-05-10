package com.bassstarling.ultimatecraftv2.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.logging.Level;

public class RecrystallizingRecipe implements Recipe<SimpleContainer>{
    private final ResourceLocation id;
    private final Ingredient seedIngredient;
    private final FluidStack inputFluid;
    private final int processTime;
    private final boolean needsSpark;
    private final boolean needsCooling;
    private final int idealFlow;
    private final int idealPower;
    private final float targetTemp;
    private final ItemStack outputItem;
    private final FluidStack outputFluid;

    public RecrystallizingRecipe(ResourceLocation id, Ingredient seedIngredient, FluidStack inputFluid,
                                 int processTime, boolean needsSpark, boolean needsCooling,
                                 int idealFlow, int idealPower, float targetTemp,
                                 ItemStack outputItem, FluidStack outputFluid) {
        this.id = id;
        this.seedIngredient = seedIngredient;
        this.inputFluid = inputFluid;
        this.processTime = processTime;
        this.needsSpark = needsSpark;
        this.needsCooling = needsCooling;
        this.idealFlow = idealFlow;
        this.idealPower = idealPower;
        this.targetTemp = targetTemp;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
    }

    // --- Getters ---
    @Override public ResourceLocation getId() { return id; }
    public Ingredient getSeedIngredient() { return seedIngredient; }
    public FluidStack getInputFluid() { return inputFluid; }
    public int getProcessTime() { return processTime; }
    public boolean needsSpark() { return needsSpark; }
    public boolean needsCooling() { return needsCooling; }
    public int getIdealFlow() { return idealFlow; }
    public int getIdealPower() { return idealPower; }
    public float getTargetTemperature() { return targetTemp; }
    public ItemStack getOutputItem() { return outputItem; }
    public FluidStack getOutputFluid() { return outputFluid; }

    @Override
    public boolean matches(SimpleContainer inv, net.minecraft.world.level.Level p_44003_) {
        return seedIngredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access) {
        // クラフト結果を返す
        return outputItem.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        // クリエイティブタブやJEI表示用
        return outputItem;
    }

    @Override public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }

    @Override public RecipeSerializer<?> getSerializer() { return ModRecipes.RECRYSTALLIZING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return ModRecipes.RECRYSTALLIZING_TYPE.get(); }

    public static class Type implements RecipeType<RecrystallizingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "recrystallizing";

        @Override
        public String toString() {
            return ID;
        }
    }

    // --- Serializer ---
    public static class Serializer implements RecipeSerializer<RecrystallizingRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public RecrystallizingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient seed = Ingredient.fromJson(json.get("ingredient"));

            // 流体入力の読み込み
            JsonObject fluidInJson = GsonHelper.getAsJsonObject(json, "fluid_input");
            FluidStack fluidIn = deserializeFluid(fluidInJson);

            int time = GsonHelper.getAsInt(json, "process_time");
            boolean spark = GsonHelper.getAsBoolean(json, "needs_spark");
            boolean cooling = GsonHelper.getAsBoolean(json, "needs_cooling");

            // 手探り要素のパラメータ
            int flow = json.has("ideal_flow") ? GsonHelper.getAsInt(json, "ideal_flow") : 0;
            int power = json.has("ideal_power") ? GsonHelper.getAsInt(json, "ideal_power") : 0;
            float temp = json.has("target_temp") ? GsonHelper.getAsFloat(json, "target_temp") : 20.0f;

            // 出力
            ItemStack resultItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output_item"));
            JsonObject fluidOutJson = GsonHelper.getAsJsonObject(json, "output_fluid");
            FluidStack fluidOut = deserializeFluid(fluidOutJson);

            return new RecrystallizingRecipe(recipeId, seed, fluidIn, time, spark, cooling, flow, power, temp, resultItem, fluidOut);
        }

        @Nullable
        @Override
        public RecrystallizingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient seed = Ingredient.fromNetwork(buffer);
            FluidStack fluidIn = buffer.readFluidStack();
            int time = buffer.readInt();
            boolean spark = buffer.readBoolean();
            boolean cooling = buffer.readBoolean();
            int flow = buffer.readInt();
            int power = buffer.readInt();
            float temp = buffer.readFloat();
            ItemStack resultItem = buffer.readItem();
            FluidStack fluidOut = buffer.readFluidStack();

            return new RecrystallizingRecipe(recipeId, seed, fluidIn, time, spark, cooling, flow, power, temp, resultItem, fluidOut);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RecrystallizingRecipe recipe) {
            recipe.seedIngredient.toNetwork(buffer);
            buffer.writeFluidStack(recipe.inputFluid);
            buffer.writeInt(recipe.processTime);
            buffer.writeBoolean(recipe.needsSpark);
            buffer.writeBoolean(recipe.needsCooling);
            buffer.writeInt(recipe.idealFlow);
            buffer.writeInt(recipe.idealPower);
            buffer.writeFloat(recipe.targetTemp);
            buffer.writeItem(recipe.outputItem);
            buffer.writeFluidStack(recipe.outputFluid);
        }

        // 流体パース補助用
        private FluidStack deserializeFluid(JsonObject json) {
            ResourceLocation fluidName = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            int amount = GsonHelper.getAsInt(json, "amount");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
            if (fluid == null || fluid == Fluids.EMPTY) {
                throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");
            }
            return new FluidStack(fluid, amount);
        }
    }
}