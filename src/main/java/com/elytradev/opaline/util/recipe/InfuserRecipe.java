package com.elytradev.opaline.util.recipe;

import com.elytradev.concrete.recipe.ICustomRecipe;
import com.elytradev.concrete.recipe.IIngredient;
import com.elytradev.concrete.recipe.ItemIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

import javax.swing.plaf.basic.BasicComboBoxUI;

public class InfuserRecipe implements ICustomRecipe<InfuserRecipe, ItemStack> {

    protected ResourceLocation registryName;
    protected int processTime;
    protected ItemIngredient catalyst;
    protected ItemIngredient ingredient;
    protected ItemStack output;

    public InfuserRecipe(ItemStack output, ItemIngredient catalyst, ItemIngredient ingredient, int processTime) {
        this.output = output;
        this.catalyst = catalyst;
        this.ingredient = ingredient;
        this.processTime = processTime;
    }

    @Override
    public InfuserRecipe setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    public Integer getProcessTime() {
        return this.processTime;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public Class<InfuserRecipe> getRegistryType() {
        return InfuserRecipe.class;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    public boolean matches(IItemHandler inventory) {
        boolean applyCheck = catalyst.apply(inventory.getStackInSlot(0)) && ingredient.apply(inventory.getStackInSlot(1));
        boolean extractCheck = !inventory.extractItem(0,1,true).isEmpty() && !inventory.extractItem(0,1,true).isEmpty();
        return applyCheck && extractCheck;
    }

    public void consumeIngredients(IItemHandler inventory) {
        inventory.extractItem(0, 1, false);
        inventory.extractItem(1, 1, false);
    }

}
