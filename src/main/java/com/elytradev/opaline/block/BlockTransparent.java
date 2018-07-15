package com.elytradev.opaline.block;

import com.elytradev.opaline.Opaline;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class BlockTransparent extends Block implements IBlockBase {

    protected String name;

    public BlockTransparent(Material material, String name) {
        super(material);
        //setSoundType(SoundType.GLASS);
        this.name = name;

        setTranslationKey(name);
        setRegistryName(name);
    }

    public void registerItemModel(Item item) {
        Opaline.proxy.registerItemRenderer(item, 0, name);
    }

    @Override
    public BlockTransparent setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public Block toBlock(){
        return this;
    }
}
