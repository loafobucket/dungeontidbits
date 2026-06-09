package com.loafobucket.dungeontidbits.recipe;

import com.loafobucket.dungeontidbits.item.ModItems;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

//thank you scorched guns
public class PottleFlowerRecipe implements PottleRecipe {
    private final ItemStack result;
    final NonNullList<Ingredient> ingredients;

    public PottleFlowerRecipe(NonNullList<Ingredient> ingredients, ItemStack result) {
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
        ItemStack flower = inv.getItem(0);
        var result = new ItemStack(ModItems.EFFECT_EXTRACT.get());
        if (!flower.isEmpty()) {
            SuspiciousEffectHolder suspiciouseffectholder = SuspiciousEffectHolder.tryGet(flower.getItem());
            if (suspiciouseffectholder != null) {
                MobEffectInstance suspiciouscontent = new MobEffectInstance(suspiciouseffectholder.getSuspiciousEffects().effects().getFirst().effect());
                result.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(suspiciouscontent)));
                result.setCount(1 + suspiciouseffectholder.getSuspiciousEffects().effects().getFirst().duration() / 20);
            }
        }
        return result;
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
        return ModRecipes.POTTLE_FLOWER_SERIALIZER.get();
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

    public static class Serializer implements RecipeSerializer<PottleFlowerRecipe> {
        private static final MapCodec<PottleFlowerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(inputlist -> {Ingredient[] aingredient = inputlist.toArray(Ingredient[]::new);
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "the recipe is empty");
                                    } else {
                                        return  DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));}},
                                DataResult::success)
                        .forGetter(recipe -> recipe.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, (ingredients, result) -> new PottleFlowerRecipe(toNonNullList(ingredients), result)));

        private static final StreamCodec<RegistryFriendlyByteBuf, PottleFlowerRecipe> STREAM_CODEC = StreamCodec.of(
                PottleFlowerRecipe.Serializer::toNetwork, PottleFlowerRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<PottleFlowerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PottleFlowerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static PottleFlowerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
            ingredients.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new PottleFlowerRecipe(ingredients, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, PottleFlowerRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}