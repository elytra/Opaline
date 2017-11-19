package com.elytradev.opaline.recipe;

import com.elytradev.concrete.recipe.ICustomRecipe;
import com.elytradev.concrete.recipe.IIngredient;
import com.elytradev.concrete.recipe.ItemIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class InfuserRecipe implements ICustomRecipe<InfuserRecipe, ItemStack> {

    protected ResourceLocation registryName;
    protected ItemStack output;
    protected IIngredient<Integer> processTime;
    protected ItemIngredient exhaustedLapis;
    protected ItemIngredient catalyst;

    @Override
    public InfuserRecipe setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
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

}
