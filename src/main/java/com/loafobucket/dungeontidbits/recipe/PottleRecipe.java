package com.loafobucket.dungeontidbits.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;


public record PottleRecipe(NonNullList<Ingredient> ingredients, ItemStack result) implements Recipe<PottleRecipeInput> {

    @Override
    public boolean matches(PottleRecipeInput inv, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        NonNullList<Ingredient> requiredIngredients = NonNullList.create();
        requiredIngredients.addAll(ingredients);
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stackInSlot = inv.getItem(i);
            if (!stackInSlot.isEmpty()) {
                boolean matched = false;
                Iterator<Ingredient> iterator = requiredIngredients.iterator();
                while (iterator.hasNext()) {
                    Ingredient ingredient = iterator.next();
                    if (ingredient.test(stackInSlot)) {
                        iterator.remove();
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
        }

        return requiredIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(PottleRecipeInput inv, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.POTTLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.POTTLE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    private static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
        NonNullList<Ingredient> list = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            list.set(i, ingredients.get(i));
        }
        return list;
    }

    public static class Serializer implements RecipeSerializer<PottleRecipe> {
        private static final MapCodec<PottleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(inputlist -> {Ingredient[] aingredient = inputlist.toArray(Ingredient[]::new);
                            if (aingredient.length == 0) {
                                return DataResult.error(() -> "the recipe is empty");
                            } else {
                                return  DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));}},
                        DataResult::success)
                        .forGetter(recipe -> recipe.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, (ingredients, output) -> new PottleRecipe(toNonNullList(ingredients), output)));

        private static final StreamCodec<RegistryFriendlyByteBuf, PottleRecipe> STREAM_CODEC = StreamCodec.of(
                PottleRecipe.Serializer::toNetwork, PottleRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<PottleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PottleRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PottleRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
            ingredients.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new PottleRecipe(ingredients, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, PottleRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}