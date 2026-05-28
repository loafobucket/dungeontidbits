package com.loafobucket.dungeontidbits.item.custom;

import com.loafobucket.dungeontidbits.block.ModBlocks;
import com.loafobucket.dungeontidbits.component.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
            Map.of (ModBlocks.ROOM_VOID.get(), Blocks.BEDROCK);
    public RoomKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();
        Integer halldepth = context.getItemInHand().get(ModDataComponents.HALLDEPTH);
        BlockPos blockpos = context.getClickedPos().relative(context.getClickedFace().getOpposite(), halldepth);
        ResourceLocation roomdata = ResourceLocation.parse(context.getItemInHand().get(ModDataComponents.ROOMDATA));
        Integer roomdepth = context.getItemInHand().get(ModDataComponents.ROOMDEPTH);
        //i quite literally have no idea what happened here but it stopped showing as an error so i guess thats good
        ResourceKey<StructureTemplatePool> pool = ResourceKey.create(Registries.TEMPLATE_POOL, roomdata);
        Registry<StructureTemplatePool> registry = level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder = registry.getHolderOrThrow(pool);
        //the amount of yellow here is concerning
        boolean keepJigsaws = false;

        if(VOID_MAP.containsKey(clickedBlock)) {
            if (!level.isClientSide()) {
                ServerLevel serverlevel = (ServerLevel) level;
                JigsawPlacement.generateJigsaw(serverlevel, holder, roomdata, roomdepth, blockpos, keepJigsaws);
            }
        }
        return super.useOn(context);
    }
}