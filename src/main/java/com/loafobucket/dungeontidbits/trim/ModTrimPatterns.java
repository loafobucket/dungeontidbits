package com.loafobucket.dungeontidbits.trim;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModTrimPatterns {
    public static final ResourceKey<TrimPattern> VITALITY = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "vitality"));
    public static final ResourceKey<TrimPattern> UTILITY = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "utility"));
    public static final ResourceKey<TrimPattern> AGILITY = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "agility"));
    public static final ResourceKey<TrimPattern> PLATED = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "plated"));
    public static final ResourceKey<TrimPattern> FRAMED = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "framed"));

    public static void bootstrap(BootstrapContext<TrimPattern> context) {
        register(context, ModItems.UPGRADE_VITALITY, VITALITY);
        register(context, ModItems.UPGRADE_UTILITY, UTILITY);
        register(context, ModItems.UPGRADE_AGILITY, AGILITY);
        register(context, ModItems.UPGRADE_PLATED, PLATED);
        register(context, ModItems.UPGRADE_FRAMED, FRAMED);
    }

    private static void register(BootstrapContext<TrimPattern> context, DeferredItem<Item> item, ResourceKey<TrimPattern> key) {
        TrimPattern trimPattern = new TrimPattern(key.location(), item.getDelegate(),
                Component.translatable(Util.makeDescriptionId("trim_pattern", key.location())), false);
        context.register(key, trimPattern);
    }
}
