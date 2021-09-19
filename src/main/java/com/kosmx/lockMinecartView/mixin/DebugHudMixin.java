package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.kosmx.lockMinecartView.LockViewConfig;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
@Mixin(DebugScreenOverlay.class)
public class DebugHudMixin extends GuiComponent {

    @Inject(method="getGameInformation", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> info){
        if (LockViewConfig.showDebug.get()) {
            List<String> list = info.getReturnValue();
            list.addAll(LockViewClient.getDebug());
            info.setReturnValue(list);
        }
    }
}
