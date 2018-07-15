package com.elytradev.opaline.tile;

import com.elytradev.concrete.inventory.*;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.util.FluidAccess;
import com.elytradev.opaline.util.recipe.InfuserRecipe;
import com.elytradev.opaline.util.recipe.MachineRecipes;
import com.google.common.base.Predicates;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TileEntityInfuser extends TileEntity implements ITickable, IContainerInventoryHolder {

    public ConcreteFluidTank tank;
    public ConcreteItemStorage items;
    private int currentProcessTime;
    private int processLength = 1;
    private int currentFuelTime;
    private int maxFuelTime = 5;
    private int currentDischargeTicks = 0;
    private int cooldown;
    private static final int maxCooldown = 40;
    public int ticksExisted;
    public static final int SLOT_CATALYST = 0;
    public static final int SLOT_INGREDIENT = 1;
    public static final int SLOT_LAPIS = 2;
    public static final int MAX_PERCENT = 100;
    public int currentPercent;

    public static boolean oreMatches(String oreName, ItemStack stack) {
        if (oreName == null) return false;
        int oreId = OreDictionary.getOreID(oreName);
        return ArrayUtils.contains(OreDictionary.getOreIDs(stack), oreId);
    }

    public static final Predicate<ItemStack> INGREDIENTS = (it) -> {
        if (oreMatches("gemLapis", it) || oreMatches("gemQuartz", it)) return true;
        else return false;
    };

    public TileEntityInfuser() {
        this.tank = new ConcreteFluidTank(10).withFillValidator((it)->(it.getFluid() == ModBlocks.fluidOpaline));
        this.items = new ConcreteItemStorage(3).withValidators(
                (it)->(it.getItem() == ModItems.EXHAUSTED_LAPIS), INGREDIENTS, Validators.NOTHING)
                .setCanExtract(SLOT_CATALYST, false)
                .setCanExtract(SLOT_INGREDIENT, false)
                .withName(ModBlocks.INFUSER.getTranslationKey() + ".name");
        tank.listen(this::markDirty);
        items.listen(this::markDirty);
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (cooldown>0) cooldown--;
            if (cooldown>0) {
                this.markDirty();
                return;
            }

            InfuserRecipe recipe = MachineRecipes.getInfuser(items);
            if (recipe!=null) {
                processLength = recipe.getProcessTime();
                boolean canFillOutput = items.insertItem(SLOT_LAPIS, recipe.getOutput(), true).isEmpty();
                if (recipe.matches(items) && canFillOutput) {
                    if (consumeFuel()) currentProcessTime++;
                    else dischargeProgress();
                    if (items.getStackInSlot(SLOT_CATALYST).isEmpty()) {
                        currentProcessTime = 0;
                    }
                    if (currentProcessTime >= processLength) {
                        recipe.consumeIngredients(items);
                        items.insertItem(SLOT_LAPIS, recipe.getOutput().copy(), false);
                        currentProcessTime = 0;
                        cooldown = maxCooldown;
                    }
                } else if (currentFuelTime > 0) {
                    consumeFuel();
                }
            } else {
                cooldown = maxCooldown;
            }
            if (processLength == 0) {
                processLength = 1;
            }
            currentPercent = 100 * currentProcessTime / processLength;
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
        Chunk c = getWorld().getChunk(getPos());
        SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
        for (EntityPlayerMP player : getWorld().getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
            if (ws.getPlayerChunkMap().isPlayerWatchingChunk(player, c.x, c.z)) {
                player.connection.sendPacket(packet);
            }
        }
    }

    private boolean consumeFuel() {
        if (currentFuelTime == 0) {
            FluidStack usedFuel = tank.drain(1, true);
            if (usedFuel != null && MachineRecipes.getInfuser(items) != null) {
                currentFuelTime = maxFuelTime;
            } else {
                return false;
            }
        }
        currentFuelTime --;
        return true;
    }

    private boolean dischargeProgress() {
        if (currentProcessTime > 0) {
            if (currentDischargeTicks <= 0) {
                currentProcessTime--;
                currentDischargeTicks = 5;
            } else {
                currentDischargeTicks--;
            }
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
                    .withField(2, () -> currentPercent)
                    .withField(3, () -> MAX_PERCENT);
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
