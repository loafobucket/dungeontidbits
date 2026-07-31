package com.loafobucket.dungeontidbits.misc;

import com.loafobucket.dungeontidbits.DungeonTidbits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID,name));
        }
    }
    public static class Items {
        public static final TagKey<Item> MODIFIED_SMALL_FLOWERS = createTag("modified_small_flowers");
        public static final TagKey<Item> SMOKY_ITEMS = createTag("smoky_items");
        public static final TagKey<Item> UPGRADE_MODIFIABLE = createTag("upgrade_modifiable");
        public static final TagKey<Item> UPGRADE_DOUBLED = createTag("upgrade_doubled");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(DungeonTidbits.MOD_ID,name));
        }
    }
}
