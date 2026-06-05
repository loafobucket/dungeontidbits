package com.loafobucket.dungeontidbits.block.entity;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DungeonTidbits.MOD_ID);

    public static final Supplier<BlockEntityType<PottleBlockEntity>> POTTLE_BE =
            BLOCK_ENTITIES.register("constructor_be", () -> BlockEntityType.Builder.of(
                    PottleBlockEntity::new, ModBlocks.POTTLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
