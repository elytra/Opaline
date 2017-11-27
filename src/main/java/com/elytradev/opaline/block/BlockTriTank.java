package com.elytradev.opaline.block;

import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.tile.TileEntityTriTank;
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

public class BlockTriTank extends BlockTileEntity<TileEntityTriTank> implements IBlockBase {

    public static PropertyDirection FACING = BlockHorizontal.FACING;
    public static int FACE = 3;

    private static final AxisAlignedBB n = new AxisAlignedBB(0.0, 0.0D, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB s = new AxisAlignedBB(0.0, 0.0D, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB e = new AxisAlignedBB(0.0, 0.0D, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB w = new AxisAlignedBB(0.0, 0.0D, 0.0, 1.0, 1.0, 1.0);

    public BlockTriTank() {
        super(Material.ROCK, "tri_tank");
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
        if(!world.isRemote && !player.isSneaking()) {
            player.openGui(Opaline.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public Class<TileEntityTriTank> getTileEntityClass() {
        return TileEntityTriTank.class;
    }

    @Override
    public TileEntityTriTank createTileEntity(World world, IBlockState state) {
        return new TileEntityTriTank();
    }

    @Override
    public BlockTriTank setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(Opaline.creativeTab);
        return this;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityTriTank tile = getTileEntity(world, pos);
        super.breakBlock(world, pos, state);
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
                throw new AssertionError("Case missing for Tri-Tank AABB"); //this should NEVER happen
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
