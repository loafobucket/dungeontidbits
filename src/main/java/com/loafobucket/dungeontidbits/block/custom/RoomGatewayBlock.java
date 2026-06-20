package com.loafobucket.dungeontidbits.block.custom;

import com.loafobucket.dungeontidbits.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
                placeTwo(serverLevel, pos, state.getValue(FACING));
                placeThree(serverLevel, pos, state.getValue(FACING));
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
            rotationOffset = rotationOffset.offset(tileZ, 0, 0);
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            direction = Direction.EAST;
            rotationOffset = rotationOffset.offset(0, 0, tileX);
        } else {
            direction = Direction.NORTH;
            rotationOffset = rotationOffset.offset(tileX, 0, tileZ);
        }
        if (mirror == Mirror.LEFT_RIGHT) {
            mirrorOffset = mirrorOffset.relative(direction, tileZ);
        } else if (mirror == Mirror.FRONT_BACK) {
            mirrorOffset = mirrorOffset.relative(direction.getCounterClockWise(), tileX);
        }
        return rotationOffset.offset(mirrorOffset);
    }

    private void placeOne(ServerLevel serverLevel, BlockPos pos, Direction facing) {
        int tileSize = 5;
        Random random = new Random();
        BlockPos centerPos = pos.offset(new Vec3i(0,0,0).relative(facing.getOpposite(), 9));
        for  (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                StructureTemplate template;
                if (i == 0 && j == 0) {
                    String picking = String.valueOf(random.nextInt(2)+1);
                    template = (StructureTemplate) serverLevel.getStructureManager().get(ResourceLocation.parse("dungeontidbits:center_"+picking)).orElse(null);
                } else {
                    String picking = String.valueOf(random.nextInt(12)+1);
                    template = (StructureTemplate) serverLevel.getStructureManager().get(ResourceLocation.parse("dungeontidbits:one_"+picking)).orElse(null);
                }
                Rotation rotation = rotationOption[random.nextInt(4)];
                Mirror mirror = mirrorOption[random.nextInt(3)];
                Vec3i offset = offsetter(rotation, mirror, tileSize, tileSize);
                BlockPos changingPos = new BlockPos(centerPos.getX() + (tileSize * i) - 2 , pos.getY(), centerPos.getZ() + (tileSize * j) - 2).offset(offset);
                if (template != null) {
                    placeStructure(serverLevel, template, changingPos, rotation, mirror);
                }
            }
        }
    }

    private void placeTwo(ServerLevel serverLevel, BlockPos pos, Direction facing) {
        int tileSize = 5;
        Random random = new Random();
        BlockPos centerPos = pos.offset(new Vec3i(0,0,0).relative(facing.getOpposite(), 9));
        for  (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (random.nextFloat() <= 0.15) {
                    String picking = String.valueOf(random.nextInt(6) + 1);
                    StructureTemplate template = (StructureTemplate) serverLevel.getStructureManager().get(ResourceLocation.parse("dungeontidbits:two_"+picking)).orElse(null);
                    Rotation rotation = null;
                    int rotationPick = random.nextInt(2);
                    if (i == 0 && j != 0) {
                        rotation = rotationOption[rotationPick * 2];

                    } else if (j == 0 && i != 0) {
                        rotation = rotationOption[rotationPick * 2 + 1];
                    }
                    if (template != null && rotation != null) {
                        Mirror mirror = mirrorOption[random.nextInt(3)];
                        Vec3i offset = offsetter(rotation, mirror, tileSize * 2, tileSize);
                        BlockPos changingPos = new BlockPos(centerPos.getX() + (tileSize * i) - 2, pos.getY(), centerPos.getZ() + (tileSize * j) - 2).offset(offset);
                        placeStructure(serverLevel, template, changingPos, rotation, mirror);
                    }
                }
            }
        }
    }

    private void placeThree(ServerLevel serverLevel, BlockPos pos, Direction facing) {
        int tileSize = 5;
        Random random = new Random();
        BlockPos centerPos = pos.offset(new Vec3i(0,0,0).relative(facing.getOpposite(), 9));
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (random.nextFloat() <= 0.05) {
                    String picking = String.valueOf(random.nextInt(4) + 1);
                    StructureTemplate template = (StructureTemplate) serverLevel.getStructureManager().get(ResourceLocation.parse("dungeontidbits:three_"+picking)).orElse(null);
                    Rotation rotation = null;
                    Vec3i bandaid = new Vec3i(0,0,0);
                    int rotationPick = random.nextInt(2);
                    if (i == -1 && j == 1) {
                        rotation = rotationOption[rotationPick]; //0 1
                        if (rotationPick == 1) {bandaid = bandaid.offset(0, 0, -tileSize * 2);}
                    }
                    if (i == 1 && j == 1) {
                        rotation = rotationOption[rotationPick + 1]; //1 2
                        if (rotationPick == 0) {bandaid = bandaid.offset(0, 0, -tileSize * 2);}
                        else {bandaid = bandaid.offset(-tileSize * 2, 0, 0);}
                    }
                    if (i == 1 && j == -1) {
                        rotation = rotationOption[rotationPick + 2]; //2 3
                        if (rotationPick == 0) {bandaid = bandaid.offset(-tileSize * 2, 0, 0);}
                    }
                    if (i == -1 && j == -1) {
                        rotation = rotationOption[rotationPick * 3]; //0 3
                    }
                    if (template != null && rotation != null) {
                        Mirror mirror = mirrorOption[random.nextInt(3)];
                        Vec3i offset = offsetter(rotation, mirror, tileSize * 3, tileSize).offset(bandaid);
                        BlockPos changingPos = new BlockPos(centerPos.getX() + (tileSize * i) - 2, pos.getY(), centerPos.getZ() + (tileSize * j) - 2).offset(offset);
                        placeStructure(serverLevel, template, changingPos, rotation, mirror);
                    }
                }
            }
        }
    }

    private void placeStructure(ServerLevel level, StructureTemplate structureTemplate, BlockPos pos, Rotation rotation, Mirror mirror) {
        StructurePlaceSettings structureplacesettings = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror).setIgnoreEntities(true);
        structureTemplate.placeInWorld(level, pos, pos, structureplacesettings, createRandom(1), 2);
    }
}
