package com.kosmx.lockMinecartView;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid = LockViewClient.MOD_ID, name = LockViewClient.MOD_NAME, version = "1.2.0")
public class lockMinecartView
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static KeyBinding keyBinding;

    /*public lockMinecartView() {


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


     */
    @Mod.EventHandler
    public void doClientStuff(FMLInitializationEvent event) {
        //isMixinIn();
        // do something that can only be done on the client
        keyBinding = new KeyBinding("toggle", 65, "Better minecart");

        ClientRegistry.registerKeyBinding(keyBinding);


        //FMLJavaModLoadingContext.get().getModEventBus().addListener(LockViewClient::keyEvent);
        MinecraftForge.EVENT_BUS.register(new keyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeEventAction());

        LockViewClient.enabled = LockViewConfig.enableByDefault;

        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, LockViewMenu.config);

        //LockViewMenu.loadConfig(LockViewMenu.config, FMLPaths.CONFIGDIR.get().resolve("lock_minecart_view.toml").toString());
        //LockViewClient.enabled = LockViewConfig.enableByDefault.get();

        //DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> new ClothConfigInterface());
        //ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            //return ClothConfigInterface.getClothConfigInterface().build();
        //});

        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    /*private void isMixinIn(){
        try {
            Class.forName("org.spongepowered.asm.launch.Phases");
        }
        catch (ClassNotFoundException e) {
            LOGGER.fatal("[lock-minecaft-view] MixinBootstrap not found :( did you download it? \n https://www.curseforge.com/minecraft/mc-mods/mixinbootstrap");
        }
    }

     */

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
