package com.loafobucket.dungeontidbits.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
public interface PottleRecipe extends Recipe<PottleRecipeInput> {
    default RecipeType<?> getType() {
        return ModRecipes.POTTLE_TYPE.get();
    }


    default ItemStack getExtraItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
}