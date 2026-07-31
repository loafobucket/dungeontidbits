package com.loafobucket.dungeontidbits.item;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import com.loafobucket.dungeontidbits.item.custom.EffectExtractItem;
import com.loafobucket.dungeontidbits.item.custom.RoomKeyItem;
import com.loafobucket.dungeontidbits.item.custom.SparklingAxeItem;
import com.loafobucket.dungeontidbits.item.custom.UpgradePatternItem;
import com.loafobucket.dungeontidbits.misc.ModAttributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DungeonTidbits.MOD_ID);

    public static final DeferredItem<Item> ROOM_KEY = ITEMS.register("room_key",
            () -> new RoomKeyItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> POTTLE_SHERD = ITEMS.register("pottle_sherd",
            () -> new RoomKeyItem(new Item.Properties()));
    public static final DeferredItem<Item> EFFECT_EXTRACT = ITEMS.register ("effect_extract",
            () -> new EffectExtractItem(new Item.Properties().component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)));
    public static final DeferredItem<SparklingAxeItem> SPARKLING_AXE = ITEMS.register ("sparkling_axe",
            () -> new SparklingAxeItem(new Item.Properties()
                    .durability(32)
                    .component(DataComponents.TOOL, SparklingAxeItem.createToolProperties())
                    .attributes(SparklingAxeItem.createAttributes())));

    public static final DeferredItem<Item> UPGRADE_REINFORCED = ITEMS.register ("upgrade_reinforced",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "reinforced"), 1.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .build())));
    public static final DeferredItem<Item> UPGRADE_FORTIFIED = ITEMS.register ("upgrade_fortified",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "fortified"), 1.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "fortified"), 1.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .build())));
    public static final DeferredItem<Item> UPGRADE_VITALITY = ITEMS.register ("upgrade_vitality",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .build())));
    public static final DeferredItem<Item> UPGRADE_UTILITY = ITEMS.register ("upgrade_utility",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MOVEMENT_EFFICIENCY, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .add(Attributes.BURNING_TIME, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), -0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .add(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .add(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 0.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .add(ModAttributes.DAMPENING, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), -0.1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .build())));
    public static final DeferredItem<Item> UPGRADE_AGILITY = ITEMS.register ("upgrade_agility",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.ARMOR)
                    .build())));
    public static final DeferredItem<Item> UPGRADE_PLATED = ITEMS.register ("upgrade_plated",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .build())));
    public static final DeferredItem<Item> UPGRADE_FRAMED = ITEMS.register ("upgrade_framed",
            () -> new UpgradePatternItem(new Item.Properties().attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, "modified"), 1.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
                    .build())));

    public static final DeferredItem<ArmorItem> REINFORCED_LEATHER_HELMET = ITEMS.register("reinforced_leather_helmet",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.HELMET, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+200)));
    public static final DeferredItem<ArmorItem> REINFORCED_LEATHER_CHESTPLATE = ITEMS.register("reinforced_leather_chestplate",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+200)));
    public static final DeferredItem<ArmorItem> REINFORCED_LEATHER_LEGGINGS = ITEMS.register("reinforced_leather_leggings",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+200)));
    public static final DeferredItem<ArmorItem> REINFORCED_LEATHER_BOOTS = ITEMS.register("reinforced_leather_boots",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+200)));

    public static final DeferredItem<ArmorItem> FORTIFIED_LEATHER_HELMET = ITEMS.register("fortified_leather_helmet",
            () -> new ArmorItem(ModArmorMaterials.FORTIFIED_LEATHER, ArmorItem.Type.HELMET, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+400)));
    public static final DeferredItem<ArmorItem> FORTIFIED_LEATHER_CHESTPLATE = ITEMS.register("fortified_leather_chestplate",
            () -> new ArmorItem(ModArmorMaterials.FORTIFIED_LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+400)));
    public static final DeferredItem<ArmorItem> FORTIFIED_LEATHER_LEGGINGS = ITEMS.register("fortified_leather_leggings",
            () -> new ArmorItem(ModArmorMaterials.FORTIFIED_LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+400)));
    public static final DeferredItem<ArmorItem> FORTIFIED_LEATHER_BOOTS = ITEMS.register("fortified_leather_boots",
            () -> new ArmorItem(ModArmorMaterials.FORTIFIED_LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(5)+400)));

    public static final DeferredItem<ArmorItem> REINFORCED_GOLDEN_HELMET = ITEMS.register("reinforced_golden_helmet",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_GOLD, ArmorItem.Type.HELMET, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(7)+100)));
    public static final DeferredItem<ArmorItem> REINFORCED_GOLDEN_CHESTPLATE = ITEMS.register("reinforced_golden_chestplate",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_GOLD, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(7)+100)));
    public static final DeferredItem<ArmorItem> REINFORCED_GOLDEN_LEGGINGS = ITEMS.register("reinforced_golden_leggings",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_GOLD, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(7)+100)));
    public static final DeferredItem<ArmorItem> REINFORCED_GOLDEN_BOOTS = ITEMS.register("reinforced_golden_boots",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_GOLD, ArmorItem.Type.BOOTS, new Item.Properties().durability(net.minecraft.world.item.ArmorItem.Type.HELMET.getDurability(7)+100)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
