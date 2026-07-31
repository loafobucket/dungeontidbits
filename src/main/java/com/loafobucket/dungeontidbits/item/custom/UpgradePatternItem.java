package com.loafobucket.dungeontidbits.item.custom;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradePatternItem extends Item {
    private static final ChatFormatting TITLE_FORMAT;
    private static final ChatFormatting DESCRIPTION_FORMAT;
    private static final Component INGREDIENTS_TITLE;
    private static final Component APPLIES_TO_TITLE;
    private static final Component ATTRIBUTE_APPLIES_TO_TITLE;
    private static final Component REINFORCED_UPGRADE_APPLIES_TO;
    private static final Component FORTIFIED_UPGRADE_APPLIES_TO;
    private static final Component MODIFIED_UPGRADE_APPLIES_TO;
    private static final Component INGREDIENT_COPPER;
    private static final Component INGREDIENT_OMINOUS;
    private static final Component INGREDIENT_EMERALD;
    private static final Component INGREDIENT_LAPIS;
    private static final Component INGREDIENT_REDSTONE;
    private static final Component INGREDIENT_PRISMARINE;
    private static final Component INGREDIENT_AMETHYST;

    public UpgradePatternItem(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.is(ModItems.UPGRADE_REINFORCED)) {
            tooltipComponents.add(APPLIES_TO_TITLE);
            tooltipComponents.add(CommonComponents.space().append(REINFORCED_UPGRADE_APPLIES_TO));
            tooltipComponents.add(INGREDIENTS_TITLE);
            tooltipComponents.add(CommonComponents.space().append(INGREDIENT_COPPER));
        } else if (stack.is(ModItems.UPGRADE_FORTIFIED)) {
            tooltipComponents.add(APPLIES_TO_TITLE);
            tooltipComponents.add(CommonComponents.space().append(FORTIFIED_UPGRADE_APPLIES_TO));
            tooltipComponents.add(INGREDIENTS_TITLE);
            tooltipComponents.add(CommonComponents.space().append(INGREDIENT_OMINOUS));
        } else {
            tooltipComponents.add(ATTRIBUTE_APPLIES_TO_TITLE);
            tooltipComponents.add(CommonComponents.space().append(MODIFIED_UPGRADE_APPLIES_TO));
            tooltipComponents.add(INGREDIENTS_TITLE);
            if (stack.is(ModItems.UPGRADE_VITALITY)) {
                tooltipComponents.add(CommonComponents.space().append(INGREDIENT_EMERALD));
            } else if (stack.is(ModItems.UPGRADE_UTILITY)) {
                tooltipComponents.add(CommonComponents.space().append(INGREDIENT_LAPIS));
            } else if (stack.is(ModItems.UPGRADE_AGILITY)) {
                tooltipComponents.add(CommonComponents.space().append(INGREDIENT_REDSTONE));
            } else if (stack.is(ModItems.UPGRADE_PLATED)) {
                tooltipComponents.add(CommonComponents.space().append(INGREDIENT_PRISMARINE));
            } else if (stack.is(ModItems.UPGRADE_FRAMED)) {
                tooltipComponents.add(CommonComponents.space().append(INGREDIENT_AMETHYST));
            }
        }
    }

    static {
        TITLE_FORMAT = ChatFormatting.GRAY;
        DESCRIPTION_FORMAT = ChatFormatting.BLUE;
        INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
        APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
        ATTRIBUTE_APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("upgrade_pattern", ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "description"))).withStyle(TITLE_FORMAT);

        REINFORCED_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("upgrade_pattern", ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "reinforced_list"))).withStyle(DESCRIPTION_FORMAT);
        FORTIFIED_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("upgrade_pattern", ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "fortified_list"))).withStyle(DESCRIPTION_FORMAT);
        MODIFIED_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("upgrade_pattern", ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified_list"))).withStyle(DESCRIPTION_FORMAT);

        INGREDIENT_COPPER = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("copper_ingot"))).withStyle(DESCRIPTION_FORMAT);
        INGREDIENT_OMINOUS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("ominous_bottle"))).withStyle(DESCRIPTION_FORMAT);
        INGREDIENT_EMERALD = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("emerald"))).withStyle(DESCRIPTION_FORMAT);
        INGREDIENT_LAPIS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("lapis_lazuli"))).withStyle(DESCRIPTION_FORMAT);
        INGREDIENT_REDSTONE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("redstone"))).withStyle(DESCRIPTION_FORMAT);
        INGREDIENT_PRISMARINE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("prismarine_shard"))).withStyle(DESCRIPTION_FORMAT);
        INGREDIENT_AMETHYST = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("amethyst_shard"))).withStyle(DESCRIPTION_FORMAT);
    }
}
