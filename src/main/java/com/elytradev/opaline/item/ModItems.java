package com.elytradev.opaline.item;

import com.elytradev.opaline.Opaline;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

        public static ItemBase EXHAUSTED_LAPIS = new ItemBase("exhausted_lapis", "dyeBlue").setCreativeTab(Opaline.creativeTab);
        public static ItemBase QUARTZ_LAPIS = new ItemBase("quartz_lapis").setCreativeTab(Opaline.creativeTab);
        public static itemDebugger LOGO_FAKE = new itemDebugger("logo_fake");

    public static ItemBase[] allItems = {
            EXHAUSTED_LAPIS, LOGO_FAKE, QUARTZ_LAPIS
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
