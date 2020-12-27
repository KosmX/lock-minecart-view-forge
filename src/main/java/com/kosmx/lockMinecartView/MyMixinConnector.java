package com.kosmx.lockMinecartView;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;


public class MyMixinConnector implements IMixinConnector {
    @Override
    public void connect(){
        Mixins.addConfiguration("assets/lock_minecart_view/lock_minecart_view.mixins.json");
        LockViewClient.log(Level.FATAL, "kjabfjkhAEBVDjfhvbljeuawhDavbf kuyawegkuyf gluiwesdaygfujoyg kuyew");
    }
}
