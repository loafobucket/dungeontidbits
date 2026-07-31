package com.loafobucket.dungeontidbits.entity;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.entity.custom.SparkleProjectileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DungeonTidbits.MOD_ID);

    public static final Supplier<EntityType<SparkleProjectileEntity>> SPARKLE =
            ENTITY_TYPES.register("sparkle", () -> EntityType.Builder.<SparkleProjectileEntity>of(SparkleProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("sparkle"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
