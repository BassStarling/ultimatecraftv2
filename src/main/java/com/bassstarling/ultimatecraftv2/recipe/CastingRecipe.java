package com.bassstarling.ultimatecraftv2.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CastingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient inputA;
    private final Ingredient inputB;
    private final ItemStack output;

    public CastingRecipe(ResourceLocation id, Ingredient inputA, Ingredient inputB, ItemStack output) {
        this.id = id;
        this.inputA = inputA;
        this.inputB = inputB;
        this.output = output;
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        ItemStack slot0 = pInv.getItem(0);
        ItemStack slot1 = pInv.getItem(1);

        // inputAがスロット0 かつ inputBがスロット1
        // または
        // inputAがスロット1 かつ inputBがスロット0 (順不同に対応)
        return (inputA.test(slot0) && inputB.test(slot1)) ||
                (inputA.test(slot1) && inputB.test(slot0));
    }

    @Override
    public ItemStack assemble(Container pInv, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return output; }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipes.CASTING_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipes.CASTING_TYPE.get(); }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.inputA);
        list.add(this.inputB);
        return list;
    }

    public static class Type implements RecipeType<CastingRecipe> {
        public static final CastingRecipe.Type INSTANCE = new CastingRecipe.Type();
        public static final String ID = "casting_crafting";
    }

    public static class Serializer implements RecipeSerializer<CastingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation("ultimatecraftv2", "casting_crafting");

        @Override
        public CastingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            // "inputA" という名前の項目を読み取る
            Ingredient inputA = Ingredient.fromJson(pSerializedRecipe.get("inputA"));
            // "inputB" という名前の項目を読み取る
            Ingredient inputB = Ingredient.fromJson(pSerializedRecipe.get("inputB"));

            // "output" を読み取る
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            return new CastingRecipe(pRecipeId, inputA, inputB, output);
        }

        @Override
        public @Nullable CastingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            // サーバーからクライアントへ送られたデータを読み取る
            Ingredient inputA = Ingredient.fromNetwork(pBuffer);
            Ingredient inputB = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();

            return new CastingRecipe(pRecipeId, inputA, inputB, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CastingRecipe pRecipe) {
            // クライアントへデータを送る
            pRecipe.inputA.toNetwork(pBuffer);
            pRecipe.inputB.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.output);
        }
    }
}