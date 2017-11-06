package com.elytradev.opaline.client;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import com.elytradev.opaline.block.BlockInfuser;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.tile.TileEntityInfuser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderInfuser extends TileEntitySpecialRenderer<TileEntityInfuser> {
    private static final Field age = ReflectionHelper.findField(EntityItem.class, "field_70292_b", "age");

    private RenderEntityItem rei;
    private EntityItem dummy;

    @Override
    public void render(TileEntityInfuser te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState ibs = te.getWorld().getBlockState(te.getPos());
        if (ibs.getBlock() != ModBlocks.distiller) return;
        FluidStack fluid = te.tank.getFluid();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5f, 0.5f, 0.5f);
        switch (ibs.getValue(BlockInfuser.FACING)) {
            case WEST:
                GlStateManager.rotate(90f, 0, 1, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(180f, 0, 1, 0);
                break;
            case EAST:
                GlStateManager.rotate(270f, 0, 1, 0);
                break;
            default:
                break;
        }
        GlStateManager.translate(-0.5f, -0.5f, -0.5f);
        if (fluid != null) {
            TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill(fluid).toString());
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.color(1, 1, 1);
            renderCube(10.25f, 7, 10.25f, 2.5f, 2.5f*(fluid.amount/(float)te.tank.getCapacity()), 2.5f, tas, true, true, true);
            GlStateManager.disableBlend();
        }
        if (dummy == null) {
            rei = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem());
            dummy = new EntityItem(te.getWorld());
        }
        try {
            age.set(dummy, te.ticksExisted);
        } catch (Exception e) {
        }
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        dummy.setItem(te.items.getStackInSlot(TileEntityInfuser.SLOT_LAPIS));
        rei.doRender(dummy, 1.4f, 0, 1, 0, partialTicks);
        dummy.setItem(te.items.getStackInSlot(TileEntityInfuser.SLOT_EXHAUSTED));
        rei.doRender(dummy, 1.4f, 0, 1, 0, partialTicks);
        GlStateManager.popMatrix();
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }

    // this is like the fifth time I've copied this from Glass Hearts
    // should probably get added to Concrete
    private void renderCube(float x, float y, float z, float w, float h, float d, TextureAtlasSprite tas, boolean renderTop, boolean renderBottom, boolean renderSides) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder vb = tess.getBuffer();

        float minVX = tas.getInterpolatedV(x);
        float maxVX = tas.getInterpolatedV(x+w);
        float minVY = tas.getInterpolatedV(y);
        float maxVY = tas.getInterpolatedV(y+h);

        float minUX = tas.getInterpolatedU(x);
        float maxUX = tas.getInterpolatedU(x+w);
        float minUZ = tas.getInterpolatedU(z);
        float maxUZ = tas.getInterpolatedU(z+d);

        float s = 1/16f;

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        if (renderSides) {
            vb.pos((x+0)*s, (y+h)*s, (z+0)*s).tex(minUX, maxVY).normal(0, 0, -1).endVertex();
            vb.pos((x+w)*s, (y+h)*s, (z+0)*s).tex(maxUX, maxVY).normal(0, 0, -1).endVertex();
            vb.pos((x+w)*s, (y+0)*s, (z+0)*s).tex(maxUX, minVY).normal(0, 0, -1).endVertex();
            vb.pos((x+0)*s, (y+0)*s, (z+0)*s).tex(minUX, minVY).normal(0, 0, -1).endVertex();

            vb.pos((x+w)*s, (y+h)*s, (z+d)*s).tex(maxUX, maxVY).normal(0, 0, 1).endVertex();
            vb.pos((x+0)*s, (y+h)*s, (z+d)*s).tex(minUX, maxVY).normal(0, 0, 1).endVertex();
            vb.pos((x+0)*s, (y+0)*s, (z+d)*s).tex(minUX, minVY).normal(0, 0, 1).endVertex();
            vb.pos((x+w)*s, (y+0)*s, (z+d)*s).tex(maxUX, minVY).normal(0, 0, 1).endVertex();


            vb.pos((x+0)*s, (y+0)*s, (z+d)*s).tex(maxUZ, minVY).normal(-1, 0, 0).endVertex();
            vb.pos((x+0)*s, (y+h)*s, (z+d)*s).tex(maxUZ, maxVY).normal(-1, 0, 0).endVertex();
            vb.pos((x+0)*s, (y+h)*s, (z+0)*s).tex(minUZ, maxVY).normal(-1, 0, 0).endVertex();
            vb.pos((x+0)*s, (y+0)*s, (z+0)*s).tex(minUZ, minVY).normal(-1, 0, 0).endVertex();

            vb.pos((x+w)*s, (y+0)*s, (z+0)*s).tex(minUZ, minVY).normal(1, 0, 0).endVertex();
            vb.pos((x+w)*s, (y+h)*s, (z+0)*s).tex(minUZ, maxVY).normal(1, 0, 0).endVertex();
            vb.pos((x+w)*s, (y+h)*s, (z+d)*s).tex(maxUZ, maxVY).normal(1, 0, 0).endVertex();
            vb.pos((x+w)*s, (y+0)*s, (z+d)*s).tex(maxUZ, minVY).normal(1, 0, 0).endVertex();
        }

        if (renderBottom) {
            vb.pos((x+0)*s, (y+0)*s, (z+0)*s).tex(minUZ, minVX).normal(0, -1, 0).endVertex();
            vb.pos((x+w)*s, (y+0)*s, (z+0)*s).tex(minUZ, maxVX).normal(0, -1, 0).endVertex();
            vb.pos((x+w)*s, (y+0)*s, (z+d)*s).tex(maxUZ, maxVX).normal(0, -1, 0).endVertex();
            vb.pos((x+0)*s, (y+0)*s, (z+d)*s).tex(maxUZ, minVX).normal(0, -1, 0).endVertex();
        }

        if (renderTop) {
            vb.pos((x+0)*s, (y+h)*s, (z+0)*s).tex(minUZ, minVX).normal(0, 1, 0).endVertex();
            vb.pos((x+0)*s, (y+h)*s, (z+d)*s).tex(maxUZ, minVX).normal(0, 1, 0).endVertex();
            vb.pos((x+w)*s, (y+h)*s, (z+d)*s).tex(maxUZ, maxVX).normal(0, 1, 0).endVertex();
            vb.pos((x+w)*s, (y+h)*s, (z+0)*s).tex(minUZ, maxVX).normal(0, 1, 0).endVertex();

        }
        tess.draw();
    }

}
