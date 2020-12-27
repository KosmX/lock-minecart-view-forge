package com.kosmx.lockMinecartView;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

public class ForgeEventAction {
    /*
    @SubscribeEvent
    public void updeteMinecart(MinecartUpdateEvent event){
        if(LockViewClient.enabled && event.getMinecart() instanceof EntityMinecartEmpty && event.getMinecart().getPassengers().contains(Minecraft.getMinecraft().player)){
            LockViewClient.log(Level.INFO, "ticking");
            EntityMinecartEmpty minecart = (EntityMinecartEmpty) event.getMinecart();
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            LockViewClient.update(minecart);
            player.rotationYaw = LockViewClient.calcYaw(player.rotationYaw);
        }
    }   //No client-side callback exist for my purpose

     */

    @SubscribeEvent
    public void enterMinecart(MinecartInteractEvent event){
        //This callback is client-side
        if(event.getMinecart() instanceof EntityMinecartEmpty && event.getPlayer() == Minecraft.getMinecraft().player){
            LockViewClient.log(Level.INFO, "entering minecart");
            LockViewClient.onStartRiding();
        }
    }


}
