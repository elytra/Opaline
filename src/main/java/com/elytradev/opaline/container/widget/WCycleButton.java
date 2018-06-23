package com.elytradev.opaline.container.widget;

import com.elytradev.concrete.inventory.gui.widget.WClientButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class WCycleButton extends WClientButton {

    public ResourceLocation[] options;
    public int state = 0;
    public Runnable onRightClick;

    public void setOnRightClick(Runnable r) {
        this.onRightClick = r;
    }

    public WCycleButton(ResourceLocation enabled, ResourceLocation disabled, Runnable onClick, ResourceLocation... options) {
        super(enabled, disabled, onClick);
        this.image = enabled;
        this.disabledImage = disabled;
        this.setOnClick(onClick);
        this.options = options;
    }

    public WCycleButton(ResourceLocation enabled, ResourceLocation disabled, Runnable onClick, Runnable onRightClick, ResourceLocation... options) {
        super(enabled, disabled, onClick);
        this.image = enabled;
        this.disabledImage = disabled;
        this.setOnClick(onClick);
        this.setOnRightClick(onRightClick);
        this.options = options;
    }

    @Override
    public void paintBackground(int x, int y) {
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

    public WCycleButton withTooltip(String... label) {
        this.setRenderTooltip(true);
        if (state > label.length) {
            this.tooltipLabel = "No label for this state";
            return this;
        }
        this.tooltipLabel = label[state];
        return this;
    }

}