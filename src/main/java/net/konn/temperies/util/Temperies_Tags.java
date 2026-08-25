package net.konn.temperies.util;

import net.konn.temperies.Temperies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class Temperies_Tags {
    public static class Blocks{

        public static final TagKey<Block> DOES_NOT_PROVIDE_SHADE = createTag("does_not_provide_shade");
        public static final TagKey<Block> DOES_NOT_SEAL_ROOM = createTag("does_not_seal_room");

        private static TagKey<Block> createTag (String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID,name));
        }
    }
    public static class Items {

        public static final TagKey<Item> COOLING_MATERIALS = createTag("cooling_materials");

        private static TagKey<Item> createTag(String name) {return ItemTags.create(
                    ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID, name));
        }
    }
    public static final class Biomes {
        public static final TagKey<Biome> COLD =
                createTag("temperature/cold");

        public static final TagKey<Biome> HOT =
                createTag("temperature/hot");

        public static final TagKey<Biome> NEUTRAL =
                createTag("temperature/neutral");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID, name));
        }
    }
}
