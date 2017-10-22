package com.elytradev.opaline.block;

import com.elytradev.opaline.Opaline;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static BlockBase temp = new BlockBase(Material.ROCK, "temp").setCreativeTab(Opaline.creativeTab);

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                temp
        );
        //GameRegistry.registerTileEntity(temp.getTileEntityClass(), temp.getRegistryName().toString());
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                temp.createItemBlock()
        );

    }

    public static void registerModels() {
        temp.registerItemModel(Item.getItemFromBlock(temp));

    }
}
