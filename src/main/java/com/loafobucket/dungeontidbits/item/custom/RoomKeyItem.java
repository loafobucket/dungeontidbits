package com.loafobucket.dungeontidbits.item.custom;

import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.block.custom.ConstructorBlock;
import com.loafobucket.dungeontidbits.component.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Map;



public class RoomKeyItem extends Item {
    private static final Map<Block, Block> VOID_MAP =
            Map.of (ModBlocks.ROOM_CONSTRUCTOR.get(), Blocks.BEDROCK);
    public RoomKeyItem(Properties properties) {
        super(properties);
    }

    //@Override
    //public InteractionResult useOn(UseOnContext context) {
    //    Level level = context.getLevel();
    //    Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();
    //    Integer halldepth = context.getItemInHand().get(ModDataComponents.HALLDEPTH);
    //    BlockPos blockpos = context.getClickedPos().relative(level.getBlockState(context.getClickedPos()).getValue(ConstructorBlock.ORIENTATION).front());
    //    ResourceLocation roomdata = ResourceLocation.parse(context.getItemInHand().get(ModDataComponents.ROOMDATA));
    //    Integer roomdepth = context.getItemInHand().get(ModDataComponents.ROOMDEPTH);
    //    ResourceLocation target = ResourceLocation.parse("minecraft:empty");
    //    boolean keepJigsaws = false;

    //    if(VOID_MAP.containsKey(clickedBlock)) {
    //        if (!level.isClientSide()) {
    //            ResourceKey<StructureTemplatePool> pool = ResourceKey.create(Registries.TEMPLATE_POOL, roomdata);
    //            Registry<StructureTemplatePool> registry = level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
    //            Holder<StructureTemplatePool> holder = registry.getHolderOrThrow(pool);
    //            ServerLevel serverlevel = (ServerLevel) level;
    //            JigsawPlacement.generateJigsaw(serverlevel, holder, target, roomdepth, blockpos.above(), keepJigsaws);

    //            level.playSound(null, context.getClickedPos(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS);
    //        }
    //    }

    //    return super.useOn(context);
    //}
}