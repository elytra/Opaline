package com.elytradev.opaline.client;


import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

public class OpalineTab extends CreativeTabs {
    public OpalineTab() {
        super(Opaline.modId);
        //setBackgroundImageName("opaline.png");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.distiller);
    }
}
