package com.loafobucket.dungeontidbits.effect;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.misc.ModAttributes;
import joptsimple.internal.AbbreviationMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DampeningEffect extends MobEffect {

    public DampeningEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.getAttributes().hasAttribute(ModAttributes.DAMPENING)) {
            double dampenValue = (double) -(livingEntity.getAttributes().getValue(ModAttributes.DAMPENING) * 10 - 10);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "effect.dampening"), dampenValue, AttributeModifier.Operation.ADD_VALUE);
        }
    }
}
