package com.loafobucket.dungeontidbits.item;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final Holder<ArmorMaterial> REINFORCED_LEATHER = register("reinforced_leather",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, 2);
                attribute.put(ArmorItem.Type.LEGGINGS, 3);
                attribute.put(ArmorItem.Type.CHESTPLATE, 4);
                attribute.put(ArmorItem.Type.HELMET, 2);
                attribute.put(ArmorItem.Type.BODY, 4);
            }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0.0f, () -> Items.LEATHER,
            List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID,"reinforced_leather"), "_overlay", false)));

    public static final Holder<ArmorMaterial> FORTIFIED_LEATHER = register("fortified_leather",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, 3);
                attribute.put(ArmorItem.Type.LEGGINGS, 5);
                attribute.put(ArmorItem.Type.CHESTPLATE, 6);
                attribute.put(ArmorItem.Type.HELMET, 3);
                attribute.put(ArmorItem.Type.BODY, 5);
            }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 1.5f, 0.0f, () -> Items.LEATHER,
            List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID,"fortified_leather"), "_overlay", false)));

    public static final Holder<ArmorMaterial> REINFORCED_GOLD = register("reinforced_gold",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, 2);
                attribute.put(ArmorItem.Type.LEGGINGS, 4);
                attribute.put(ArmorItem.Type.CHESTPLATE, 6);
                attribute.put(ArmorItem.Type.HELMET, 3);
                attribute.put(ArmorItem.Type.BODY, 9);
            }), 25, SoundEvents.ARMOR_EQUIP_GOLD, 1f, 0.0f, () -> Items.GOLD_INGOT);


    private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> typeProtection, int enchantability, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Item> ingredientItem) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, name);
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));
        return register(name, typeProtection, enchantability, equipSound, toughness, knockbackResistance,ingredientItem, layers);
    }

    private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> typeProtection, int enchantability, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Item> ingredientItem, List<ArmorMaterial.Layer> layers) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID, name);
        Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());
        EnumMap<ArmorItem.Type, Integer> typeMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            typeMap.put(type, typeProtection.get(type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location,
                new ArmorMaterial(typeProtection, enchantability, equipSound, ingredient, layers, toughness, knockbackResistance));
    }
}
