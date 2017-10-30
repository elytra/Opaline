package com.elytradev.opaline.container;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WFluidBar;
import com.elytradev.concrete.inventory.gui.widget.WGridPanel;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.tile.TileEntityDistiller;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class DistillerContainer extends ConcreteContainer {

    private ResourceLocation distillerBG = new ResourceLocation(Opaline.modId,"textures/gui/distiller_bg.png");
    private ResourceLocation distillerFG = new ResourceLocation(Opaline.modId,"textures/gui/distiller_fg.png");

    public DistillerContainer(IInventory player, IInventory container, TileEntityDistiller distiller) {
        super(player, container);
        WGridPanel panel = new WGridPanel();
        setRootPanel(panel);
        WItemSlot slotLapis = WItemSlot.of(container, 0);
        WItemSlot slotFuel = WItemSlot.of(container, 1);
        WItemSlot playerInv = WItemSlot.ofPlayerStorage(player);
        WItemSlot playerHot = WItemSlot.of(player, 0, 9, 1);
        WFluidBar tankOpaline = new WFluidBar(distillerBG, distillerFG, distiller.tank);
        panel.add(slotLapis, 2, 1);
        panel.add(slotFuel, 2, 3);
        panel.add(playerInv, 0, 5);
        panel.add(playerHot, 0, 8);
        panel.add(tankOpaline, 6, 1, 1, 3);
    }
}
