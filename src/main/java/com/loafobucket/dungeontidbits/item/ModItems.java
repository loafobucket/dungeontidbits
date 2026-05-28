package com.loafobucket.dungeontidbits.item;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.component.ModDataComponents;
import com.loafobucket.dungeontidbits.item.custom.RoomKeyItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DungeonTidbits.MOD_ID);

    public static final DeferredItem<Item> ROOM_KEY = ITEMS.register("room_key",
            () -> new RoomKeyItem(new Item.Properties().component(ModDataComponents.ROOMDATA.get(), "dungeontidbits:flat").component(ModDataComponents.ROOMDEPTH.get(), 1).component(ModDataComponents.HALLDEPTH.get(), 1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
