package com.loafobucket.dungeontidbits.block;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.block.custom.*;
import com.loafobucket.dungeontidbits.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
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
            () -> new PottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA).requiresCorrectToolForDrops().strength(4f).sound(SoundType.DECORATED_POT).noOcclusion()));
    public static final DeferredBlock<Block> ROOM_GATEWAY = registerBlock("room_gateway",
            () -> new RoomGatewayBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMITHING_TABLE).strength(25f).sound(SoundType.DRIPSTONE_BLOCK)));

    public static final DeferredBlock<Block> ROOM_WOOD = registerBlock("room_wood",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).requiresCorrectToolForDrops()));
    public static final DeferredBlock<StairBlock> ROOM_WOOD_STAIRS = registerBlock("room_wood_stairs",
            () -> new StairBlock(ModBlocks.ROOM_WOOD.get().defaultBlockState(),BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_STAIRS).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> ROOM_WOOD_SLAB = registerBlock("room_wood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_SLAB).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ROOM_TILE = registerBlock("room_tile",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE).requiresCorrectToolForDrops()));
    public static final DeferredBlock<StairBlock> ROOM_TILE_STAIRS = registerBlock("room_tile_stairs",
            () -> new StairBlock(ModBlocks.ROOM_TILE.get().defaultBlockState(),BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE_STAIRS).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> ROOM_TILE_SLAB = registerBlock("room_tile_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE_SLAB).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SLIPPING_TILE = registerBlock("slipping_tile",
            () -> new SlippingTileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_ICE).friction(1.1F).jumpFactor(0f).strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> BOOSTING_TILE = registerBlock("boosting_tile",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).friction(0.9F).speedFactor(1.2f).jumpFactor(1.2f).strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> TRAPPING_TILE = registerBlock("trapping_tile",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SAND).speedFactor(0.4f).jumpFactor(0.5f).strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DAMAGING_TILE = registerBlock("damaging_tile",
            () -> new DamagingTileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MAGMA_BLOCK).strength(3f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ROOM_STONE = registerBlock("room_stone",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ROOM_STONE_PILLAR = registerBlock("room_stone_pillar",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ROOM_LIGHT = registerBlock("room_light",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ROOM_SPAWNER = registerBlock("room_spawner",
            () -> new RoomSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).noOcclusion().requiresCorrectToolForDrops().lightLevel((light) -> {return 15;})));

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
