package com.kosmx.lockMinecartView;


import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

public class ClothConfigInterface {


    public static ConfigBuilder getClothConfigInterface(){

        ConfigBuilder builder = ConfigBuilder.create();

        ConfigCategory general = builder.getOrCreateCategory(getTranslationStr("title"));

        ConfigEntryBuilder smoothMode = builder.entryBuilder();
        ConfigEntryBuilder smartMode = builder.entryBuilder();
        ConfigEntryBuilder threshold = builder.entryBuilder();
        ConfigEntryBuilder rollerCoasterMode = builder.entryBuilder();
        ConfigEntryBuilder enabled = builder.entryBuilder();

        general.addEntry(smoothMode
                                 .startBooleanToggle(getTranslationStr("smoothMode"), LockViewConfig.smoothMode.get())
                                 .setDefaultValue(true)
                                 .setTooltip(getTooltipTranslation("smoothMode"))
                                 .setSaveConsumer(newValue -> LockViewConfig.smoothMode.set(newValue))
                                 .build());
        general.addEntry(smartMode
                                 .startBooleanToggle(getTranslationStr("smartMode"), LockViewConfig.smartMode.get())
                                 .setDefaultValue(true)
                                 .setTooltip(getTooltipTranslation("smartMode"))
                                 .setSaveConsumer(newValue -> LockViewConfig.smartMode.set(newValue))
                                 .build());
        general.addEntry(threshold
                                 .startIntSlider(getTranslationStr("threshold"), LockViewConfig.threshold.get(), 0, 80)
                                 .setDefaultValue(8)
                                 .setTooltip(getTooltipTranslation("threshold"))
                                 .setSaveConsumer(newValue -> LockViewConfig.threshold.set(newValue))
                                 .build());
        general.addEntry(rollerCoasterMode
                                 .startBooleanToggle(getTranslationStr("rollerCoasterMode"), LockViewConfig.rollerCoasterMode.get())
                                 .setDefaultValue(false)
                                 .setTooltip(getTooltipTranslation("rollerCoasterMode"))
                                 .setSaveConsumer(newValue -> LockViewConfig.rollerCoasterMode.set(newValue))
                                 .build());
        general.addEntry(enabled
                                 .startBooleanToggle(getTranslationStr("enabled"), LockViewConfig.enableByDefault.get())
                                 .setDefaultValue(true)
                                 .setTooltip(getTooltipTranslation("enabled"))
                                 .setSaveConsumer(newValue -> LockViewConfig.enableByDefault.set(newValue))
                                 .build());
        //Screen screen = builder.build();
        return builder;
    }


    private static String getTranslationStr(String str){
        return "text.autoconfig.lock_minecart_view." + str;
    }
    private static String getTooltipTranslation(String str){
        return getTranslationStr(str) + ".@Tooltip";
    }
}
