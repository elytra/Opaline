package com.elytradev.opaline;

import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.ConcreteGui;
import com.elytradev.opaline.block.ModBlocks;
import com.elytradev.opaline.client.OpalineTab;
import com.elytradev.opaline.container.DistillerContainer;
import com.elytradev.opaline.item.ModItems;
import com.elytradev.opaline.proxy.CommonProxy;
import com.elytradev.opaline.tile.TileEntityDistiller;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


@Mod(modid = Opaline.modId, name = Opaline.name, version = Opaline.version)
public class Opaline {
    public static final String modId = "opaline";
    public static final String name  = "Opaline";
    public static final String version = "1.0.0";

    @Mod.Instance(modId)
    public static Opaline instance;

    public static final OpalineTab creativeTab = new OpalineTab();

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @SidedProxy(serverSide = "com.elytradev.opaline.proxy.CommonProxy", clientSide = "com.elytradev.opaline.proxy.ClientProxy")
    public static CommonProxy proxy;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(name + " is loading!");
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {
            @Nullable
            @Override
            public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                return new DistillerContainer(
                        player.inventory, ((IContainerInventoryHolder)world.getTileEntity(new BlockPos(x,y,z))).getContainerInventory(),
                        (TileEntityDistiller)world.getTileEntity(new BlockPos(x,y,z)));
            }

            @Nullable
            @Override
            @SideOnly(Side.CLIENT)
            public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                DistillerContainer container = new DistillerContainer(
                        player.inventory, ((IContainerInventoryHolder)world.getTileEntity(new BlockPos(x,y,z))).getContainerInventory(),
                        (TileEntityDistiller)world.getTileEntity(new BlockPos(x,y,z)));
                return new ConcreteGui(container);
            }
        });
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //MinecraftForge.EVENT_BUS.register(new SoundRegisterListener());
        //MinecraftForge.EVENT_BUS.register(LightHandler.class);
        ModItems.registerOreDict(); // register oredict
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ModItems.register(event.getRegistry());
            ModBlocks.registerItemBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            ModItems.registerModels();
            ModBlocks.registerModels();
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            ModBlocks.register(event.getRegistry());
        }
    }
}
