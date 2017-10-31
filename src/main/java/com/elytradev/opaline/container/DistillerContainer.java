package com.elytradev.opaline.container;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.*;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.tile.TileEntityDistiller;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class DistillerContainer extends ConcreteContainer {

    private ResourceLocation distillerBG = new ResourceLocation(Opaline.modId,"textures/gui/distiller_bg.png");
    private ResourceLocation distillerFG = new ResourceLocation(Opaline.modId,"textures/gui/distiller_fg.png");
    private ResourceLocation fireBG = new ResourceLocation(Opaline.modId,"textures/gui/fire_bg.png");
    private ResourceLocation fireFG = new ResourceLocation(Opaline.modId,"textures/gui/fire_fg.png");
    private ResourceLocation arrowBG = new ResourceLocation(Opaline.modId,"textures/gui/arrow_bg.png");
    private ResourceLocation arrowFG = new ResourceLocation(Opaline.modId,"textures/gui/arrow_fg.png");

    public DistillerContainer(IInventory player, IInventory container, TileEntityDistiller distiller) {
        super(player, container);
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WItemSlot slotLapis = WItemSlot.of(container, 0);
        WItemSlot slotFuel = WItemSlot.of(container, 1);
        WItemSlot slotExhausted = WItemSlot.of(container, 2);
        WItemSlot playerInv = WItemSlot.ofPlayerStorage(player);
        WItemSlot playerHot = WItemSlot.of(player, 0, 9, 1);
        WFluidBar tankOpaline = new WFluidBar(distillerBG, distillerFG, distiller.tank);
        WBar fuelTicks = new WBar(fireBG, fireFG, container, 0, 1);
        WBar progressTicks = new WBar(arrowBG, arrowFG, container, 2, 3);
        panel.add(slotLapis, 48, 16);
        panel.add(slotFuel, 48, 52);
        panel.add(slotExhausted, 101, 52);
        panel.add(playerInv, 0, 87);
        panel.add(playerHot, 0, 145);
        panel.add(tankOpaline, 103, 10, 12, 40);
        panel.add(fuelTicks, 48, 35, 16, 16);
        panel.add(progressTicks, 72, 34, 24, 17);
    }
}
