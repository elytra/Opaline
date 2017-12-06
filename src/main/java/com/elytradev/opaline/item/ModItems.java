package com.elytradev.opaline.item;

import com.elytradev.opaline.Opaline;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

        public static ItemBase exhaustedLapis = new ItemBase("exhausted_lapis", "dyeBlue").setCreativeTab(Opaline.creativeTab);
        public static ItemBase quartzLapis = new ItemBase("quartz_lapis").setCreativeTab(Opaline.creativeTab);
        public static itemDebugger logoFake = new itemDebugger("logo_fake");

    public static ItemBase[] allItems = {
            exhaustedLapis, logoFake, quartzLapis
    };

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(allItems);
    }

    public static void registerModels() {
        for (int i = 0; i < allItems.length ; i++) {
            allItems[i].registerItemModel();
        }
    }

    public static void registerOreDict() {
        for (int i = 0; i < allItems.length ; i++) {
            allItems[i].initOreDict();
        }
    }
}
