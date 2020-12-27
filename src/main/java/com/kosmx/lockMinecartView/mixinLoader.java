package com.kosmx.lockMinecartView;

import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;
import java.util.logging.LogManager;

public class mixinLoader implements IFMLLoadingPlugin {

    public mixinLoader(){
        MixinBootstrap.init();
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        if(codeSource != null){
            URL location = codeSource.getLocation();
            try{
                File file = new File(location.toURI());
                if(file.isFile()){
                    CoreModManager.getReparseableCoremods().remove(file.getName());
                }
            }catch (URISyntaxException ignored){}
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
