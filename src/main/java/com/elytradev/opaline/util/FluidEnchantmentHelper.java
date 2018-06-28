package com.elytradev.opaline.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FluidEnchantmentHelper {

    public static NBTTagList getEnchantmentNBT(FluidStack fluid) {
        NBTTagCompound nbttagcompound = fluid.tag;
        return nbttagcompound != null ? nbttagcompound.getTagList("StoredEnchantments", 10) : new NBTTagList();
    }

    public static List<Enchantment> getEnchantments(FluidStack fluid) {

        List<Enchantment> list = new ArrayList<Enchantment>();
        NBTTagList enchants = getEnchantmentNBT(fluid);

        for (int i = 0; i < enchants.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = enchants.getCompoundTagAt(i);
            Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getShort("id"));
            list.add(enchantment);
        }

        return list;
    }

    public static void addEnchantment(FluidStack fluid, EnchantmentData ench) {
        NBTTagList nbttaglist = getEnchantmentNBT(fluid);

            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short)Enchantment.getEnchantmentID(ench.enchantment));
            nbttaglist.appendTag(nbttagcompound1);

        if (fluid.tag == null) {
            fluid.tag = new NBTTagCompound();
        }

        fluid.tag.setTag("StoredEnchantments", nbttaglist);
    }

    public static void setEnchantments(List<Enchantment> enchList, FluidStack fluid) {
        NBTTagList nbttaglist = new NBTTagList();

        for (Enchantment enchantment : enchList) {
            if (enchantment != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setShort("id", (short)Enchantment.getEnchantmentID(enchantment));
                nbttaglist.appendTag(nbttagcompound);

                addEnchantment(fluid, new EnchantmentData(enchantment, 0));
            }
        }

        if (nbttaglist.hasNoTags()) {
            if (fluid.tag != null) fluid.tag.removeTag("ench");
        }
        else fluid.tag.setTag("ench", nbttaglist);
    }
}
