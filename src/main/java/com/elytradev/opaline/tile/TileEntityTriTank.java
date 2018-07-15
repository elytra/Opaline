package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.ConcreteFluidTank;
import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.util.FluidAccess;
import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityTriTank extends TileEntity implements IContainerInventoryHolder {

    public ConcreteItemStorage inv;
    public ConcreteFluidTank tankRed;
    public ConcreteFluidTank tankBlue;
    public ConcreteFluidTank tankGreen;

    public TileEntityTriTank() {
        this.inv = new ConcreteItemStorage(0).withName(ModBlocks.TRI_TANK.getTranslationKey() + ".name");
        this.tankRed = new ConcreteFluidTank(1000);
        this.tankBlue = new ConcreteFluidTank(1000);
        this.tankGreen = new ConcreteFluidTank(1000);
        tankRed.listen(this::markDirty);
        tankBlue.listen(this::markDirty);
        tankGreen.listen(this::markDirty);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setTag("TankRed", tankRed.writeToNBT(new NBTTagCompound()));
        tag.setTag("TankBlue", tankBlue.writeToNBT(new NBTTagCompound()));
        tag.setTag("TankGreen", tankGreen.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tankRed.readFromNBT(compound.getCompoundTag("TankRed"));
        tankBlue.readFromNBT(compound.getCompoundTag("TankBlue"));
        tankGreen.readFromNBT(compound.getCompoundTag("TankGreen"));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        // again, I've copy-pasted this like 12 times, should probably go into Concrete
        if (!hasWorld() || getWorld().isRemote) return;
        WorldServer ws = (WorldServer)getWorld();
        Chunk c = getWorld().getChunk(getPos());
        SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
        for (EntityPlayerMP player : getWorld().getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
            if (ws.getPlayerChunkMap().isPlayerWatchingChunk(player, c.x, c.z)) {
                player.connection.sendPacket(packet);
            }
        }
    }

    @Override
    public IInventory getContainerInventory() {
        ValidatedInventoryView view = new ValidatedInventoryView(inv);
        return view;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        } else {
            return super.hasCapability(capability, facing);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (facing == null) {
                // return a read-only view of all the tanks
                throw new RuntimeException("TODO");
            }
            switch (facing) {
                case NORTH:
                case SOUTH:
                    return (T) FluidAccess.fullAccess(tankBlue);
                case EAST:
                case WEST:
                    return (T) FluidAccess.fullAccess(tankRed);
                case UP:
                case DOWN:
                    return (T) FluidAccess.fullAccess(tankGreen);
                default:
                    throw new AssertionError("missing case for "+facing);
            }
        } else  {
            return super.getCapability(capability, facing);
        }
    }
}
