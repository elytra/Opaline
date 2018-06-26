package com.elytradev.opaline.container;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.*;
import com.elytradev.opaline.Opaline;
import com.elytradev.opaline.container.widget.WCentrCycleButton;
import com.elytradev.opaline.container.widget.WCentrifugeButton;
import com.elytradev.opaline.container.widget.WCycleButton;
import com.elytradev.opaline.network.PacketButtonClick;
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

    private TileEntityCentrifuge centrifuge;

    public CentrifugeContainer(IInventory player, IInventory container, TileEntityCentrifuge centrifuge) {
        super(player, container);
        this.centrifuge = centrifuge;
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WPanel playerInv = this.createPlayerInventoryPanel();
        WFluidBar tankInRed = new WFluidBar(inRedBG, inRedFG, centrifuge.tankInRed).withTooltip("%d/%d mB");
        WFluidBar tankInGreen = new WFluidBar(inGreenBG, inGreenFG, centrifuge.tankInGreen).withTooltip("%d/%d mB");
        WFluidBar tankOut = new WFluidBar(outBG, outFG, centrifuge.tankOut).withTooltip("%d/%d mB");
        WBar progress = new WBar(arrowBG, arrowFG, container, 0, 1, WBar.Direction.DOWN).withTooltip("%d%%");
        String[] tooltips = new String[4];
        tooltips[0] = "Combine";
        tooltips[1] = "Merge Left";
        tooltips[2] = "Merge Right";
        tooltips[3] = "Dissolve";
        WCycleButton mode = new WCentrCycleButton(buttonEnabled, buttonDisabled, this::increaseMode, this::decreaseMode, centrifuge.getState(), centrifuge, buttonModes).withTooltip(tooltips);
        WClientButton go = new WCentrifugeButton(buttonGoEnabled, buttonGoDisabled, this::go, centrifuge).withTooltip("Activate");
        go.setEnabled(!centrifuge.isRunning);
        panel.add(playerInv, 0, 87);
        panel.add(tankInRed, 26, 31, 28, 20);
        panel.add(tankInGreen, 108, 31, 28, 20);
        panel.add(tankOut, 67, 56, 28, 26);
        panel.add(progress, 62, 37, 38, 16);
        panel.add(mode, 61, 16, 18, 18);
        panel.add(go, 83, 16, 18, 18);
    }

    public void increaseMode() {
        PacketButtonClick plusOne = new PacketButtonClick("centrifuge_plus");
        plusOne.sendToServer();
    }

    public void decreaseMode() {
        PacketButtonClick minusOne = new PacketButtonClick("centrifuge_minus");
        minusOne.sendToServer();
    }

    public void go() {
        PacketButtonClick go = new PacketButtonClick("centrifuge_go");
        go.sendToServer();
    }

    public void catchServerSide(String task) {
        switch (task) {
            case "centrifuge_plus":
                centrifuge.increaseState();
                break;
            case "centrifuge_minus":
                centrifuge.decreaseState();
                break;
            case "centrifuge_go":
                centrifuge.startRunning();
                break;
            default:
                break;
        }
    }
}