package com.kosmx.lockMinecartView;

import net.minecraftforge.common.config.Config;

@Config(modid = LockViewClient.MOD_ID)
public class LockViewConfig {
    public static boolean smoothMode = true;
    public static boolean smartMode = true;
    @Config.RangeInt(min = 0, max = 80)
    public static int threshold = 8;
    public static boolean rollerCoasterMode = false;
    public static boolean enableByDefault = true;
    public static boolean showDebug = false;

    /*
    public static ForgeConfigSpec.BooleanValue smoothMode;
    public static ForgeConfigSpec.BooleanValue smartMode;
    public static ForgeConfigSpec.IntValue threshold;
    public static ForgeConfigSpec.BooleanValue rollerCoasterMode;
    public static ForgeConfigSpec.BooleanValue enableByDefault;
    public static ForgeConfigSpec.BooleanValue showDebug;

    public static void init(ForgeConfigSpec.Builder client){

        //client.comment("VML_config");
        smoothMode = client.comment("Smooth rotation mode").define("vml.smooth", true);
        smartMode = client.comment("Smart mode: Smart direction calculation (minecart hasn't got a usable yaw)").define("vml.smart", true);
        threshold = client.comment("SRotation reset treshold").defineInRange("vml.threshold", 8, 0, 80);
        rollerCoasterMode = client.comment("Roller-Coaster mode").define("vml.rollercoaster", false);
        enableByDefault = client.comment("Enable on start game").define("vml.enablebydefault", true);
        showDebug = client.comment("Show debug info in F3 screen").define("vml.debug", false);
    }

     */
}
