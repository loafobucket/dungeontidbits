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

import java.awt.*;

public class PottleRecipeCategory implements IRecipeCategory<RecipeHolder<PottleNormalRecipe>> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "pottle");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "textures/gui/pottle/pottle_gui_jei.png");
    public static final ResourceLocation CHANCE = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "textures/gui/chance.png");
    public static final RecipeType<RecipeHolder<PottleNormalRecipe>> POTTLE_RECIPE_TYPE = RecipeType.createRecipeHolderType(UID);
    private final IDrawable background;
    private final IDrawable icon;

    public PottleRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 86);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.POTTLE));
    }

    @NotNull
    @Override
    public RecipeType<RecipeHolder<PottleNormalRecipe>> getRecipeType() {
        return POTTLE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.dungeontidbits.pottle");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nullable
    @Override
    public IDrawable getBackground() {return background;}

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PottleNormalRecipe> holder, IFocusGroup focuses) {
        PottleNormalRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 35, 17).addIngredients(recipe.getIngredients().get(0));
        if (recipe.getIngredients().size() >= 2) {
            builder.addSlot(RecipeIngredientRole.INPUT, 17, 43).addIngredients(recipe.getIngredients().get(1));
            if (recipe.getIngredients().size() >= 3) {
                builder.addSlot(RecipeIngredientRole.INPUT, 35, 43).addIngredients(recipe.getIngredients().get(2));
                if (recipe.getIngredients().size() == 4) {
                    builder.addSlot(RecipeIngredientRole.INPUT, 53, 43).addIngredients(recipe.getIngredients().get(3));
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 35).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 35).addItemStack(recipe.getExtraItem(Minecraft.getInstance().level.registryAccess()));
    }

    @Override
    public void draw(RecipeHolder<PottleNormalRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PottleNormalRecipe recipe = holder.value();
        IRecipeCategory.super.draw(holder, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
        int resultChance = (int)(recipe.getResultChance(null)*100);
        int extraChance = (int)(recipe.getExtraChance(null)*100);
        if (resultChance < 100) {
            String resultChanceText = String.format("%d", resultChance);
            guiGraphics.drawString(Minecraft.getInstance().font, resultChanceText+"%", 115, 59, Color.gray.getRGB(), false);
        }
        if (!recipe.getExtraItem(null).isEmpty() && extraChance < 100) {
            String extraChanceText = String.format("%d", extraChance);
            guiGraphics.drawString(Minecraft.getInstance().font, extraChanceText+"%", 141, 59, Color.gray.getRGB(), false);
        }
    }
}
