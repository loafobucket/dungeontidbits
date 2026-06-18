package com.loafobucket.dungeontidbits.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SlippingTileBlock extends Block {
    public static final MapCodec<SlippingTileBlock> CODEC = simpleCodec(SlippingTileBlock::new);
    public SlippingTileBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 2));
            if (entity.getTicksFrozen() > 90) {
                entity.setIsInPowderSnow(true);
            } else {
                entity.setTicksFrozen(100);
            }
        }

        super.stepOn(level, pos, state, entity);
    }
}
