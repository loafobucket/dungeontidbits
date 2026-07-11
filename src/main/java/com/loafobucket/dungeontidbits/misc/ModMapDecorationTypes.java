package com.loafobucket.dungeontidbits.misc;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMapDecorationTypes {
    public static final DeferredRegister<MapDecorationType> MAP_DECORATION_TYPE = DeferredRegister.create(BuiltInRegistries.MAP_DECORATION_TYPE, DungeonTidbits.MOD_ID);

    public static final Holder<MapDecorationType> ROOM_GATEWAY_SPACE = MAP_DECORATION_TYPE.register("room_gateway_space", () -> new MapDecorationType(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID,"room_gateway_space"), true, 12215095, true, false));
    public static final Holder<MapDecorationType> TRAIL_RUINS = MAP_DECORATION_TYPE.register("trail_ruins", () -> new MapDecorationType(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID,"trail_ruins"), true, 10121308, true, false));

    public static void register(IEventBus eventBus){
        MAP_DECORATION_TYPE.register(eventBus);
    }
}
