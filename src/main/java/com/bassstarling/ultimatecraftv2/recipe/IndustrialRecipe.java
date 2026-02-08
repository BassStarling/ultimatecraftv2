package com.bassstarling.ultimatecraftv2.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class IndustrialRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public IndustrialRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        // 1. 盤面(5x5)の「実際にアイテムがある範囲」を特定
        int minCol = 5, maxCol = -1, minRow = 5, maxRow = -1;
        for (int i = 0; i < 25; i++) {
            if (!pContainer.getItem(i).isEmpty()) {
                int row = i / 5;
                int col = i % 5;
                minCol = Math.min(minCol, col);
                maxCol = Math.max(maxCol, col);
                minRow = Math.min(minRow, row);
                maxRow = Math.max(maxRow, row);
            }
        }

        if (maxCol == -1) return false; // 盤面が空

        // 2. レシピ(JSON)の「有効な素材がある範囲」を特定
        int rMinCol = 5, rMaxCol = -1, rMinRow = 5, rMaxRow = -1;
        for (int i = 0; i < 25; i++) {
            if (!recipeItems.get(i).isEmpty()) {
                int row = i / 5;
                int col = i % 5;
                rMinCol = Math.min(rMinCol, col);
                rMaxCol = Math.max(rMaxCol, col);
                rMinRow = Math.min(rMinRow, row);
                rMaxRow = Math.max(rMaxRow, row);
            }
        }

        int actualW = maxCol - minCol + 1;
        int actualH = maxRow - minRow + 1;
        int recipeW = rMaxCol - rMinCol + 1;
        int recipeH = rMaxRow - rMinRow + 1;

        // サイズが違えば不一致
        if (actualW != recipeW || actualH != recipeH) return false;

        // 3. 内容の比較
        for (int y = 0; y < recipeH; y++) {
            for (int x = 0; x < recipeW; x++) {
                Ingredient expected = recipeItems.get((rMinRow + y) * 5 + (rMinCol + x));
                ItemStack actual = pContainer.getItem((minRow + y) * 5 + (minCol + x));
                if (!expected.test(actual)) return false;
            }
        }
        return true;
    }

    // ★重要: 1.20.1では RegistryAccess が引数に必要
    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    // ★重要: 1.20.1では RegistryAccess が引数に必要
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.INDUSTRIAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.INDUSTRIAL_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    // --- インナークラス: Recipe Type ---
    // これが「このレシピは工業用作業台のレシピだ」という識別票になります
    public static class Type implements RecipeType<IndustrialRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "industrial_crafting";
    }

    // --- インナークラス: Serializer ---
    // JSONファイルの読み込みと、サーバー・クライアント間の通信を担当します
    public static class Serializer implements RecipeSerializer<IndustrialRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        // JSON -> Java
        @Override
        public IndustrialRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(25, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size() && i < ingredients.size(); i++) {
                JsonObject obj = ingredients.get(i).getAsJsonObject();

                if (obj.entrySet().isEmpty()) {
                    inputs.set(i, Ingredient.EMPTY);
                } else {
                    inputs.set(i, Ingredient.fromJson(obj));
                }
            }

            return new IndustrialRecipe(pRecipeId, result, inputs);
        }

        // Server -> Client (Network)
        @Override
        public @Nullable IndustrialRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(25, Ingredient.EMPTY);

            for (int i = 0; i < 25; i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            return new IndustrialRecipe(pRecipeId, output, inputs);
        }

        // Java -> Network
        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, IndustrialRecipe pRecipe) {
            for (Ingredient ing : pRecipe.getIngredients()) {
                ing.toNetwork(pBuffer);
            }
            pBuffer.writeItem(pRecipe.output);
        }
    }
}