package com.loafobucket.dungeontidbits.item.custom;

import com.loafobucket.dungeontidbits.entity.custom.SparkleProjectileEntity;
import com.loafobucket.dungeontidbits.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class SparklingAxeItem extends Item {
    private final Random random = new Random();
    public SparklingAxeItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 6.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean onCooldown = false;
        if (attacker instanceof Player) {
            if (((Player) attacker).getCooldowns().isOnCooldown(ModItems.SPARKLING_AXE.get())) {
                onCooldown = true;
            }
        }
        if (random.nextDouble() < 0.3 && !(attacker instanceof Player) || onCooldown == false) {
            ServerLevel serverlevel = (ServerLevel)attacker.level();;
            serverlevel.sendParticles(ParticleTypes.END_ROD, target.getX(),target.getY()+(target.getBbHeight()/2),target.getZ(), 10, 0, 0, 0, 0.4);
            serverlevel.playSound(null, target.getX(),target.getY()+(target.getBbHeight()/2), target.getZ(), SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 0.7f, 1f);
            float damage = (float) (Math.max(attacker.getAttributeValue(Attributes.ATTACK_DAMAGE)/2, 4));
            int sparkleCount = EnchantmentHelper.getEnchantmentLevel(serverlevel.registryAccess().holderOrThrow(Enchantments.SWEEPING_EDGE), attacker) + 3;
            for (int i = 0; i < sparkleCount; i++) {
                SparkleProjectileEntity sparkle = new SparkleProjectileEntity(attacker, serverlevel);
                sparkle.setPos(target.getX(),target.getY()+(target.getBbHeight()/2),target.getZ());
                sparkle.shootFromRotation(attacker, attacker.getXRot(), attacker.getYRot(), 0.0F, random.nextFloat(0.8F) + 0.2F, 30.0F);
                sparkle.setLifetime(i*5+40);
                sparkle.setDamage(damage);
                serverlevel.addFreshEntity(sparkle);
            }
        }
        if (attacker instanceof Player) {
            ((Player) attacker).getCooldowns().addCooldown(ModItems.SPARKLING_AXE.get(), 20);
        }
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        //ah well this one doesn't want to work so i moved it to the one above
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.GOLD_INGOT);
    }
}
