package com.elytradev.opaline.block.fluids;

import com.elytradev.opaline.Opaline;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockOpaline extends BlockFluidClassic {
    protected String name;

    public BlockOpaline(Fluid fluid, String name) {
        super(fluid, Material.WATER);
        this.quantaPerBlock= 8;
        this.quantaPerBlockFloat = 8f;
        this.tickRate = 7;
        this.temperature = 294;

        this.name = name;

        setUnlocalizedName(Opaline.modId + ".fluid." + name);
        setRegistryName(name);
    }
}
