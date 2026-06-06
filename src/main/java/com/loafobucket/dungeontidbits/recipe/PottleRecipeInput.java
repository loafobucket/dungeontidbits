package com.loafobucket.dungeontidbits.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record PottleRecipeInput(ItemStack input, ItemStack effect) implements RecipeInput {
    public PottleRecipeInput(ItemStack input, ItemStack effect) {
        this.input = input;
        this.effect = effect;
    }

    public ItemStack getItem(int i) {
        ItemStack item;
        switch (i) {
            case 0 -> item = this.input;
            case 1 -> item = this.effect;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + i);
        }

        return item;
    }

    public int size() {
        return 2;
    }

    public boolean isEmpty() {
        return this.input.isEmpty() && this.effect.isEmpty();
    }

    public ItemStack template() {
        return this.input;
    }

    public ItemStack base() {
        return this.effect;
    }
}