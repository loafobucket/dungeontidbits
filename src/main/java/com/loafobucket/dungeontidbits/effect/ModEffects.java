package com.loafobucket.dungeontidbits.effect;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, DungeonTidbits.MOD_ID);

    public static final Holder<MobEffect> DAMPENING_EFFECT = MOB_EFFECTS.register("dampening", () -> new DampeningEffect(MobEffectCategory.NEUTRAL, 0x66998f));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
