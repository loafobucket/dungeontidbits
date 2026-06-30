package com.loafobucket.dungeontidbits.recipe;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
public interface PottleRecipe extends Recipe<PottleRecipeInput> {
    default RecipeType<?> getType() {
        return ModRecipes.POTTLE_TYPE.get();
    }

    default Float getResultChance(HolderLookup.Provider registries) {
        return 1.0F;
    }

    default ItemStack getExtraItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    default Float getExtraChance(HolderLookup.Provider registries) {
        return 1.0F;
    }

    default CraftingBookCategory craftingBookCategory() {return CraftingBookCategory.MISC;}
}