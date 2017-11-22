package com.elytradev.opaline.util.recipe;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraftforge.items.IItemHandler;

public class MachineRecipes {
    protected static Set<InfuserRecipe> infuser = new HashSet<InfuserRecipe>();

    public static void register(InfuserRecipe recipe) {
        infuser.add(recipe);
    }

    @Nullable
    public static InfuserRecipe getInfuser(IItemHandler inv) {
        for(InfuserRecipe recipe : infuser) {
            if (recipe.matches(inv)) return recipe;
        }

        return null;
    }
}