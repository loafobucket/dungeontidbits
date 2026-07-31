package com.loafobucket.dungeontidbits.recipe;

import com.loafobucket.dungeontidbits.misc.ModTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

//actually just a rip off from the regular SmithingTransformRecipe
public class SmithingAddonRecipe implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public SmithingAddonRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public boolean matches(SmithingRecipeInput input, Level level) {
        return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack output = input.base().copy();
        output.applyComponents(this.result.getComponentsPatch());
        List<ItemAttributeModifiers.Entry> originalAttribute = input.base().getItem().getDefaultAttributeModifiers(input.base().copy()).modifiers();
        List<ItemAttributeModifiers.Entry> templateAttribute = input.template().copy().getAttributeModifiers().modifiers();
        int bonus = 1;
        if (!templateAttribute.isEmpty() && output.is(ModTags.Items.UPGRADE_MODIFIABLE)) {
            if (input.base().is(ModTags.Items.UPGRADE_DOUBLED)) {bonus = 2;}
            output.remove(DataComponents.ATTRIBUTE_MODIFIERS);
            ItemAttributeModifiers modifier = ItemAttributeModifiers.EMPTY;
            for (ItemAttributeModifiers.Entry entry : originalAttribute) {
                modifier = modifier.withModifierAdded(
                        entry.attribute(),
                        new AttributeModifier(entry.modifier().id(),
                                entry.modifier().amount(),
                                entry.modifier().operation()),
                        EquipmentSlotGroup.ARMOR);
            }
            for (ItemAttributeModifiers.Entry entry : templateAttribute) {
                modifier = modifier.withModifierAdded(
                        entry.attribute(),
                        new AttributeModifier(entry.modifier().id(),
                                entry.modifier().amount() * bonus,
                                entry.modifier().operation()),
                        EquipmentSlotGroup.ARMOR);
            }
            ItemAttributeModifiers finalizedModifier = modifier;
            output.update(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    ItemAttributeModifiers.EMPTY,
                    itemAttributeModifiers -> finalizedModifier
            );
        }
        return output;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    public boolean isTemplateIngredient(ItemStack stack) {
        return this.template.test(stack);
    }

    public boolean isBaseIngredient(ItemStack stack) {
        return this.base.test(stack);
    }

    public boolean isAdditionIngredient(ItemStack stack) {
        return this.addition.test(stack);
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SMITHING_ADDON_SERIALIZER.get();
    }

    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::hasNoItems);
    }

    public static class Serializer implements RecipeSerializer<SmithingAddonRecipe> {
        private static final MapCodec<SmithingAddonRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
                Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, SmithingAddonRecipe::new));

        @Override
        public MapCodec<SmithingAddonRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingAddonRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingAddonRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);


        private static SmithingAddonRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient1 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient2 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack itemstack = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
            return new SmithingAddonRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, SmithingAddonRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.addition);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}
