package com.kosmx.lockMinecartView;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class keyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event){
        LockViewClient.keyEvent(event);
    }
}
