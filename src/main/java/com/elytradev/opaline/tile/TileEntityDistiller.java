package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.*;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.util.FluidAccess;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public class TileEntityDistiller extends TileEntity implements ITickable, IContainerInventoryHolder {

    public ConcreteFluidTank tank;
    public ConcreteItemStorage items;
    private int processTime;
    private int lastTankAmount;
    private int lastProcessTime;
    private static final int processLength = 200;
    public static final int slotLapis = 0;
    public static final int slotFuel = 1;

    public static boolean oreMatches(String oreName, ItemStack stack) {
        if (oreName == null) return false;
        int oreId = OreDictionary.getOreID(oreName);
        return ArrayUtils.contains(OreDictionary.getOreIDs(stack), oreId);
    }

    public TileEntityDistiller() {
        this.tank = new ConcreteFluidTank(300).withFillValidator((it)->false);
        this.items = new ConcreteItemStorage(2).withValidators(
            (it)->oreMatches("gemLapis", it)).setCanExtract(slotLapis, false).setCanExtract(slotFuel, false)
            .withName(ModBlocks.distiller.getUnlocalizedName() + ".name");
        tank.listen(this::markDirty);
    }

    @SideOnly(Side.CLIENT)
    public int getTankScaled(int i){
        return this.tank.getFluidAmount()*i/this.tank.getCapacity();
    }

    public void update() {
    lastProcessTime ++;
    if (lastProcessTime == processLength) {

        }
    }

    @Override
    public IInventory getContainerInventory() {
        ValidatedInventoryView view = new ValidatedInventoryView(items);
        if(world.isRemote) {
            return view;
        }
        else {
            return view.withField(0, ()->0) //TODO: Current fuel ticks
            .withField(1, ()->100) //TODO: Max fuel ticks
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
            return (T) FluidAccess.extractOnly(tank);
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) new ValidatedItemHandlerView(items);
        } else {
            return super.getCapability(capability, facing);
        }
    }
}
