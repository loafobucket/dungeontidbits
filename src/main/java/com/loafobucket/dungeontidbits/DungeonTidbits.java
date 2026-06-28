package com.loafobucket.dungeontidbits;

import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.block.entity.ModBlockEntities;
import com.loafobucket.dungeontidbits.component.ModDataComponents;
import com.loafobucket.dungeontidbits.effect.ModEffects;
import com.loafobucket.dungeontidbits.misc.ModAttributes;
import com.loafobucket.dungeontidbits.misc.ModCapabilities;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.recipe.ModRecipes;
import com.loafobucket.dungeontidbits.screen.ModMenuTypes;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DungeonTidbits.MOD_ID)
public class DungeonTidbits {
    public static final String MOD_ID = "dungeontidbits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DungeonTidbits(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(ModCapabilities::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ROOM_KEY);
            event.accept(ModItems.EFFECT_EXTRACT);
        }
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.POTTLE);
            event.accept(ModBlocks.ROOM_GATEWAY);
        }
        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.ROOM_WOOD);
            event.accept(ModBlocks.ROOM_WOOD_STAIRS);
            event.accept(ModBlocks.ROOM_WOOD_SLAB);
            event.accept(ModBlocks.ROOM_TILE);
            event.accept(ModBlocks.ROOM_TILE_STAIRS);
            event.accept(ModBlocks.ROOM_TILE_SLAB);
            event.accept(ModBlocks.ROOM_STONE);
            event.accept(ModBlocks.ROOM_STONE_PILLAR);
            event.accept(ModBlocks.ROOM_LIGHT);
            event.accept(ModBlocks.ROOM_SPAWNER);

            event.accept(ModBlocks.SLIPPING_TILE);
            event.accept(ModBlocks.BOOSTING_TILE);
            event.accept(ModBlocks.TRAPPING_TILE);
            event.accept(ModBlocks.DAMAGING_TILE);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
