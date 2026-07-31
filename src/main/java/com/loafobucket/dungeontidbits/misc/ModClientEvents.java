package com.loafobucket.dungeontidbits.misc;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.entity.ModEntities;
import com.loafobucket.dungeontidbits.entity.client.SparkleProjectileEntityRenderer;
import com.loafobucket.dungeontidbits.item.ModItems;
import com.loafobucket.dungeontidbits.screen.ModMenuTypes;
import com.loafobucket.dungeontidbits.screen.custom.PottleScreen;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@EventBusSubscriber(modid = DungeonTidbits.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event){
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : FastColor.ARGB32.opaque(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor()), ModItems.EFFECT_EXTRACT);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.REINFORCED_LEATHER_HELMET);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.REINFORCED_LEATHER_CHESTPLATE);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.REINFORCED_LEATHER_LEGGINGS);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.REINFORCED_LEATHER_BOOTS);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.FORTIFIED_LEATHER_HELMET);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.FORTIFIED_LEATHER_CHESTPLATE);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.FORTIFIED_LEATHER_LEGGINGS);
        event.register((stack, colorIn) -> colorIn > 0 ? -1 : DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR), ModItems.FORTIFIED_LEATHER_BOOTS);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.POTTLE_MENU.get(), PottleScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.SPARKLE.get(), SparkleProjectileEntityRenderer::new);
    }
}
