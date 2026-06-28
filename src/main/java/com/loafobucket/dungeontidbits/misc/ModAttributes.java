package com.loafobucket.dungeontidbits.misc;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, DungeonTidbits.MOD_ID);

    public static final Holder<Attribute> DAMPENING = ATTRIBUTES.register("dampening", () -> new RangedAttribute(
            "attributes.dungeontidbits.dampening",
            1.0d,
            0.0d,
            100.0d
    ));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
