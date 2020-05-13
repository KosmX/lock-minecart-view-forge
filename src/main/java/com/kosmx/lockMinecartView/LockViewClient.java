package com.kosmx.lockMinecartView;

/*import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;     fabric api- not forge
*/
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.kosmx.lockMinecartView.lockMinecartView.keyBinding;


public class LockViewClient{

    //-------------system variables--------------------
    private static boolean isHeld = false;
    public static boolean enabled = true;
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lock_minecart_view";
    public static final String MOD_NAME = "Lock Minecart view";
    //-------------calculating vars--------------------
    public static float yaw = 0f;
    //public static float pitch = 0f;
    private static boolean doCorrection;
    @Nullable
    private static Vec3d lastCoord = null;
    private static float lastYaw = 0f;
    private static Vec3d lastVelocity;
    private static float rawLastYaw;
    private static float rawYaw;
    public static int tickAfterLastFollow = 0;
    private static float difference;
    private static int lastSlowdown = 0;

    //-------------methods-----------------------------

    public static float sphericalFromVec3d(Vec3d vec3d){
        //float f = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
        //pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
        return (float)(MathHelper.atan2(-vec3d.x, vec3d.z) * 57.2957763671875D);
    }

    public static boolean onStartRiding(){
        lastCoord = null;
        tickAfterLastFollow = 100;
        lastVelocity = Vec3d.ZERO;
        lastSlowdown = 100;
        return !enabled;
    }

    public static void setMinecartDirection(float yawF){
        if (LockViewConfig.smoothMode.get()){
            if(!LockViewConfig.rollerCoasterMode.get() && tickAfterLastFollow > LockViewConfig.threshold.get()){
                LockViewClient.yaw = yawF;
            }
            else if(doCorrection){
                LockViewClient.yaw = normalize(LockViewClient.yaw + 180f);
            }
            if (Math.abs(yawF - LockViewClient.yaw) < 180f){
                LockViewClient.yaw = LockViewClient.yaw/2 + yawF/2;
            }
            else{
                float tmp = LockViewClient.yaw/2 + yawF/2;
                //log(Level.INFO, Float.toString(LockViewClient.yaw));
                LockViewClient.yaw = (tmp >= 0) ? tmp - 180f : tmp + 180f;
            }
            //log(Level.INFO, Float.toString(LockViewClient.yaw));
        }
        else{
            LockViewClient.yaw = yawF;
        }
    }

    public static void setMinecartDirection(MinecartEntity minecart){
        float yawF = sphericalFromVec3d(minecart.getMotion());
        LockViewClient.rawLastYaw = LockViewClient.rawYaw;
        LockViewClient.rawYaw = yawF;
        checkSmartCorrection(minecart);
        setMinecartDirection(yawF);
    }

    public static void setMinecartDirection(Vec3d vec3d){
        setMinecartDirection(sphericalFromVec3d(vec3d));
    }

    public static void smartCalc(MinecartEntity minecart){
        LockViewClient.lastYaw = LockViewClient.yaw;
        boolean update = false;
        if (minecart.getMotion().lengthSquared()>0.000002f){
            update = true;
            setMinecartDirection(minecart);
            //log(Level.INFO, Float.toString(LockViewClient.yaw - LockViewClient.lastYaw));
        }
        if (LockViewClient.tickAfterLastFollow++ >= LockViewConfig.threshold.get()){
            LockViewClient.lastYaw = LockViewClient.yaw;
            //log(Level.INFO, "clear rotation" + Integer.toString(tickAfterLastFollow) + " : " + Boolean.toString(update));
        }
        else if(doCorrection){
            LockViewClient.lastYaw = normalize(LockViewClient.lastYaw + 180f);
            //log(Level.INFO, "do smart correction");
        }
        doCorrection = false;
        if(update) LockViewClient.tickAfterLastFollow = 0;
        LockViewClient.difference = LockViewClient.yaw - LockViewClient.lastYaw;
    }

    private static void checkSmartCorrection(MinecartEntity minecart){
        boolean correction = false;
        if(LockViewConfig.smartMode.get()){
            float ang = 60;
            if (Math.abs(LockViewClient.rawLastYaw - LockViewClient.rawYaw) > 180f-ang && Math.abs(LockViewClient.rawLastYaw - LockViewClient.rawYaw)<180+ang){
                correction = true;
            }
            Vec3d vec3d = minecart.getPositionVec();
            if(lastCoord != null){
                Vec3d velocity = new Vec3d(vec3d.x - lastCoord.x, 0, vec3d.z - lastCoord.z);
                if(lastVelocity == null) lastVelocity = new Vec3d(0, 0, 0);
                Vec3d velocity2d = new Vec3d(minecart.getMotion().getX(), 0, minecart.getMotion().getZ());
                //log(Level.INFO, Double.toString(velocity2d.lengthSquared() - velocity.lengthSquared()));
                //log(Level.INFO, velocity.lengthSquared() + " : " + lastVelocity.lengthSquared());
                if( velocity2d.length() != 0 && lastVelocity.length()/velocity2d.length() > 2.4d) lastSlowdown = 0;
                boolean bl1 = correction && velocity.lengthSquared() > 0.000008f && Math.abs(velocity.normalize().dotProduct(velocity2d.normalize())) < 0.8f;//vectors dot product ~0, if vectors are ~perpendicular to each other
                boolean bl2 = (!bl1) || lastSlowdown++ < LockViewConfig.threshold.get() && Math.abs(velocity.normalize().dotProduct(velocity2d.normalize())) < 0.866f && velocity2d.lengthSquared() < 0.32;
                if(bl1 && !bl2) {
                    correction = false;
                }
                lastVelocity = velocity2d;
            }
            lastCoord = vec3d;
        }
        LockViewClient.doCorrection = correction;
    }

    public static float calcYaw(float entityYaw){
        //log(Level.INFO, Float.toString(LockViewClient.difference));
        return entityYaw + LockViewClient.difference;
    }


    private static float normalize(Float f){
        return (Math.abs(f) > 180) ? (f < 0) ? f + 360f : f - 360f : f;
    }


    //-------------control key variables---------------
    

    /*@Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(LockViewConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(LockViewConfig.class).getConfig();
        keyBinding = FabricKeyBinding.Builder.create(
            new Identifier(MOD_ID, "toggle"), 
            net.minecraft.client.util.InputUtil.Type.KEYSYM, 
            GLFW.GLFW_KEY_F7, 
            MOD_NAME
        ).build();
        KeyBindingRegistry.INSTANCE.addCategory(MOD_NAME);
        KeyBindingRegistry.INSTANCE.register(keyBinding);
        ClientTickCallback.EVENT.register(e ->
        */
        @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
        public static void keyEvent(InputEvent.KeyInputEvent e)
        {
            LOGGER.info("spam");
            if (keyBinding.isPressed()){
                if(isHeld)return;
                isHeld = true;
                enabled = onStartRiding();
            }
            else if (isHeld){
                isHeld = false;
            }
        }/*);
    }
    //net.minecraft.client.render.item.HeldItemRenderer
    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }*/

}