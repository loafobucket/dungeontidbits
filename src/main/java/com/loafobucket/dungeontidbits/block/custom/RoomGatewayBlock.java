package com.loafobucket.dungeontidbits.block.custom;

import com.loafobucket.dungeontidbits.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Random;

import static com.mojang.datafixers.TypeRewriteRule.orElse;

public class RoomGatewayBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<RoomGatewayBlock> CODEC = simpleCodec(RoomGatewayBlock::new);
    public RoomGatewayBlock(Properties properties) {
        super(properties);
    }
    public static final Mirror[] mirrorOption = {Mirror.NONE, Mirror.FRONT_BACK, Mirror.LEFT_RIGHT};
    public static final Rotation[] rotationOption = {Rotation.NONE, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90};

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            if (stack.is(ModItems.ROOM_KEY)) {
                placeOne(serverLevel, pos, state.getValue(FACING));
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static RandomSource createRandom(long seed) {
        return seed == 0L ? RandomSource.create(Util.getMillis()) : RandomSource.create(seed);
    }

    public Vec3i offsetter(Rotation rotation, Mirror mirror, int tileX, int tileZ) {
        Direction direction;
        tileX = tileX - 1;
        tileZ = tileZ - 1;
        Vec3i rotationOffset = new Vec3i(0, 0, 0);
        Vec3i mirrorOffset = new Vec3i(0, 0, 0);
        if (rotation == Rotation.NONE) {
            direction = Direction.SOUTH;
        } else if (rotation == Rotation.CLOCKWISE_90) {
            direction = Direction.WEST;
            rotationOffset.offset(tileZ, 0, 0);
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            direction = Direction.EAST;
            rotationOffset.offset(0, 0, tileX);
        } else {
            direction = Direction.NORTH;
            rotationOffset.offset(tileX, 0, tileZ);
        }
        if (mirror == Mirror.LEFT_RIGHT) {
            return mirrorOffset.relative(direction, tileX).offset(rotationOffset);
        } else if (mirror == Mirror.FRONT_BACK) {
            return mirrorOffset.relative(direction.getClockWise(), tileZ).offset(rotationOffset);
        } else {
            return rotationOffset;
        }
    }

    private void placeOne(ServerLevel serverLevel, BlockPos pos, Direction facing) {
        int tileSize = 5;
        Random random = new Random();
        BlockPos centerPos = pos.offset(new Vec3i(0,0,0).relative(facing.getOpposite(), 9));
        for  (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                String picking = String.valueOf(random.nextInt(11)+1);
                StructureTemplate template = (StructureTemplate) serverLevel.getStructureManager().get(ResourceLocation.parse("dungeontidbits:one_"+picking)).orElse(null);
                //Rotation rotation = rotationOption[random.nextInt(3)];
                //Mirror mirror = mirrorOption[random.nextInt(2)];
                Rotation rotation = Rotation.NONE;
                Mirror mirror = Mirror.NONE;
                Vec3i offset = offsetter(rotation, mirror, tileSize, tileSize);
                BlockPos changingPos = new BlockPos(centerPos.getX() + offset.getX() + (tileSize * i) - 2 , pos.getY(), centerPos.getZ() + offset.getZ() + (tileSize * j) - 2);
                if (template != null) {
                    placeStructure(serverLevel, template, changingPos, rotation, mirror);
                }
            }
        }
    }

    private void placeStructure(ServerLevel level, StructureTemplate structureTemplate, BlockPos pos, Rotation rotation, Mirror mirror) {
        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setMirror(mirror).setRotation(rotation).setIgnoreEntities(true);
        structureTemplate.placeInWorld(level, pos, pos, structureplacesettings, createRandom(1), 2);
    }
}
