package com.loafobucket.dungeontidbits.misc;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.effect.ModEffects;
import com.loafobucket.dungeontidbits.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.Objects;

@EventBusSubscriber()
public class ModEvents {

    @SubscribeEvent
    public static void onLivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        float originalDamage = event.getAmount();
        if (target.hasEffect(ModEffects.DAMPENING_EFFECT) && target.getAttributes().hasAttribute(ModAttributes.DAMPENING)) {
            float magnitude = (float)target.getAttributeValue(ModAttributes.DAMPENING) * (float)target.getAttributeValue(ModAttributes.DAMPENING);
            event.setAmount(originalDamage * magnitude);
        }
    }

    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        event.add(
                EntityType.PLAYER,
                ModAttributes.DAMPENING, 1.0
        );
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity target = event.getEntity();
        if (Objects.requireNonNull(event.getEffectInstance()).getEffect() == ModEffects.DAMPENING_EFFECT) {
            Objects.requireNonNull(target.getAttributes().getInstance(Attributes.ATTACK_DAMAGE)).removeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "effect.dampening"));
        }
    }

    @SubscribeEvent
    public static void addWandererTrades(WandererTradesEvent event) {
        event.getGenericTrades().add(new BasicItemListing(16, new ItemStack(ModItems.ROOM_KEY.get(),1), 4, 0));
    }
}
