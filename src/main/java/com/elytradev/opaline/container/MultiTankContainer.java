package com.elytradev.opaline.container;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WBar;
import com.elytradev.concrete.inventory.gui.widget.WFluidBar;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.concrete.inventory.gui.widget.WPanel;
import com.elytradev.concrete.inventory.gui.widget.WPlainPanel;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.tile.TileEntityMultiTank;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class MultiTankContainer extends ConcreteContainer {

    private ResourceLocation redTankBG = new ResourceLocation(Opaline.modId,"textures/gui/red_tank_bg.png");
    private ResourceLocation redTankFG = new ResourceLocation(Opaline.modId,"textures/gui/red_tank_fg.png");
    private ResourceLocation blueTankBG = new ResourceLocation(Opaline.modId,"textures/gui/blue_tank_bg.png");
    private ResourceLocation blueTankFG = new ResourceLocation(Opaline.modId,"textures/gui/blue_tank_fg.png");
    private ResourceLocation greenTankBG = new ResourceLocation(Opaline.modId,"textures/gui/green_tank_bg.png");
    private ResourceLocation greenTankFG = new ResourceLocation(Opaline.modId,"textures/gui/green_tank_fg.png");

    public MultiTankContainer(IInventory player, IInventory container, TileEntityMultiTank multiTank) {
        super(player, container);
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WPanel playerInv = this.createPlayerInventoryPanel();
        WFluidBar tankRed = new WFluidBar(redTankBG, redTankFG, multiTank.tankRed).withTooltip("%d/%d mB %3$s");
        WFluidBar tankBlue = new WFluidBar(blueTankBG, blueTankFG, multiTank.tankBlue).withTooltip("%d/%d mB %3$s");
        WFluidBar tankGreen = new WFluidBar(greenTankBG, greenTankFG, multiTank.tankGreen).withTooltip("%d/%d mB %3$s");
        panel.add(playerInv, 0, 87);
        panel.add(tankRed, 40, 16, 12, 40);
        panel.add(tankBlue, 112, 16, 12, 40);
        panel.add(tankGreen, 76, 16, 12, 40);
    }
}
