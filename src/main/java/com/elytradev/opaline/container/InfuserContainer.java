package com.elytradev.opaline.container;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WBar;
import com.elytradev.concrete.inventory.gui.widget.WFluidBar;
import com.elytradev.concrete.inventory.gui.widget.WItemSlot;
import com.elytradev.concrete.inventory.gui.widget.WPanel;
import com.elytradev.concrete.inventory.gui.widget.WPlainPanel;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.tile.TileEntityInfuser;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class InfuserContainer extends ConcreteContainer {

    private ResourceLocation tankBG = new ResourceLocation(Opaline.modId,"textures/gui/small_tank_bg.png");
    private ResourceLocation tankFG = new ResourceLocation(Opaline.modId,"textures/gui/small_tank_fg.png");
    private ResourceLocation arrowBG = new ResourceLocation(Opaline.modId,"textures/gui/arrow_bg.png");
    private ResourceLocation arrowFG = new ResourceLocation(Opaline.modId,"textures/gui/arrow_fg.png");

    public InfuserContainer(IInventory player, IInventory container, TileEntityInfuser infuser) {
        super(player, container);
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WItemSlot slotCatalyst = WItemSlot.of(container, 0);
        WItemSlot slotIngredient = WItemSlot.of(container, 1);
        WItemSlot slotLapis = WItemSlot.outputOf(container, 2);
        WPanel playerInv = this.createPlayerInventoryPanel();
        WFluidBar tankOpaline = new WFluidBar(tankBG, tankFG, infuser.tank).withTooltip("%d/%d mB");
        WBar progressTicks = new WBar(arrowBG, arrowFG, container, 2, 3, WBar.Direction.RIGHT).withTooltip("%d/%d ticks");
        panel.add(slotCatalyst, 48, 16);
        panel.add(slotIngredient, 48, 52);
        panel.add(slotLapis, 105, 33);
        panel.add(playerInv, 0, 87);
        panel.add(tankOpaline, 50, 35, 12, 14);
        panel.add(progressTicks, 72, 34, 24, 17);
    }
}