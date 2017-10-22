package com.elytradev.opaline.item;

import com.elytradev.opaline.Opaline;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static ItemBase temp = new ItemBase("temp").setCreativeTab(Opaline.creativeTab);

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                temp
        );
    }

    public static void registerModels() {
        temp.registerItemModel();
    }
}
