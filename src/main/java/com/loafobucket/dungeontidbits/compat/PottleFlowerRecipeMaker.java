package com.loafobucket.dungeontidbits.compat;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.misc.ModTags;
import com.loafobucket.dungeontidbits.recipe.PottleNormalRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

import java.util.List;
import java.util.Optional;

public final class PottleFlowerRecipeMaker {
    //thank you nirvana
    public static List<RecipeHolder<PottleNormalRecipe>> createRecipes() {
        String group = "jei.effect.extract";

        return BuiltInRegistries.ITEM.getTag(ModTags.Items.MODIFIED_SMALL_FLOWERS)
                .stream()
                .flatMap(HolderSet.ListBacked::stream)
                .map(Holder::value)
                .filter(BlockItem.class::isInstance)
                .map(item -> ((BlockItem) item).getBlock())
                .filter(FlowerBlock.class::isInstance)
                .map(FlowerBlock.class::cast)
                .map(flowerBlock -> {
                    Ingredient flower = Ingredient.of(flowerBlock.asItem());
                    NonNullList<Ingredient> input = NonNullList.of(Ingredient.EMPTY, flower);
                    ItemStack output = new ItemStack(ModItems.EFFECT_EXTRACT.get());
                    var effects = flowerBlock.getSuspiciousEffects();
                    output.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, effects);
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "jei.effect.extract." + flowerBlock.getDescriptionId());

                    MobEffectInstance suspiciouscontent = new MobEffectInstance(effects.effects().getFirst().effect());
                    output.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(suspiciouscontent)));
                    output.setCount(Math.ceilDiv(effects.effects().getFirst().duration(), 40));

                    PottleNormalRecipe recipe = new PottleNormalRecipe(input, output, Optional.empty(), Optional.empty(), Optional.empty());
                    return new RecipeHolder<>(id, recipe);
                })
                .toList();
    }

    private PottleFlowerRecipeMaker() {

    }
}