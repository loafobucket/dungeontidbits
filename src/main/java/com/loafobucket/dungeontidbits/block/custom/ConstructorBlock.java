package com.loafobucket.dungeontidbits.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;

//a blatant rip-off from jigsaw blocks
public class ConstructorBlock extends Block {
    public static final MapCodec<ConstructorBlock> CODEC = simpleCodec(ConstructorBlock::new);
    public static final EnumProperty<FrontAndTop> ORIENTATION;

    public MapCodec<ConstructorBlock> codec() {
        return CODEC;
    }

    public ConstructorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ORIENTATION, FrontAndTop.NORTH_UP));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ORIENTATION});
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue(ORIENTATION, rotation.rotation().rotate((FrontAndTop)state.getValue(ORIENTATION)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return (BlockState)state.setValue(ORIENTATION, mirror.rotation().rotate((FrontAndTop)state.getValue(ORIENTATION)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        Direction direction1;
        if (direction.getAxis() == Direction.Axis.Y) {
            direction1 = context.getHorizontalDirection().getOpposite();
        } else {
            direction1 = Direction.UP;
        }

        return (BlockState)this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction1));
    }

    public static Direction getFrontFacing(BlockState state) {
        return ((FrontAndTop)state.getValue(ORIENTATION)).front();
    }

    public static Direction getTopFacing(BlockState state) {
        return ((FrontAndTop)state.getValue(ORIENTATION)).top();
    }

    static {
        ORIENTATION = BlockStateProperties.ORIENTATION;
    }
}
