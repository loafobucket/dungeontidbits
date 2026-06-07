package com.loafobucket.dungeontidbits.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

// thank you forbidden and arcanus
public record PottleExtraOutput(ItemStack extra, double chance) {

    public static final Codec<PottleExtraOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("type").forGetter(PottleExtraOutput::extra),
            Codec.DOUBLE.fieldOf("chance").forGetter(PottleExtraOutput::chance)
    ).apply(instance, PottleExtraOutput::new));
}
