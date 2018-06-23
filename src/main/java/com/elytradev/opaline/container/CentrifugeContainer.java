package com.elytradev.opaline.container;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.*;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.container.widget.WCycleButton;
import com.elytradev.opaline.tile.TileEntityCentrifuge;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class CentrifugeContainer extends ConcreteContainer {

    private ResourceLocation inRedBG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/in_red_bg.png");
    private ResourceLocation inRedFG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/in_red_fg.png");
    private ResourceLocation inGreenBG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/in_green_bg.png");
    private ResourceLocation inGreenFG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/in_green_fg.png");
    private ResourceLocation outBG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/out_bg.png");
    private ResourceLocation outFG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/out_fg.png");
    private ResourceLocation arrowBG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/arrow_bg.png");
    private ResourceLocation arrowFG = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/arrow_fg.png");
    private ResourceLocation buttonEnabled = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/enabled.png");
    private ResourceLocation buttonDisabled = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/disabled.png");
    private ResourceLocation buttonGoEnabled = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/go_enabled.png");
    private ResourceLocation buttonGoDisabled = new ResourceLocation(Opaline.modId,"textures/gui/centrifuge/go_disabled.png");
    private static ResourceLocation[] buttonModes = new ResourceLocation[4];
    static {
        for (int i = 0; i <= 3; i++) {
            buttonModes[i] = new ResourceLocation(Opaline.modId, "textures/gui/centrifuge/mode_" + i + ".png");
        }
    }

    public CentrifugeContainer(IInventory player, IInventory container, TileEntityCentrifuge centrifuge) {
        super(player, container);
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WPanel playerInv = this.createPlayerInventoryPanel();
        WFluidBar tankInRed = new WFluidBar(inRedBG, inRedFG, centrifuge.tankInRed).withTooltip("%d/%d mB");
        WFluidBar tankInGreen = new WFluidBar(inGreenBG, inGreenFG, centrifuge.tankInGreen).withTooltip("%d/%d mB");
        WFluidBar tankOut = new WFluidBar(outBG, outFG, centrifuge.tankOut).withTooltip("%d/%d mB");
        WBar progress = new WBar(arrowBG, arrowFG, container, 0, 1);
        String[] tooltips = new String[4];
        tooltips[0] = "Combine";
        tooltips[1] = "Merge Left";
        tooltips[2] = "Merge Right";
        tooltips[3] = "Dissolve";
        WCycleButton mode = new WCycleButton(buttonEnabled, buttonDisabled, this::increaseMode, this::decreaseMode, buttonModes).withTooltip(tooltips);
        WClientButton go = new WClientButton(buttonGoEnabled, buttonGoDisabled, this::go).withTooltip("Activate");
        panel.add(playerInv, 0, 87);
        panel.add(tankInRed, 26, 17, 28, 20);
        panel.add(tankInGreen, 108, 17, 28, 20);
        panel.add(tankOut, 67, 48, 28, 30);
        panel.add(progress, 62, 32, 38, 16);
        panel.add(mode, 66, 16, 12, 12);
        panel.add(go, 84, 16, 12, 12);
    }

    public void increaseMode() {

    }

    public void decreaseMode() {

    }

    public void go() {

    }
}