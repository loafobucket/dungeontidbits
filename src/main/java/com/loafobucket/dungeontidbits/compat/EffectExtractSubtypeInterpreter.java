package com.loafobucket.dungeontidbits.compat;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class EffectExtractSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final EffectExtractSubtypeInterpreter INSTANCE = new EffectExtractSubtypeInterpreter();

    private EffectExtractSubtypeInterpreter() {

    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        PotionContents contents = ingredient.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (contents == PotionContents.EMPTY) {
            return "";
        } else {
            return contents.customEffects();
        }
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return getStringName(ingredient);
    }

    public String getStringName(ItemStack itemStack) {
        PotionContents contents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (contents == PotionContents.EMPTY) {
            return "";
        } else {
            return contents.customEffects().getFirst().getEffect().getRegisteredName();
        }
    }
}
