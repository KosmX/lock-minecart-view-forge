package com.kosmx.lockMinecartView;

import net.minecraftforge.common.ForgeConfigSpec;

public class LockViewConfig {
    public static ForgeConfigSpec.BooleanValue smoothMode;
    public static ForgeConfigSpec.BooleanValue smartMode;
    public static ForgeConfigSpec.IntValue threshold;
    public static ForgeConfigSpec.BooleanValue rollerCoasterMode;

    public static void init(ForgeConfigSpec.Builder client){

        //client.comment("VML_config");
        smoothMode = client.comment("Smooth rotation mode").define("vml.smooth", true);
        smartMode = client.comment("Smart mode: Smart direction calculation (minecart hasn't got a usable yaw)").define("vml.smart", true);
        threshold = client.comment("SRotation reset treshold").defineInRange("vml.threshold", 8, 0, 80);
        rollerCoasterMode = client.comment("Roller-Coaster mode").define("vml.rollercoaster", false);
    }
}
