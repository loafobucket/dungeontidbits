package com.loafobucket.dungeontidbits.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;
import java.util.stream.Stream;
//thank you scorched guns
public record PottleRecipeInput(List<ItemStack> items) implements RecipeInput {

    public PottleRecipeInput {
        items = List.copyOf(items);
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}