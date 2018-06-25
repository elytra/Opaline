package com.elytradev.opaline.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.opaline.Opaline;
//import com.elytradev.opaline.container.OscillatorContainer;
import com.elytradev.opaline.container.CentrifugeContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.SERVER)
public class PacketButtonClick extends Message {

    private String name;

    public PacketButtonClick() {
        super(Opaline.CONTEXT);

    }

    public PacketButtonClick(String name) {
        super(Opaline.CONTEXT);
        this.name=name;

    }

    @Override
    protected void handle(EntityPlayer entityPlayer) {
        Container container = entityPlayer.openContainer;
        if (entityPlayer.openContainer instanceof CentrifugeContainer) {
            CentrifugeContainer centrifuge = (CentrifugeContainer) container;
            centrifuge.catchServerSide(name);
        }
    }
}