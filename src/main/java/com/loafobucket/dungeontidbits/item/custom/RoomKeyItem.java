package com.loafobucket.dungeontidbits.item.custom;

import com.loafobucket.dungeontidbits.block.ModBlocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;

import java.util.Map;


public class RoomKeyItem extends Item {
    private static final Map<Block, Block> VOID_MAP =
            Map.of (ModBlocks.ROOM_VOID.get(), Blocks.BEDROCK);
    public RoomKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();

        if(VOID_MAP.containsKey(clickedBlock)) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(context.getClickedPos(), VOID_MAP.get(clickedBlock).defaultBlockState());
                level.playSound(null, context.getClickedPos(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS);
            }
        }
        return super.useOn(context);
    }
}