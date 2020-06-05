package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.kosmx.lockMinecartView.LockViewConfig;
//import net.minecraft.client.gui.DrawableHelper;
//import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.DebugOverlayGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
@Mixin(DebugOverlayGui.class)
public class DebugHudMixin extends AbstractGui {

    @Inject(method="getDebugInfoLeft", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> info){
        if (LockViewConfig.showDebug.get()) {
            List<String> list = info.getReturnValue();
            list.addAll(LockViewClient.getDebug());
            info.setReturnValue(list);
        }
    }
}
