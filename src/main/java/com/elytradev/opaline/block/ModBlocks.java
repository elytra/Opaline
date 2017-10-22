package com.elytradev.opaline.block;

import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.block.fluids.BlockMana;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static BlockBase temp = new BlockBase(Material.ROCK, "temp").setCreativeTab(Opaline.creativeTab);

    public static Fluid fluidMana = new Fluid("mana",
        new ResourceLocation("opaline", "blocks/fluids/mana_still"),
        new ResourceLocation("opaline", "blocks/fluids/mana_flowing"))
        .setDensity(1396) //the density of glitter glue
        .setTemperature(294) //approximately 69ºF
        .setRarity(EnumRarity.UNCOMMON);

    public static IBlockBase[] allBlocks = {
            temp
    };

    public static void register(IForgeRegistry<Block> registry) {
        for (int i = 0; i < allBlocks.length; i++) {
            IBlockBase block = allBlocks[i];
            registry.register(block.toBlock());
        }
        BlockMana mana = new BlockMana(fluidMana, "fluid_mana");
        registry.register(mana);
        fluidMana.setBlock(mana);
        //GameRegistry.registerTileEntity(temp.getTileEntityClass(), temp.getRegistryName().toString());
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        for (int i = 0; i < allBlocks.length; i++) {
            IBlockBase block = allBlocks[i];
            registry.register(block.createItemBlock());
        }

    }

    public static void registerModels() {
        for (int i = 0; i < allBlocks.length; i++) {
            IBlockBase block = allBlocks[i];
            block.registerItemModel(Item.getItemFromBlock(block.toBlock()));
        }
    }
}
