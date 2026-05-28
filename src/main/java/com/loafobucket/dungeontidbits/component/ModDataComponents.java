package com.loafobucket.dungeontidbits.component;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,DungeonTidbits.MOD_ID);

    public static  final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ROOMDATA = register("roomdata", builder -> builder.persistent(Codec.STRING));
    public static  final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ROOMDEPTH = register("roomdepth", builder -> builder.persistent(Codec.INT));
    public static  final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HALLDEPTH = register("halldepth", builder -> builder.persistent(Codec.INT));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }
    public static void register (IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
