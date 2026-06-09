package com.loafobucket.dungeontidbits.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;

//thank you scorched guns
public class PottleNormalRecipe implements PottleRecipe {
    final NonNullList<Ingredient> ingredients;
    final ItemStack result;

    public PottleNormalRecipe(NonNullList<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(PottleRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        NonNullList<Ingredient> requiredIngredients = NonNullList.create();
        requiredIngredients.addAll(ingredients);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stackInSlot = input.getItem(i);
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

    public static class Serializer implements RecipeSerializer<PottleNormalRecipe> {
        private static final MapCodec<PottleNormalRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(inputlist -> {Ingredient[] aingredient = inputlist.toArray(Ingredient[]::new);
                            if (aingredient.length == 0) {
                                return DataResult.error(() -> "the recipe is empty");
                            } else {
                                return  DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));}},
                        DataResult::success)
                        .forGetter(recipe -> recipe.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, (ingredients, result) -> new PottleNormalRecipe(toNonNullList(ingredients), result)));

        private static final StreamCodec<RegistryFriendlyByteBuf, PottleNormalRecipe> STREAM_CODEC = StreamCodec.of(
                PottleNormalRecipe.Serializer::toNetwork, PottleNormalRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<PottleNormalRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PottleNormalRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PottleNormalRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
            ingredients.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new PottleNormalRecipe(ingredients, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, PottleNormalRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}