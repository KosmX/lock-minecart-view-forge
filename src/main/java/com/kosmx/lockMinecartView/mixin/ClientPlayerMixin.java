package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.mojang.authlib.GameProfile;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;

import com.kosmx.lockMinecartView.LockViewConfig;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin extends AbstractClientPlayerEntity{

    @Shadow public float renderArmYaw;

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "updateRidden", at = @At("TAIL"))
    private void ridingTick(CallbackInfo info){
        Entity vehicle = this.getRidingEntity();
        if(LockViewClient.enabled && vehicle instanceof MinecartEntity){
            MinecartEntity minecart = (MinecartEntity)vehicle;
            /*Using MinecartEntity.getYaw() is unusable, becouse it's not the minecart's yaw...
             *There is NO way in mc to get the minecart's yaw...
             *I need to create any identifier method (from the speed)
             */
            LockViewClient.update(minecart);
            this.rotationYaw = LockViewClient.calcYaw(this.rotationYaw);
            //this.bodyYaw = LockViewClient.calcYaw(this.bodyYaw);
            //this.lastRenderYaw += LockViewClient.correction;
            //this.renderYaw += LockViewClient.correction;
            //this.sendMovementPackets();
        }
    }

    @Inject(method = "startRiding", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/Minecraft;getSoundHandler()Lnet/minecraft/client/audio/SoundHandler;"))
    private void startRidingInject(Entity entity, boolean force, CallbackInfoReturnable<Object> info){
        //net.minecraft.client.network.ClientPlayerEntity
        LockViewClient.log(Level.INFO, "entering minecart");
        LockViewClient.onStartRiding();
    }
    
}