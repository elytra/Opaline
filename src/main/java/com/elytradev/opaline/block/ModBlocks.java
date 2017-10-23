package com.elytradev.opaline.block;

import com.elytradev.opaline.block.fluids.BlockOpaline;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {


    public static Fluid fluidOpaline = new Fluid("opaline",
        new ResourceLocation("opaline", "blocks/fluids/opaline_still"),
        new ResourceLocation("opaline", "blocks/fluids/opaline_flowing"))
        .setDensity(1396) //the density of glitter glue
        .setTemperature(294) //approximately 69ºF
        .setRarity(EnumRarity.UNCOMMON);

    public static IBlockBase[] allBlocks = {

    };

    public static void register(IForgeRegistry<Block> registry) {
        for (int i = 0; i < allBlocks.length; i++) {
            IBlockBase block = allBlocks[i];
            registry.register(block.toBlock());
        }
        FluidRegistry.registerFluid(ModBlocks.fluidOpaline);
        BlockOpaline opaline = new BlockOpaline(fluidOpaline, "fluid_opaline");
        registry.register(opaline);
        fluidOpaline.setBlock(opaline);
        FluidRegistry.addBucketForFluid(ModBlocks.fluidOpaline);
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
