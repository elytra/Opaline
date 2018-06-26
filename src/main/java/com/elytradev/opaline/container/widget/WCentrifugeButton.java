package com.elytradev.opaline.container.widget;

import com.elytradev.concrete.inventory.gui.widget.WClientButton;
import com.elytradev.opaline.tile.TileEntityCentrifuge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class WCentrifugeButton extends WClientButton {

    private TileEntityCentrifuge centrifuge;

    public WCentrifugeButton(ResourceLocation enabled, ResourceLocation disabled, Runnable onClick, TileEntityCentrifuge centrifuge) {
        super(enabled, disabled, onClick);
        this.centrifuge = centrifuge;
    }

    @Override
    public void paintBackground(int x, int y) {
        enabled = !centrifuge.isRunning;
        if (enabled) {
            super.paintBackground(x, y);
        } else {
            if (disabledImage!=null) {
                rect(disabledImage, x, y, getWidth(), getHeight(), 0,0,1,1);
            } else {
                //No disabled image, so draw nothing
            }
        }
    }

    @Override
    public void onClick(int x, int y, int button) {
        if (enabled) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (onClick!=null) onClick.run();
        }
    }

}
