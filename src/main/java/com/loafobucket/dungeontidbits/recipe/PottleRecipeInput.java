package com.loafobucket.dungeontidbits.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;
import java.util.stream.Stream;
//thank you forbidden and arcanus
public record PottleRecipeInput(ItemStack firstInput, ItemStack secondInput, ItemStack thirdInput, ItemStack fourthInput) implements RecipeInput {

    public List<ItemStack> getInputs() {
        return Stream.of(this.firstInput, this.secondInput, this.thirdInput, this.fourthInput).filter(itemStack -> !itemStack.isEmpty()).toList();
    }

    @Override
    public ItemStack getItem(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}