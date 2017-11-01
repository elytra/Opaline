package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.*;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.util.FluidAccess;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
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
    private int currentProcessTime;
    private static final int processLength = 200;
    private int currentFuelTime;
    private int maxFuelTime;
    public static final int SLOT_LAPIS = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_EXHAUSTED = 2;

    public static boolean oreMatches(String oreName, ItemStack stack) {
        if (oreName == null) return false;
        int oreId = OreDictionary.getOreID(oreName);
        return ArrayUtils.contains(OreDictionary.getOreIDs(stack), oreId);
    }

    public TileEntityDistiller() {
        this.tank = new ConcreteFluidTank(300).withFillValidator((it)->false);
        this.items = new ConcreteItemStorage(3).withValidators(
            (it)->oreMatches("gemLapis", it), Validators.FURNACE_FUELS, Validators.NOTHING)
            .setCanExtract(SLOT_LAPIS, false)
            .setCanExtract(SLOT_FUEL, false)
            .withName(ModBlocks.distiller.getUnlocalizedName() + ".name");
        tank.listen(this::markDirty);
        items.listen(this::markDirty);
    }

    @SideOnly(Side.CLIENT)
    public int getTankScaled(int i){
        return this.tank.getFluidAmount()*i/this.tank.getCapacity();
    }

    public void update() {
        if(world.isRemote) {
            //System.out.println("currentProcessTime: " + currentProcessTime + ", currentFuelTime: " + currentFuelTime);
            if (consumeFuel()) currentProcessTime++;
            if (items.getStackInSlot(SLOT_LAPIS).isEmpty()) {
                currentProcessTime = 0;
            }
            if (currentProcessTime >= processLength) {
                processItem();
                currentProcessTime = 0;
            }
        }
    }

    private boolean processItem() {
        ItemStack itemExtracted = items.extractItem(SLOT_LAPIS, 1, true);
        ItemStack itemInserted = items.insertItem(SLOT_EXHAUSTED, new ItemStack(ModItems.exhaustedLapis, 1), true);
        int tankFilled = tank.fill(new FluidStack(ModBlocks.fluidOpaline, 100), false);
        if (itemExtracted.isEmpty()) {
            System.out.println("failed to extract " + itemExtracted + ", isEmpty " + itemExtracted.isEmpty());
            return false;
        } else if (!itemInserted.isEmpty()) {
            System.out.println("failed to insert " + itemInserted + ", isEmpty " + itemExtracted.isEmpty());
            return false;
        } else if (tankFilled != 100) {
            System.out.println("failed to insert fluid, inserted " + tankFilled + " mb total");
            return false;
        } else {
            System.out.println("all simulations succeeded, processing items");
            items.extractItem(SLOT_LAPIS, 1, false);
            items.insertItem(SLOT_EXHAUSTED, new ItemStack(ModItems.exhaustedLapis, 1), false);
            tank.fill(new FluidStack(ModBlocks.fluidOpaline, 100), true);
            return true;
        }
    }

    private boolean consumeFuel() {
        if (currentFuelTime == 0) {
            ItemStack usedFuel = items.extractItem(SLOT_FUEL, 1, false);
            if (!usedFuel.isEmpty() && !items.getStackInSlot(SLOT_LAPIS).isEmpty()) {
                int newFuelTicks = TileEntityFurnace.getItemBurnTime(usedFuel);
                maxFuelTime = newFuelTicks;
                currentFuelTime = newFuelTicks;
                System.out.println("New fuel added! At " + currentFuelTime + " out of " + maxFuelTime + " fuel ticks.");
            } else {
                return false;
            }
        }
        currentFuelTime --;
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
            return (T) FluidAccess.extractOnly(tank);
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) new ValidatedItemHandlerView(items);
        } else {
            return super.getCapability(capability, facing);
        }
    }
}
