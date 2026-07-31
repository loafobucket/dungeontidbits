package com.loafobucket.dungeontidbits.entity.client;

import com.loafobucket.dungeontidbits.entity.custom.SparkleProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SparkleProjectileEntityRenderer extends EntityRenderer<SparkleProjectileEntity> {
    public SparkleProjectileEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SparkleProjectileEntity sparkleProjectileEntity) {
        return null;
    }
}
