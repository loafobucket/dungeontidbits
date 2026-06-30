package com.loafobucket.dungeontidbits.compat;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.recipe.ModRecipes;
import com.loafobucket.dungeontidbits.recipe.PottleNormalRecipe;
import com.loafobucket.dungeontidbits.screen.custom.PottleScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIDungeonTidbitsPlugin implements IModPlugin {
    public static RecipeType<PottleNormalRecipe> POTTLE_RECIPE_TYPE = new RecipeType<>(PottleRecipeCategory.UID, PottleNormalRecipe.class);
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(new PottleRecipeCategory(jeiHelpers.getGuiHelper()));
    }
//courtesy of tarantel in Kaupenhub
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(PottleRecipeCategory.POTTLE_RECIPE_TYPE, getRecipe(recipeManager, ModRecipes.POTTLE_TYPE.get()));
    }

    public <C extends RecipeInput, T extends Recipe<C>> List<T> getRecipe(RecipeManager manager, net.minecraft.world.item.crafting.RecipeType<T> recipeType){
        List<T> list = new ArrayList<>();
        manager.getAllRecipesFor(recipeType).forEach(tRecipeHolder -> {
            list.add(tRecipeHolder.value());
        });
        return list;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(PottleScreen.class, 78, 35, 24, 16, PottleRecipeCategory.POTTLE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.POTTLE.get().asItem()),
                PottleRecipeCategory.POTTLE_RECIPE_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.EFFECT_EXTRACT.get(), EffectExtractSubtypeInterpreter.INSTANCE);
    }
}