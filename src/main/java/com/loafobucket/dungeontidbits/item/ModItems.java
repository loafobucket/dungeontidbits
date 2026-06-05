package com.loafobucket.dungeontidbits.item;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.component.ModDataComponents;
import com.loafobucket.dungeontidbits.item.custom.EffectExtractItem;
import com.loafobucket.dungeontidbits.item.custom.RoomKeyItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DungeonTidbits.MOD_ID);

    public static final DeferredItem<Item> ROOM_KEY = ITEMS.register("room_key",
            () -> new RoomKeyItem(new Item.Properties().component(ModDataComponents.ROOMDATA.get(), "dungeontidbits:flat").component(ModDataComponents.ROOMDEPTH.get(), 1).component(ModDataComponents.HALLDEPTH.get(), 1)));
    public static final DeferredItem<Item> EFFECT_EXTRACT = ITEMS.register ("effect_extract",
            () -> new EffectExtractItem(new Item.Properties().component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
