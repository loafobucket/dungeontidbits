package com.loafobucket.dungeontidbits.compat;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.misc.ModTags;
import com.loafobucket.dungeontidbits.recipe.ModRecipes;
import com.loafobucket.dungeontidbits.recipe.PottleNormalRecipe;
import com.loafobucket.dungeontidbits.screen.custom.PottleScreen;
import com.mojang.datafixers.types.templates.Tag;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIDungeonTidbitsPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PottleRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
//thank you scorched guns
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<RecipeHolder<PottleNormalRecipe>> pottleNormalRecipes = recipeManager.getAllRecipesFor(ModRecipes.POTTLE_TYPE.get());
        registration.addRecipes(PottleRecipeCategory.POTTLE_RECIPE_TYPE, pottleNormalRecipes);
        registration.addRecipes(PottleRecipeCategory.POTTLE_RECIPE_TYPE, PottleFlowerRecipeMaker.createRecipes());
        registration.addIngredientInfo(ModBlocks.POTTLE, Component.translatable("desc.dungeontidbits.pottle"));
        registration.addIngredientInfo(ModItems.EFFECT_EXTRACT, Component.translatable("desc.dungeontidbits.effect_extract"));
        registration.addIngredientInfo(ModBlocks.ROOM_GATEWAY, Component.translatable("desc.dungeontidbits.room_gateway"));
        registration.addIngredientInfo(ModItems.ROOM_KEY, Component.translatable("desc.dungeontidbits.room_key"));
        registration.addIngredientInfo(ModBlocks.ROOM_SPAWNER, Component.translatable("desc.dungeontidbits.room_spawner"));
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