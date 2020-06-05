package com.kosmx.lockMinecartView;


import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class ClothConfigInterface {


    public static ConfigBuilder getClothConfigInterface(){

        ConfigBuilder builder = ConfigBuilder.create();

        ConfigCategory general = builder.getOrCreateCategory("general menu");

        ConfigEntryBuilder smoothMode = builder.entryBuilder();
        ConfigEntryBuilder smartMode = builder.entryBuilder();
        ConfigEntryBuilder threshold = builder.entryBuilder();
        ConfigEntryBuilder rollerCoasterMode = builder.entryBuilder();
        ConfigEntryBuilder enabled = builder.entryBuilder();
        ConfigEntryBuilder showDebug = builder.entryBuilder();

        general.addEntry(smoothMode
                                 .startBooleanToggle("Smooth rotation mode", LockViewConfig.smoothMode.get())
                                 .setDefaultValue(true)
                                 .setTooltip(":D")
                                 .setSaveConsumer(newValue -> LockViewConfig.smoothMode.set(newValue))
                                 .build());
        general.addEntry(smartMode
                                 .startBooleanToggle("Smart mode (hint)", LockViewConfig.smartMode.get())
                                 .setDefaultValue(true)
                                 .setTooltip("Don't rotate when crash into something and start moving in opposite direction")
                                 .setSaveConsumer(newValue -> LockViewConfig.smartMode.set(newValue))
                                 .build());
        general.addEntry(threshold
                                 .startIntSlider("Rotation reset treshold", LockViewConfig.threshold.get(), 0, 80)
                                 .setDefaultValue(8)
                                 .setTooltip("Smart mode's sub-option, try changing it's value. 20 tick is 1 second")
                                 .setSaveConsumer(newValue -> LockViewConfig.threshold.set(newValue))
                                 .build());
        general.addEntry(rollerCoasterMode
                                 .startBooleanToggle("Roller Coaster mode", LockViewConfig.rollerCoasterMode.get())
                                 .setDefaultValue(false)
                                 .setTooltip("Enforce to look forward. Smart mode has no effect, if turned on")
                                 .setSaveConsumer(newValue -> LockViewConfig.rollerCoasterMode.set(newValue))
                                 .build());
        general.addEntry(enabled
                                 .startBooleanToggle("Enable mod on starting the game", LockViewConfig.enableByDefault.get())
                                 .setDefaultValue(true)
                                 .setTooltip("spenceregilbert asked me, to add this entry")
                                 .setSaveConsumer(newValue -> LockViewConfig.enableByDefault.set(newValue))
                                 .build());
        general.addEntry(enabled
                                 .startBooleanToggle("Show debug info in F3 screen", LockViewConfig.enableByDefault.get())
                                 .setDefaultValue(false)
                                 .setTooltip("and toggle text debug info")
                                 .setSaveConsumer(newValue -> LockViewConfig.showDebug.set(newValue))
                                 .build());
        //Screen screen = builder.build();
        return builder;
    }


}
