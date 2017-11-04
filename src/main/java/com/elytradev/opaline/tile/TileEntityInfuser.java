package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.*;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.util.FluidAccess;
import com.google.common.base.Predicates;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public class TileEntityInfuser extends TileEntity implements ITickable, IContainerInventoryHolder {

    public ConcreteFluidTank tank;
    public ConcreteItemStorage items;
    private int currentProcessTime;
    private static final int processLength = 500;
    private int currentFuelTime;
    private int maxFuelTime = 5;
    private int currentDischargeTicks;
    public int ticksExisted;
    public static final int SLOT_EXHAUSTED = 0;
    public static final int SLOT_BOOKSHELF = 1;
    public static final int SLOT_LAPIS = 2;

    public TileEntityInfuser() {
        this.tank = new ConcreteFluidTank(10).withFillValidator((it)->(it.getFluid() == ModBlocks.fluidOpaline));
        this.items = new ConcreteItemStorage(3).withValidators(
                (it)->(it.getItem() == ModItems.exhaustedLapis), (it)->(it.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF)), Validators.NOTHING)
                .setCanExtract(SLOT_EXHAUSTED, false)
                .setCanExtract(SLOT_BOOKSHELF, false)
                .withName(ModBlocks.infuser.getUnlocalizedName() + ".name");
        tank.listen(this::markDirty);
        items.listen(this::markDirty);
    }

    @SideOnly(Side.CLIENT)
    public int getTankScaled(int i){
        return this.tank.getFluidAmount()*i/this.tank.getCapacity();
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (processItem()) {
                if (consumeFuel()) currentProcessTime++; else dischargeProgress();
                if (items.getStackInSlot(SLOT_EXHAUSTED).isEmpty()) {
                    currentProcessTime = 0;
                }
                if (currentProcessTime >= processLength) {
                    items.extractItem(SLOT_EXHAUSTED, 1, false);
                    items.extractItem(SLOT_BOOKSHELF, 1, false);
                    items.insertItem(SLOT_LAPIS, new ItemStack(Items.DYE, 1, 4), false);
                    currentProcessTime = 0;
                }
            } else if (currentFuelTime > 0) {
                consumeFuel();
            }
        }
        ticksExisted++;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setTag("OutputTank", tank.writeToNBT(new NBTTagCompound()));
        tag.setTag("Inventory", items.serializeNBT());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tank.readFromNBT(compound.getCompoundTag("OutputTank"));
        items.deserializeNBT(compound.getCompoundTag("Inventory"));
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

    private boolean processItem() {
        ItemStack exhaustedExtracted = items.extractItem(SLOT_EXHAUSTED, 1, true);
        ItemStack bookshelfExtracted = items.extractItem(SLOT_BOOKSHELF, 1, true);
        ItemStack itemInserted = items.insertItem(SLOT_LAPIS, new ItemStack(Items.DYE, 1, 4), true);
        if (exhaustedExtracted.isEmpty()) {
            return false;
        } else if (bookshelfExtracted.isEmpty()) {
            return false;
        } else if (!itemInserted.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean consumeFuel() {
        if (currentFuelTime == 0) {
            FluidStack usedFuel = tank.drain(1, true);
            if (usedFuel != null && !items.getStackInSlot(SLOT_EXHAUSTED).isEmpty() && !items.getStackInSlot(SLOT_BOOKSHELF).isEmpty()) {
                currentFuelTime = maxFuelTime;
            } else {
                return false;
            }
        }
        currentFuelTime --;
        return true;
    }

    private boolean dischargeProgress() {
        if (currentDischargeTicks == 0 && currentProcessTime > 0) {
            currentProcessTime--;
            currentDischargeTicks = 5;
        } else {
            currentDischargeTicks--;
        }
        return true;
    }

    @Override
    public IInventory getContainerInventory() {
        ValidatedInventoryView view = new ValidatedInventoryView(items);
        if(world.isRemote) {
            return view;
        }
        else {
            return view.withField(0, () -> currentFuelTime)
                    .withField(1, () -> maxFuelTime)
                    .withField(2, () -> currentProcessTime)
                    .withField(3, () -> processLength);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        } else {
            return super.hasCapability(capability, facing);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) FluidAccess.insertOnly(tank);
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) new ValidatedItemHandlerView(items);
        } else {
            return super.getCapability(capability, facing);
        }
    }
}
