/*package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;


@Mixin(EntityPlayerSP.class)
public class ClientPlayerMixin extends AbstractClientPlayer {

    @Shadow public float renderArmYaw;

    public ClientPlayerMixin(World worldIn, GameProfile playerProfile){
        super(worldIn, playerProfile);
    }

    @Inject(method = "updateRidden", at = @At("TAIL"))
    private void ridingTick(CallbackInfo info){
        Entity vehicle = this.getRidingEntity();
        if(LockViewClient.enabled && vehicle instanceof EntityMinecartEmpty){
            EntityMinecartEmpty minecart = (EntityMinecartEmpty) vehicle;
            /*Using MinecartEntity.getYaw() is unusable, becouse it's not the minecart's yaw...
             *There is NO way in mc to get the minecart's yaw...
             *I need to create any identifier method (from the speed)
             /
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
*/