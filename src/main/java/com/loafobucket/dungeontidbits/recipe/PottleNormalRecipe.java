package com.loafobucket.dungeontidbits.recipe;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.misc.ModTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

//thank you scorched guns
public class PottleNormalRecipe implements PottleRecipe {
    final NonNullList<Ingredient> ingredients;
    final ItemStack result;
    final Optional<Float> resultChance;
    final Optional<ItemStack> extra;
    final Optional<Float> extraChance;

    public CraftingBookCategory craftingBookCategory() {
        return CraftingBookCategory.MISC;
    }

    public PottleNormalRecipe(NonNullList<Ingredient> ingredients, ItemStack result, Optional<Float> resultChance, Optional<ItemStack> extra, Optional<Float> extraChance) {
        this.ingredients = ingredients;
        this.result = result;
        this.resultChance = resultChance;
        this.extra = extra;
        this.extraChance = extraChance;
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
        if (inv.getItem(0).is(ModTags.Items.MODIFIED_SMALL_FLOWERS)) {
            ItemStack flower = inv.getItem(0);
            var result = new ItemStack(ModItems.EFFECT_EXTRACT.get());
            if (!flower.isEmpty()) {
                SuspiciousEffectHolder suspiciouseffectholder = SuspiciousEffectHolder.tryGet(flower.getItem());
                if (suspiciouseffectholder != null) {
                    MobEffectInstance suspiciouscontent = new MobEffectInstance(suspiciouseffectholder.getSuspiciousEffects().effects().getFirst().effect());
                    result.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(suspiciouscontent)));
                    result.setCount(Math.ceilDiv(suspiciouseffectholder.getSuspiciousEffects().effects().getFirst().duration(), 40));
                }
            }
            return result;
        } else {
            return result.copy();
        }
    }


    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    public Float getResultChance(HolderLookup.Provider registries) {
        return resultChance.orElse(1.0F);
    }

    public ItemStack getExtraItem(HolderLookup.Provider registries) {
        return extra.map(ItemStack::copy).orElse(ItemStack.EMPTY);
    }

    public Float getExtraChance(HolderLookup.Provider registries) {
        return extraChance.orElse(1.0F);
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

    public static class Type implements RecipeType<PottleNormalRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "pottle";
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
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.FLOAT.optionalFieldOf("resultChance").forGetter(recipe -> recipe.resultChance),
                ItemStack.CODEC.optionalFieldOf("extra").forGetter(recipe -> recipe.extra),
                Codec.FLOAT.optionalFieldOf("extraChance").forGetter(recipe -> recipe.extraChance)
        ).apply(instance, (ingredients, result, resultChance, extra, extraChance) -> new PottleNormalRecipe(toNonNullList(ingredients), result, resultChance, extra, extraChance)));

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
            Optional<Float> resultChance = Optional.of(buffer.readFloat());
            Optional<ItemStack> extra = ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buffer);
            Optional<Float> extraChance = Optional.of(buffer.readFloat());
            return new PottleNormalRecipe(ingredients, result, resultChance, extra, extraChance);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, PottleNormalRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeFloat(recipe.resultChance.orElse(1F));
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buffer, recipe.extra);
            buffer.writeFloat(recipe.extraChance.orElse(1F));
        }
    }
}