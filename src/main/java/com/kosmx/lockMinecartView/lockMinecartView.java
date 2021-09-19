package com.kosmx.lockMinecartView;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiFunction;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("lock_minecart_view")
public class lockMinecartView
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static KeyMapping keyBinding;

    public lockMinecartView() {


        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::warn);  //try to warn, if the mod is in the server's mods folder

       //FMLJavaModLoadingContext.get().getModEventBus().addListener(this:preInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private  void warn(final FMLDedicatedServerSetupEvent event){
        //Do DedicatedServer stuff: create a crash :D
        LOGGER.error("Better minecart rotation is Client ONLY!!! :D");
    }

    //private void preInit(FML)

    private void doClientStuff(final FMLClientSetupEvent event) {
        isMixinIn();
        // do something that can only be done on the client
        keyBinding = new KeyMapping("toggle", GLFW.GLFW_KEY_F7, "Better minecart");

        ClientRegistry.registerKeyBinding(keyBinding);

        //FMLJavaModLoadingContext.get().getModEventBus().addListener(LockViewClient::keyEvent);
        MinecraftForge.EVENT_BUS.register(new keyInputHandler());

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, LockViewMenu.config);

        LockViewMenu.loadConfig(LockViewMenu.config, FMLPaths.CONFIGDIR.get().resolve("lock_minecart_view.toml").toString());
        LockViewClient.enabled = LockViewConfig.enableByDefault.get();

        DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> () -> new ClothConfigInterface());

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory(
                        (minecraft, screen) -> ClothConfigInterface.getClothConfigInterface().build()));

        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void isMixinIn(){
        try {
            Class.forName("org.spongepowered.asm.launch.Phases");
        }
        catch (ClassNotFoundException e) {
            LOGGER.fatal("Use Forge 37.0.59+");
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    /*@SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }*/

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    /*@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }*/
}
