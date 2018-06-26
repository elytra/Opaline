package com.elytradev.opaline.container.widget;

import com.elytradev.opaline.tile.TileEntityCentrifuge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class WCentrCycleButton extends WCycleButton {

    private TileEntityCentrifuge centrifuge;

    public WCentrCycleButton(ResourceLocation enabled, ResourceLocation disabled, Runnable onClick, Runnable onRightClick, int currentState, TileEntityCentrifuge centrifuge, ResourceLocation... options) {
        super(enabled, disabled, onClick, onRightClick, currentState, options);
        this.centrifuge = centrifuge;
    }

    @Override
    public void paintBackground(int x, int y) {
        enabled = !centrifuge.isRunning;
        if (state >= options.length) state = options.length;
        if (enabled) {
            super.paintBackground(x, y);
            rect(options[state], x, y, getWidth(), getHeight(), 0, 0, 1, 1);
        } else {
            if (disabledImage!=null) {
                rect(disabledImage, x, y, getWidth(), getHeight(), 0,0,1,1);
                rect(options[state], x, y, getWidth(), getHeight(), 0, 0, 1, 1);
            } else {
                //No disabled image, so draw nothing
            }
        }
    }

    @Override
    public void onClick(int x, int y, int button) {
        if (enabled) {
            if (button == 0) {
                state++;
                if (state >= options.length) state = 0;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                if (onClick != null) onClick.run();
            } else {
                state--;
                if (state < 0) state = options.length-1;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.8F));
                if (onRightClick != null) onRightClick.run();
            }
        }
    }
}
