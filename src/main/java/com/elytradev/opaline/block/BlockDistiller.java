package com.elytradev.opaline.block;

import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.tile.TileEntityDistiller;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDistiller extends BlockTileEntity<TileEntityDistiller> implements IBlockBase {

    public static PropertyDirection FACING = BlockHorizontal.FACING;
    public static int FACE = 3;

    public BlockDistiller() {
        super(Material.ROCK, "distiller");
        this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    public int getMetaFromState(IBlockState state){
        int meta = 0;
        meta |= state.getValue(FACING).getHorizontalIndex();
        return meta;
    }

    public IBlockState getStateFromMeta(int meta){
        int facebits = meta & FACE;
        EnumFacing facing = EnumFacing.getHorizontal(facebits);
        return blockState.getBaseState().withProperty(FACING, facing);
    }

    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public Class<TileEntityDistiller> getTileEntityClass() {
        return TileEntityDistiller.class;
    }

    @Override
    public TileEntityDistiller createTileEntity(World world, IBlockState state) {
        return new TileEntityDistiller();
    }

    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(Opaline.creativeTab);
        return this;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        AxisAlignedBB n = new AxisAlignedBB(1/16.0, 0.0D, 4/16.0, 15/16.0,   15/16.0, 12/16.0);
        AxisAlignedBB s = new AxisAlignedBB(1/16.0, 0.0D, 4/16.0, 15/16.0,   15/16.0, 12/16.0);
        AxisAlignedBB e = new AxisAlignedBB(4/16.0, 0.0D, 1/16.0, 12/16.0, 15/16.0, 15/16.0);
        AxisAlignedBB w = new AxisAlignedBB(4/16.0, 0.0D, 1/16.0, 12/16.0, 15/16.0, 15/16.0);
        if(state.getValue(FACING) == EnumFacing.NORTH){return n;}
        if(state.getValue(FACING) == EnumFacing.SOUTH){return s;}
        if(state.getValue(FACING) == EnumFacing.EAST){return e;}
        if(state.getValue(FACING) == EnumFacing.WEST){return w;}
        return null; //this should NEVER happen
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public Block toBlock(){
        return this;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
}
