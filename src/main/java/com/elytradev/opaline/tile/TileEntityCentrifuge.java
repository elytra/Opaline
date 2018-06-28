package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.*;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.network.PacketButtonClick;
import com.elytradev.opaline.util.FluidAccess;
import com.elytradev.opaline.util.FluidEnchantmentHelper;
import com.elytradev.opaline.util.OpalineLog;
import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityCentrifuge extends TileEntity implements ITickable, IContainerInventoryHolder {
    public ConcreteItemStorage inv;
    public ConcreteFluidTank tankInRed;
    public ConcreteFluidTank tankInGreen;
    public ConcreteFluidTank tankOut;
    private int currentProcessTime = 0;
    private int processLength = 50;
    private int mode;
    public boolean isRunning;
    private static final int MAX_PERCENT = 100;
    private int currentPercent;

    private FluidStack enchLazurite = new FluidStack(ModBlocks.fluidLazurite, 1);

    public TileEntityCentrifuge() {
        this.tankInRed = new ConcreteFluidTank(1000).withFillValidator((it)->(it.getFluid() == ModBlocks.fluidLazurite));
        this.tankInGreen = new ConcreteFluidTank(1000).withFillValidator((it)->(it.getFluid() == ModBlocks.fluidLazurite));
        this.tankOut = new ConcreteFluidTank(2000).withFillValidator(Validators.NO_FLUID);
        this.inv = new ConcreteItemStorage(0).withName(ModBlocks.CENTRIFUGE.getUnlocalizedName() + ".name");
        tankInRed.listen(this::markDirty);
        tankInGreen.listen(this::markDirty);
        tankOut.listen(this::markDirty);
    }

    public void update() {
        if (!isRunning || world.isRemote) return;
        if (currentProcessTime < processLength) {
            switch (mode) {
                case 0:
                    processCombine();
                    break;
                case 1:
                    processMerge(tankInGreen, tankInRed);
                    break;
                case 2:
                    processMerge(tankInRed, tankInGreen);
                    break;
                case 3:
                    processDissolve();
                    break;
                default:
                    break;
            }
            currentProcessTime++;
        } else {
            isRunning = false;
            currentProcessTime = 0;
            markDirty();
        }
        currentPercent = 100 * currentProcessTime / processLength;
    }

    private void processCombine() {
        int out = tankOut.fill(enchLazurite, true);
        if (out == 1) {
            tankInRed.drain(1, true);
            tankInGreen.drain(1, true);
        }
    }

    private void processMerge(ConcreteFluidTank nbt, ConcreteFluidTank volume) {
        int out = tankOut.fill(enchLazurite, true);
        if (out == 1) {
            volume.drain(1, true);
            if (volume.getFluidAmount() != 0) nbt.drain((nbt.getFluidAmount()/volume.getFluidAmount())+1, true);
        }
    }

    private void processDissolve() {
        FluidStack singleOpaline = new FluidStack(ModBlocks.fluidOpaline, 2);
        if (tankInRed.getFluidAmount() != 0) {
            int redOut = tankOut.fill(singleOpaline, true);
            if (redOut == 2) tankInRed.drain(1, true);
        }
        if (tankInGreen.getFluidAmount() != 0) {
            int greenOut = tankOut.fill(singleOpaline, true);
            if (greenOut == 2) tankInGreen.drain(1, true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setTag("InputTankRed", tankInRed.writeToNBT(new NBTTagCompound()));
        tag.setTag("InputTankGreen", tankInGreen.writeToNBT(new NBTTagCompound()));
        tag.setTag("OutputTank", tankOut.writeToNBT(new NBTTagCompound()));
        tag.setInteger("Mode", mode);
        tag.setBoolean("Running", isRunning);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tankInRed.readFromNBT(compound.getCompoundTag("InputTankRed"));
        tankInGreen.readFromNBT(compound.getCompoundTag("InputTankGreen"));
        tankOut.readFromNBT(compound.getCompoundTag("OutputTank"));
        mode = compound.getInteger("Mode");
        isRunning = compound.getBoolean("Running");
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
            return view.withField(0, () -> currentPercent)
                    .withField(1, () -> MAX_PERCENT);
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
                    if (isRunning) return (T) FluidAccess.readOnly(tankInRed); else return (T) FluidAccess.fullAccess(tankInRed);
                case EAST:
                    if (isRunning) return (T) FluidAccess.readOnly(tankInGreen); else return (T) FluidAccess.fullAccess(tankInGreen);
                case SOUTH:
                    return (T) FluidAccess.extractOnly(tankOut);
                case WEST:
                    return (T) FluidAccess.extractOnly(tankOut);
            }
            return (T) FluidAccess.readOnly(tankOut);
        } else {
            return super.getCapability(capability, facing);
        }
    }

    public int getState() {
        return this.mode;
    }

    public void increaseState() {
        mode++;
        if (mode >= 4) mode = 0;
        this.markDirty();
    }

    public void decreaseState() {
        mode--;
        if (mode < 0) mode = 3;
        this.markDirty();
    }

    public void startRunning() {
        if (isRunning) return;
        if (mode == 3 && (tankInGreen.getFluidAmount() != 0 || tankInRed.getFluidAmount() != 0) && tankOut.getFluid() != new FluidStack(ModBlocks.fluidLazurite, 1)) {
            this.processLength = getDissolveLength();
            this.currentProcessTime = 0;
            this.isRunning = true;
        } else if (canFluidsMerge()) {
            switch (mode) {
                case 0:
                    break;
                case 1:
                    if (tankInRed.getFluidAmount() < (tankOut.getCapacity()-tankOut.getFluidAmount())) {
                        if (tankInGreen.getFluid() == null) return;
                        FluidEnchantmentHelper.setEnchantments(FluidEnchantmentHelper.getEnchantments(tankInGreen.getFluid()), enchLazurite);
                        this.processLength = tankInRed.getFluidAmount();
                        this.currentProcessTime = 0;
                        this.isRunning = true;
                    }
                    break;
                case 2:
                    if (tankInGreen.getFluidAmount() < (tankOut.getCapacity()-tankOut.getFluidAmount())) {
                        if (tankInRed.getFluid() == null) return;
                        FluidEnchantmentHelper.setEnchantments(FluidEnchantmentHelper.getEnchantments(tankInRed.getFluid()), enchLazurite);
                        this.processLength = tankInGreen.getFluidAmount();
                        this.currentProcessTime = 0;
                        this.isRunning = true;
                    }
                    break;
                default:
                    break;
            }
        }
        this.markDirty();
    }

    public boolean canFluidsMerge() {
        if (tankOut.getFluidAmount()+tankInRed.getFluidAmount()+tankInRed.getFluidAmount() > tankOut.getCapacity()) return false;
        if (tankInRed.getFluid() == null || tankInGreen.getFluid() == null) return false;
        switch (mode) {
            case 0:
                break;
            case 1:
                return checkTankMatch(tankInGreen);
            case 2:
                return checkTankMatch(tankInRed);
            default:
                break;
        }
        return false;
    }

    public boolean checkTankMatch(ConcreteFluidTank from) {
        FluidStack fluid = from.getFluid();
        if (fluid == null) return false;

        NBTTagList tags = fluid.tag.getTagList("StoredEnchantments", 0);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("StoredEnchantments", tags);
        enchLazurite.tag = tag;

        int outTest = tankOut.fill(enchLazurite, false);
        return outTest == 1;
    }

    public int getDissolveLength() {
        int divisor;
        if ((tankInRed.getFluidAmount() != 0 && tankInGreen.getFluidAmount() == 0) || (tankInRed.getFluidAmount() == 0 && tankInGreen.getFluidAmount() != 0)) {
            divisor = 1;
        } else divisor = 2;
        if (2*(tankInRed.getFluidAmount()+tankInGreen.getFluidAmount()) >= (tankOut.getCapacity()-tankOut.getFluidAmount())) {
            divisor *= 2;
            if (tankOut.getFluidAmount() % 2 == 1) return (tankOut.getCapacity()-(tankOut.getFluidAmount()-1))/(divisor);
            else return (tankOut.getCapacity()-tankOut.getFluidAmount())/(divisor);
        }
        return ((tankInRed.getFluidAmount()+tankInGreen.getFluidAmount()))/(divisor);
    }
}
