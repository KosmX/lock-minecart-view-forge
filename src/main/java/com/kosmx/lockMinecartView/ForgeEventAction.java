package com.kosmx.lockMinecartView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ForgeEventAction {
    @SubscribeEvent
    public void updeteMinecart(MinecartUpdateEvent event){
        if(LockViewClient.enabled && event.getMinecart() instanceof EntityMinecartEmpty && event.getMinecart().getPassengers().contains(Minecraft.getMinecraft().player)){
            EntityMinecartEmpty minecart = (EntityMinecartEmpty) event.getMinecart();
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            LockViewClient.update(minecart);
            player.rotationYaw = LockViewClient.calcYaw(player.rotationYaw);
        }
    }

    @SubscribeEvent
    public void enterMinecart(MinecartInteractEvent event){
        if(event.getMinecart() instanceof EntityMinecartEmpty && event.getPlayer() == Minecraft.getMinecraft().player){
            LockViewClient.log(Level.INFO, "entering minecart");
            LockViewClient.onStartRiding();
        }
    }


}
