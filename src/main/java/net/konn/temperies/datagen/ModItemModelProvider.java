package net.konn.temperies.datagen;

import net.konn.temperies.Temperies;
import net.konn.temperies.item.Temperies_Items;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Temperies.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //ITEMS
        basicItem(Temperies_Items.PEAT.get());
        basicItem(Temperies_Items.THERMOSCOPE.get());
        basicItem(Temperies_Items.THERMOMETER.get());

        //ARMOR
        basicItem(Temperies_Items.WOOL_BOOTS.get());
        basicItem(Temperies_Items.WOOL_CHESTPLATE.get());
        basicItem(Temperies_Items.WOOL_LEGGINGS.get());
        basicItem(Temperies_Items.WOOL_HELMET.get());

    }
    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(Temperies.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }
}
