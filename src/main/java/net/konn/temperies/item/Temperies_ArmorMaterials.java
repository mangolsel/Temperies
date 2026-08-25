package net.konn.temperies.item;

import net.konn.temperies.Temperies;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class Temperies_ArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Temperies.MOD_ID);

    public static final Holder<ArmorMaterial> WOOL =
            ARMOR_MATERIALS.register("wool", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 1);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                    }), 5, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ItemTags.WOOL),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID, "wool"))),
                    0,0));

    public static final Holder<ArmorMaterial> FLAX =
            ARMOR_MATERIALS.register("flax", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 1);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                    }), 5, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Temperies_Items.FLAX_FABRIC),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID, "flax"))),
                    0,0));

    public static void register(IEventBus eventBus){
        ARMOR_MATERIALS.register(eventBus);
    }
}
