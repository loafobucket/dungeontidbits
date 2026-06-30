package com.loafobucket.dungeontidbits.compat;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.recipe.PottleNormalRecipe;
import com.loafobucket.dungeontidbits.recipe.PottleRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PottleRecipeCategory implements IRecipeCategory<PottleNormalRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "pottling");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "textures/gui/pottle/pottle_gui_jei.png");
    public static final ResourceLocation CHANCE = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "textures/gui/chance.png");
    public static final RecipeType<PottleNormalRecipe> POTTLE_RECIPE_TYPE = RecipeType.create(DungeonTidbits.MOD_ID, "pottling", PottleNormalRecipe.class);
    private final IDrawable background;
    //private final IDrawable resultchance;
    //private final IDrawable extrachance;
    private final IDrawable icon;

    public PottleRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 86);
        //this.resultchance = helper.createDrawable(CHANCE,118, 59, 16, 16);
        //this.extrachance = helper.createDrawable(CHANCE,145, 59, 16, 16);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.POTTLE));
    }

    @NotNull
    @Override
    public RecipeType<PottleNormalRecipe> getRecipeType() {
        return JEIDungeonTidbitsPlugin.POTTLE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.dungeontidbits.pottle");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PottleNormalRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 35, 17).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 43).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 35, 43).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 53, 43).addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 35).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 35).addItemStack(recipe.getExtraItem(Minecraft.getInstance().level.registryAccess()));
    }

    //@Override
    //public void draw(RecipeHolder<PottleNormalRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        //PottleRecipe recipe = holder.value();
        //IRecipeCategory.super.draw(holder, recipeSlotsView, guiGraphics, mouseX, mouseY);
        //background.draw(guiGraphics);
        //if (recipe.getResultChance(null) < 1.0) {
        //    resultchance.draw(guiGraphics);
        //}
        //if (!recipe.getExtraItem(null).isEmpty() && recipe.getExtraChance(null) < 1.0) {
        //    extrachance.draw(guiGraphics);
        //}
    //}
}
