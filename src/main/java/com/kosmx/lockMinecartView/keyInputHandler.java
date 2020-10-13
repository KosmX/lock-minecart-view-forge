package com.kosmx.lockMinecartView;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class keyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event){
        LockViewClient.keyEvent(event);
    }
}
