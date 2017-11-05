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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDistiller extends BlockTileEntity<TileEntityDistiller> implements IBlockBase {

    public static PropertyDirection FACING = BlockHorizontal.FACING;
    public static int FACE = 3;

    private static final AxisAlignedBB n = new AxisAlignedBB(1/16.0, 0.0D, 4/16.0, 15/16.0,   15/16.0, 12/16.0);
    private static final AxisAlignedBB s = new AxisAlignedBB(1/16.0, 0.0D, 4/16.0, 15/16.0,   15/16.0, 12/16.0);
    private static final AxisAlignedBB e = new AxisAlignedBB(4/16.0, 0.0D, 1/16.0, 12/16.0, 15/16.0, 15/16.0);
    private static final AxisAlignedBB w = new AxisAlignedBB(4/16.0, 0.0D, 1/16.0, 12/16.0, 15/16.0, 15/16.0);

    public BlockDistiller() {
        super(Material.ROCK, "distiller");
        this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public int getMetaFromState(IBlockState state){
        int meta = 0;
        meta |= state.getValue(FACING).getHorizontalIndex();
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        int facebits = meta & FACE;
        EnumFacing facing = EnumFacing.getHorizontal(facebits);
        return blockState.getBaseState().withProperty(FACING, facing);
    }

    @Override
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            player.openGui(Opaline.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
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
    public BlockDistiller setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(Opaline.creativeTab);
        return this;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        switch(state.getValue(FACING)) {
            case NORTH:
                return n;
            case SOUTH:
                return s;
            case EAST:
                return e;
            case WEST:
                return w;
            default:
                throw new AssertionError("Case missing for Distiller AABB"); //this should NEVER happen
        }
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

    @Override
    public Block toBlock(){
        return this;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
}
