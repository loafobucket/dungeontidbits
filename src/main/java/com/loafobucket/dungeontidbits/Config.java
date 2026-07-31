package com.loafobucket.dungeontidbits;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue EFFECT_EXTRACT_USAGE = BUILDER
            .comment("Maximum amount of Effect Extracts used during Pottle activations")
            .defineInRange("effectExtractUsage", 16, 1, 64);

    public static final ModConfigSpec.IntValue ROOM_SPAWNER_DAMPEN_TIME = BUILDER
            .comment("Seconds that Room Spawner's effect cloud will apply the Dampening effect for")
            .defineInRange("roomSpawnerDampenTime", 480, 0, 1800);

    static final ModConfigSpec SPEC = BUILDER.build();
}
