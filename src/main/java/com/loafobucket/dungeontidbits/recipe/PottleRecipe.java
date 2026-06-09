package com.loafobucket.dungeontidbits.recipe;

import net.minecraft.world.item.crafting.*;
public interface PottleRecipe extends Recipe<PottleRecipeInput> {
    default RecipeType<?> getType() {
        return ModRecipes.POTTLE_TYPE.get();
    }
}