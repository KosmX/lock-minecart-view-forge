package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Minecart;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LocalPlayer.class)
public class ClientPlayerMixin extends AbstractClientPlayer {

    public ClientPlayerMixin(ClientLevel p_108548_, GameProfile p_108549_) {
        super(p_108548_, p_108549_);
    }

    @Inject(method = "rideTick", at = @At("TAIL"))
    private void ridingTick(CallbackInfo info){
        Entity vehicle = this.getVehicle();
        if(LockViewClient.enabled && vehicle instanceof Minecart minecart){
            /*Using MinecartEntity.getYaw() is unusable, becouse it's not the minecart's yaw...
             *There is NO way in mc to get the minecart's yaw...
             *I need to create any identifier method (from the speed)
             */
            LockViewClient.update(minecart);
            this.yHeadRot = LockViewClient.calcYaw(this.yHeadRot);
            //this.bodyYaw = LockViewClient.calcYaw(this.bodyYaw);
            //this.lastRenderYaw += LockViewClient.correction;
            //this.renderYaw += LockViewClient.correction;
            //this.sendMovementPackets();
        }
    }

    @Inject(method = "startRiding", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/Minecraft;getSoundManager()Lnet/minecraft/client/sounds/SoundManager;"))
    private void startRidingInject(Entity entity, boolean force, CallbackInfoReturnable<Object> info){
        //net.minecraft.client.network.ClientPlayerEntity
        LockViewClient.log(Level.INFO, "entering minecart");
        LockViewClient.onStartRiding();
    }
    
}