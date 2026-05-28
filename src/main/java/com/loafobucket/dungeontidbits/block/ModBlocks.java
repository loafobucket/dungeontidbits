package com.loafobucket.dungeontidbits.block;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.block.custom.PottleBlock;
import com.loafobucket.dungeontidbits.block.custom.RoomGatewayBlock;
import com.loafobucket.dungeontidbits.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DungeonTidbits.MOD_ID);

    public static final DeferredBlock<Block> ROOM_VOID = registerBlock("room_void",
            () -> new TransparentBlock(BlockBehaviour.Properties.of().strength(-1f).explosionResistance(3600000f).sound(SoundType.GLASS).lightLevel((light) -> {
                return 3;
            })));
    public static final DeferredBlock<Block> POTTLE = registerBlock("pottle",
            () -> new PottleBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.DECORATED_POT).noOcclusion()));
    public static final DeferredBlock<Block> ROOM_GATEWAY = registerBlock("room_gateway",
            () -> new RoomGatewayBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.DRIPSTONE_BLOCK)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
