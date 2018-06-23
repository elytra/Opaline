package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.*;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.util.FluidAccess;
import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityCentrifuge extends TileEntity implements ITickable, IContainerInventoryHolder {

    public ConcreteItemStorage inv;
    public ConcreteFluidTank tankInRed;
    public ConcreteFluidTank tankInGreen;
    public ConcreteFluidTank tankOut;
    private int currentProcessTime;
    private static final int processLength = 50;


    public TileEntityCentrifuge() {
        this.tankInRed = new ConcreteFluidTank(1000).withFillValidator((it)->(it.getFluid() == ModBlocks.fluidLazurite));
        this.tankInGreen = new ConcreteFluidTank(1000).withFillValidator((it)->(it.getFluid() == ModBlocks.fluidLazurite));
        this.tankOut = new ConcreteFluidTank(2000).withFillValidator(Validators.NO_FLUID);
        this.inv = new ConcreteItemStorage(0).withName(ModBlocks.centrifuge.getUnlocalizedName() + ".name");
        tankInRed.listen(this::markDirty);
        tankInGreen.listen(this::markDirty);
        tankOut.listen(this::markDirty);
    }

    public void update() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setTag("InputTankRed", tankInRed.writeToNBT(new NBTTagCompound()));
        tag.setTag("InputTankGreen", tankInRed.writeToNBT(new NBTTagCompound()));
        tag.setTag("OutputTank", tankOut.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tankInRed.readFromNBT(compound.getCompoundTag("InputTankRed"));
        tankInGreen.readFromNBT(compound.getCompoundTag("InputTankGreen"));
        tankOut.readFromNBT(compound.getCompoundTag("OutputTank"));
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
        Chunk c = getWorld().getChunkFromBlockCoords(getPos());
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
        if(world.isRemote) {
            return view;
        }
        else {
            return view.withField(0, () -> currentProcessTime)
                    .withField(1, () -> processLength);
        }
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
            switch (facing) {
                case NORTH:
                    return (T) FluidAccess.insertOnly(tankInRed);
                case EAST:
                    return (T) FluidAccess.extractOnly(tankOut);
                case SOUTH:
                    return (T) FluidAccess.insertOnly(tankInGreen);
                case WEST:
                    return (T) FluidAccess.extractOnly(tankOut);
            }
            return (T) FluidAccess.readOnly(tankOut);
        } else {
            return super.getCapability(capability, facing);
        }
    }
}
