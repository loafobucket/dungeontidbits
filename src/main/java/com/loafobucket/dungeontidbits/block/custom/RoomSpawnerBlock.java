package com.loafobucket.dungeontidbits.block.custom;

import com.loafobucket.dungeontidbits.Config;
import com.loafobucket.dungeontidbits.block.entity.ModBlockEntities;
import com.loafobucket.dungeontidbits.block.entity.RoomSpawnerBlockEntity;
import com.loafobucket.dungeontidbits.effect.ModEffects;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RoomSpawnerBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final IntegerProperty LEVEL = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
    public static final MapCodec<RoomSpawnerBlock> CODEC = simpleCodec(RoomSpawnerBlock::new);
    public RoomSpawnerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(((this.stateDefinition.any()).setValue(LEVEL, 0)).setValue(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @org.jetbrains.annotations.Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RoomSpawnerBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if ((Boolean)state.getValue(TRIGGERED)) {
            return InteractionResult.CONSUME;
        } else {
            level.setBlock(pos, (BlockState)state.setValue(TRIGGERED, true), 3);
            if (state.getValue(LEVEL) != 4) {
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
                if (Config.ROOM_SPAWNER_DAMPEN_TIME.getAsInt() != 0) {
                    areaeffectcloud.setPotionContents( new PotionContents (Optional.empty(), Optional.empty(), List.of(new MobEffectInstance(ModEffects.DAMPENING_EFFECT, Config.ROOM_SPAWNER_DAMPEN_TIME.getAsInt()*20, 0, true, false, true))));
                }
                areaeffectcloud.setParticle(ParticleTypes.END_ROD);
                areaeffectcloud.setRadius(0.5F);
                areaeffectcloud.setRadiusOnUse(0f);
                areaeffectcloud.setWaitTime(10);
                areaeffectcloud.setRadiusPerTick(0.8F);
                areaeffectcloud.setDuration(10);
                level.addFreshEntity(areaeffectcloud);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.ROOM_SPAWNER_BE.get(), RoomSpawnerBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
        builder.add(LEVEL);
    }
}
