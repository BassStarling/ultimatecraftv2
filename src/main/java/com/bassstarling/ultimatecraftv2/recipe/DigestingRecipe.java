package com.bassstarling.ultimatecraftv2.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DigestingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack inputItem;
    private final FluidStack inputFluid;
    private final FluidStack outputFluid;
    private final int cookTime;

    public DigestingRecipe(ResourceLocation id, ItemStack inputItem, FluidStack inputFluid, FluidStack outputFluid, int cookTime) {
        this.id = id;
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.cookTime = cookTime;
    }

    // レシピが一致するか判定（BlockEntityから呼ばれる）
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if(pLevel.isClientSide()) return false;
        return inputItem.getItem() == pContainer.getItem(0).getItem();
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY; // 液体がメイン出力なので、アイテム出力は空
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() { return Serializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    // --- レシピタイプの定義 ---
    public static class Type implements RecipeType<DigestingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "digesting";
    }

    // --- シリアライザー (JSONとJavaコードの橋渡し) ---
    // --- シリアライザー ---
    public static class Serializer implements RecipeSerializer<DigestingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation("ultimatecraftv2", "digesting");

        @Override
        public DigestingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack inputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));

            JsonObject fluidInJson = GsonHelper.getAsJsonObject(pSerializedRecipe, "fluid_input");
            FluidStack inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidInJson.get("fluid").getAsString())),
                    fluidInJson.get("amount").getAsInt());

            JsonObject fluidOutJson = GsonHelper.getAsJsonObject(pSerializedRecipe, "output");
            FluidStack outputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidOutJson.get("fluid").getAsString())),
                    fluidOutJson.get("amount").getAsInt());

            int cookTime = GsonHelper.getAsInt(pSerializedRecipe, "time");

            return new DigestingRecipe(pRecipeId, inputItem, inputFluid, outputFluid, cookTime);
        }

        @Override
        public @Nullable DigestingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            // ネットワークから読み込む順番は toNetwork と合わせる
            ItemStack inputItem = buf.readItem();
            FluidStack inputFluid = buf.readFluidStack();
            FluidStack outputFluid = buf.readFluidStack();
            int cookTime = buf.readInt();

            return new DigestingRecipe(id, inputItem, inputFluid, outputFluid, cookTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DigestingRecipe recipe) {
            // ネットワークへ書き込む
            buf.writeItem(recipe.inputItem);
            buf.writeFluidStack(recipe.inputFluid);
            buf.writeFluidStack(recipe.outputFluid);
            buf.writeInt(recipe.cookTime);
        }
    }

    // Getter
    public ItemStack getInputItem() { return inputItem; }
    public FluidStack getInputFluid() { return inputFluid; }
    public FluidStack getOutputFluid() { return outputFluid; }
    public int getCookTime() { return cookTime; }
}