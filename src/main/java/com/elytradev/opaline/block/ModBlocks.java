package com.elytradev.opaline.block;

import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.block.fluids.BlockLazurite;
import com.elytradev.opaline.block.fluids.BlockOpaline;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static BlockTransparent distiller = new BlockTransparent(Material.ROCK, "distiller").setCreativeTab(Opaline.creativeTab);

    public static Fluid fluidOpaline = new Fluid("opaline",
        new ResourceLocation("opaline", "blocks/fluids/opaline_still"),
        new ResourceLocation("opaline", "blocks/fluids/opaline_flowing"))
        .setDensity(1396) //the density of glitter glue
        .setTemperature(294) //approximately 69ºF
        .setRarity(EnumRarity.UNCOMMON);
    public static Fluid fluidLazurite = new Fluid("lazurite",
            new ResourceLocation("opaline", "blocks/fluids/lazurite_still"),
            new ResourceLocation("opaline", "blocks/fluids/lazurite_flowing"))
            .setDensity(1436) //the year Gutenberg invented the printing press
            .setTemperature(506) //451ºF
            .setRarity(EnumRarity.RARE);

    public static IBlockBase[] allBlocks = {
        distiller
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

        FluidRegistry.registerFluid(ModBlocks.fluidLazurite);
        BlockLazurite lazurite = new BlockLazurite(fluidLazurite, "fluid_lazurite");
        registry.register(lazurite);
        fluidLazurite.setBlock(lazurite);
        FluidRegistry.addBucketForFluid(ModBlocks.fluidLazurite);

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
