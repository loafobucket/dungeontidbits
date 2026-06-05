package com.loafobucket.dungeontidbits.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record PottleRecipe(Ingredient inputItem, String inputEffectName, Integer inputEffectLength, ItemStack output, String outputEffectName, Integer outputEffectLength) implements Recipe<PottleRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(PottleRecipeInput pottleRecipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return inputItem.test(pottleRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(PottleRecipeInput pottleRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    public String getInputEffectName(HolderLookup.Provider provider) {
        return inputEffectName;
    }
    public Integer getInputEffectLength(HolderLookup.Provider provider) {
        return inputEffectLength;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    public String getOutputEffectName(HolderLookup.Provider provider) {
        return outputEffectName;
    }
    public Integer getOutputEffectLength(HolderLookup.Provider provider) {
        return outputEffectLength;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.POTTLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.POTTLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PottleRecipe> {
        public static final MapCodec<PottleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group (
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(PottleRecipe::inputItem),
                Codec.STRING.optionalFieldOf("inputeffectname","none").forGetter(PottleRecipe::inputEffectName),
                Codec.INT.optionalFieldOf("inputeffectlength",0).forGetter(PottleRecipe::inputEffectLength),
                ItemStack.CODEC.fieldOf("result").forGetter(PottleRecipe::output),
                Codec.STRING.optionalFieldOf("resulteffectname", "none").forGetter(PottleRecipe::outputEffectName),
                Codec.INT.optionalFieldOf("resulteffectlength",0).forGetter(PottleRecipe::outputEffectLength)
        ).apply(inst, PottleRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PottleRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, PottleRecipe::inputItem,
                        ByteBufCodecs.STRING_UTF8, PottleRecipe::inputEffectName,
                        ByteBufCodecs.VAR_INT, PottleRecipe::inputEffectLength,
                        ItemStack.STREAM_CODEC, PottleRecipe::output,
                        ByteBufCodecs.STRING_UTF8, PottleRecipe::outputEffectName,
                        ByteBufCodecs.VAR_INT, PottleRecipe::outputEffectLength,
                        PottleRecipe::new);

        @Override
        public MapCodec<PottleRecipe> codec() {
            return null;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PottleRecipe> streamCodec() {
            return null;
        }
    }
}
