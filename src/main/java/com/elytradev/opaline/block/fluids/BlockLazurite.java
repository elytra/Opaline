package com.elytradev.opaline.block.fluids;

import com.elytradev.opaline.Opaline;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockLazurite extends BlockFluidClassic {
    protected String name;

    public BlockLazurite(Fluid fluid, String name) {
        super(fluid, Material.WATER);
        this.quantaPerBlock = 8;
        this.quantaPerBlockFloat = 8f;
        this.tickRate = 14;
        this.temperature = 506;

        this.name = name;

        setUnlocalizedName(Opaline.modId + ".fluid." + name);
        setRegistryName(name);
    }
}