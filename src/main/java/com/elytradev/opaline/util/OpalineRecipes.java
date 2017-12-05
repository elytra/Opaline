package com.elytradev.opaline.util;

import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.util.recipe.InfuserRecipe;
import com.elytradev.opaline.util.recipe.MachineRecipes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public class OpalineRecipes {

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {

        IForgeRegistry<IRecipe> r = event.getRegistry();

        // Crafting bench recipes

        recipe(r, new ShapedOreRecipe(new ResourceLocation("opaline:blocks"), new ItemStack(ModBlocks.distiller, 1),
                "bc ", "g b", "sss",
                'b', new ItemStack(Items.GLASS_BOTTLE),
                's', new ItemStack(Blocks.STONE_SLAB),
                'c', "ingotCopper",
                'g', "glass"));

        recipe(r, new ShapedOreRecipe(new ResourceLocation("opaline:blocks"), new ItemStack(ModBlocks.infuser,1),
                "b  ", "sss", "scs",
                'b', new ItemStack(Items.GLASS_BOTTLE),
                's', new ItemStack(Blocks.STONE_SLAB),
                'c', "cobblestone"));

        // Infuser recipes

        ItemIngredient exhaustedLapis = ItemIngredient.of(ModItems.exhaustedLapis);
        ItemIngredient lapis = ItemIngredient.of("gemLapis");

        InfuserRecipe remakeLapis = new InfuserRecipe(new ItemStack(Items.DYE, 2, 4),
                exhaustedLapis,
                lapis, 500);
        MachineRecipes.register(remakeLapis);

    }


    public static <T extends IRecipe> T recipe(IForgeRegistry<IRecipe> registry, T t) {
        t.setRegistryName(new ResourceLocation(t.getRecipeOutput().getItem().getRegistryName()+"_"+t.getRecipeOutput().getItemDamage()));
        registry.register(t);
        return t;
    }
}
