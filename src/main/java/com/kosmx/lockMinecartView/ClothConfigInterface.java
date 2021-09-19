package com.kosmx.lockMinecartView;


import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.TranslatableComponent;

public class ClothConfigInterface {


    public static ConfigBuilder getClothConfigInterface(){

        ConfigBuilder builder = ConfigBuilder.create();

        ConfigCategory general = builder.getOrCreateCategory(translateText("Lock MinecartView Config"));

        ConfigEntryBuilder smoothMode = builder.entryBuilder();
        ConfigEntryBuilder smartMode = builder.entryBuilder();
        ConfigEntryBuilder threshold = builder.entryBuilder();
        ConfigEntryBuilder rollerCoasterMode = builder.entryBuilder();
        ConfigEntryBuilder enabled = builder.entryBuilder();
        ConfigEntryBuilder showDebug = builder.entryBuilder();

        general.addEntry(smoothMode
                                 .startBooleanToggle(translateText("Smooth rotation mode"), LockViewConfig.smoothMode.get())
                                 .setDefaultValue(true)
                                 .setTooltip(translateTextTooltip(":D"))
                                 .setSaveConsumer(newValue -> LockViewConfig.smoothMode.set(newValue))
                                 .build());
        general.addEntry(smartMode
                                 .startBooleanToggle(translateText("Smart mode (hint)"), LockViewConfig.smartMode.get())
                                 .setDefaultValue(true)
                                 .setTooltip(translateTextTooltip("Don't rotate when crash into something and start moving in opposite direction"))
                                 .setSaveConsumer(newValue -> LockViewConfig.smartMode.set(newValue))
                                 .build());
        general.addEntry(threshold
                                 .startIntSlider(translateText("Rotation reset treshold"), LockViewConfig.threshold.get(), 0, 80)
                                 .setDefaultValue(8)
                                 .setTooltip(translateTextTooltip("Smart mode's sub-option, try changing it's value. 20 tick is 1 second"))
                                 .setSaveConsumer(newValue -> LockViewConfig.threshold.set(newValue))
                                 .build());
        general.addEntry(rollerCoasterMode
                                 .startBooleanToggle(translateText("Roller Coaster mode"), LockViewConfig.rollerCoasterMode.get())
                                 .setDefaultValue(false)
                                 .setTooltip(translateTextTooltip("Enforce to look forward. Smart mode has no effect, if turned on"))
                                 .setSaveConsumer(newValue -> LockViewConfig.rollerCoasterMode.set(newValue))
                                 .build());
        general.addEntry(enabled
                                 .startBooleanToggle(translateText("Enable mod on starting the game"), LockViewConfig.enableByDefault.get())
                                 .setDefaultValue(true)
                                 .setTooltip(translateTextTooltip("spenceregilbert asked me, to add this entry"))
                                 .setSaveConsumer(newValue -> LockViewConfig.enableByDefault.set(newValue))
                                 .build());
        general.addEntry(enabled
                                 .startBooleanToggle(translateText("Show debug info in F3 screen"), LockViewConfig.enableByDefault.get())
                                 .setDefaultValue(false)
                                 .setTooltip(translateTextTooltip("and toggle text debug info"))
                                 .setSaveConsumer(newValue -> LockViewConfig.showDebug.set(newValue))
                                 .build());
        //Screen screen = builder.build();
        return builder;
    }

    /*private static TranslationTextComponent translateText(String msg){
        return new TranslationTextComponent( "text.autoconfig." + LockViewClient.MOD_ID + "." + msg);
    }

    private static TranslationTextComponent translateTextTooltip(String msg){
        return translateText(msg + ".@Tooltip");
    }

     */

    private static TranslatableComponent translateText(String msg){
        return new TranslatableComponent(msg);
    }
    private static TranslatableComponent translateTextTooltip(String msg){
        return new TranslatableComponent(msg);
    }

}
