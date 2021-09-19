package com.kosmx.lockMinecartView;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;

import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

//import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
//import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;     Fabric Modmenu

import java.util.List;

import static com.kosmx.lockMinecartView.lockMinecartView.keyBinding;

//This class contains all code...
//Originally for Fabric loader

public class LockViewClient{

    //-------------system variables--------------------
    private static boolean isHeld = false;
    //public static LockViewConfig config;
    public static boolean enabled;
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lock_minecart_view";
    public static final String MOD_NAME = "Better Minecart rotation"; //To be correct
    //-------------calculating vars--------------------
    private static float yaw = 0f;
    //public static float pitch = 0f;
    private static boolean doCorrection;

    //posVelocity is from position's change -- real, big delay
    //gotVelocity is from Minecart.getMotion()() -- represents rail's direction, immediate
    @Nullable
    private static Vec3 gotVelocity = null;
    @Nullable
    private static Vec3 posVelocity = null;

    @Nullable
    private static Vec3 lastCoord = null;
    private static float lastYaw = 0f;
    private static Vec3 lastVelocity;
    private static float rawLastYaw;
    private static float rawYaw;
    private static int tickAfterLastFollow = 0;
    private static int tickAfterPistonRail;
    private static int pistorRailTick = 0;
    private static float difference;
    private static int lastSlowdown = 0;


    //-----------------CORE METHOD-----------------------

    public static void update(Minecart minecart){
        lastYaw = yaw;
        boolean update = setMinecartDirection(minecart);
        if (tickAfterLastFollow++ > LockViewConfig.threshold.get()){
            lastYaw = yaw;
        }
        else if(doCorrection){
            lastYaw = normalize(lastYaw + 180f);
        }
        doCorrection = false;
        if(update) tickAfterLastFollow = 0;
        difference = normalize(yaw - lastYaw);
    }

    public static boolean setMinecartDirection(Minecart minecart){
        boolean update = false;
        float yawF = rawYaw;
        boolean correction = false;
        boolean successUpdate = updateSmartCorrection(minecart);
        if (minecart.getDeltaMovement().lengthSqr()>0.000002f) {
            if (tickAfterPistonRail != LockViewConfig.threshold.get()) tickAfterPistonRail++;
            yawF = sphericalFromVector3d(minecart.getDeltaMovement());
            update = true;
            correction = true;
            if(pistorRailTick != 0)pistorRailTick--;
        }
        else if(minecart.getDeltaMovement().lengthSqr() == 0f && successUpdate && posVelocity.lengthSqr() > 0.02f){
            if(pistorRailTick != LockViewConfig.threshold.get()){
                pistorRailTick++;
            }
            else {
                tickAfterPistonRail = 0;
            }
            yawF = getEighthDirection(sphericalFromVector3d(posVelocity));
            update = true;
        }
        else {
            if(pistorRailTick != 0)pistorRailTick--;
        }

        rawLastYaw = rawYaw;
        rawYaw = yawF;
        if(correction){
            checkSmartCorrection(minecart, successUpdate);
        }
        setMinecartDirection(yawF);
        return update;
    }
    //-------------methods-----------------------------




    private static float sphericalFromVector3d(Vec3 vec3d){
        //float f = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
        //pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
        return (float)(Math.atan2(-vec3d.x, vec3d.z) * 57.2957763671875D);
    }

    public static boolean onStartRiding(){
        lastCoord = null;
        tickAfterLastFollow = 100;
        lastVelocity = Vec3.ZERO;
        lastSlowdown = 100;
        tickAfterPistonRail = LockViewConfig.threshold.get();
        pistorRailTick = 0;
        return !enabled;
    }

    private static void setMinecartDirection(float yawF){
        if (LockViewConfig.smoothMode.get()){
            if(!LockViewConfig.rollerCoasterMode.get() && tickAfterLastFollow > LockViewConfig.threshold.get()){
                yaw = yawF;
            }
            else if(doCorrection){
                yaw = normalize(yaw + 180f);
            }
            if (Math.abs(yawF - yaw) < 180f){
                yaw = yaw/2 + yawF/2;
            }
            else{
                float tmp = yaw/2 + yawF/2;
                //log(Level.INFO, Float.toString(yaw));
                yaw = (tmp >= 0) ? tmp - 180f : tmp + 180f;
            }
            //log(Level.INFO, Float.toString(yaw));
        }
        else{
            yaw = yawF;
        }
    }


    private static void checkSmartCorrection(Minecart minecart, boolean successUpdate){
        boolean correction = false;
        if(LockViewConfig.smartMode.get()){
            float ang = 60f;
            if (tickAfterPistonRail == LockViewConfig.threshold.get() && floatCircleDistance(rawLastYaw, rawYaw, 360) > 180f-ang && floatCircleDistance(rawLastYaw, rawYaw, 360) <180+ang) {
                correction = true;
                /*-------------------Explain, what does the following complicated code------------------------
                 *The Smart correction's aim is to make difference between a U-turn and a collision, what is'n an easy task
                 * The speed vector always rotate in 180* so I need data from somewhere else:Position->real speed(with a little delay)
                 * I observed 2 things:
                 * 1:On collision, the speed decreases (gotVelocity)
                 * 2:On taking U-turn, the real velocity vector and the minecart.getMotion()() are ~perpendicular
                 *
                 * fix to piston rail-
                 * while travelling on piston-rail
                 * gotVelocity = zero
                 * posVelocity always real (while euclidean space isn't broken)
                 *
                 *              :D          Don't give up!!! (message to everyone, who read my code)
                 */
                log(Level.INFO, "initCorrection");
                if (successUpdate) {
                    boolean bl1 = posVelocity.lengthSqr() > 0.00004f && Math.abs(posVelocity.normalize().dot(gotVelocity.normalize())) < 0.8f;//vectors dot product ~0, if vectors are ~perpendicular to each other
                    boolean bl2 = (!bl1) || lastSlowdown < LockViewConfig.threshold.get() && Math.abs(posVelocity.normalize().dot(gotVelocity.normalize())) < 0.866f && gotVelocity.lengthSqr() < 0.32;
                    if (bl1 && !bl2) {
                        correction = false;
                        log(Level.INFO, "correction cancelled");
                    }
                }
            }
        }
        doCorrection = correction;
    }

    private static boolean updateSmartCorrection(Minecart minecart){
        boolean success = lastCoord != null;
        Vec3 pos = minecart.position();
        if(success) {
            posVelocity = new Vec3(pos.x - lastCoord.x, 0, pos.z - lastCoord.z);
            lastVelocity = (gotVelocity == null) ? new Vec3(0, 0, 0) : gotVelocity;
            gotVelocity = new Vec3(minecart.getDeltaMovement().x(), 0, minecart.getDeltaMovement().z());
            if( gotVelocity.length() != 0 && lastVelocity.length()/gotVelocity.length() > 2.4d) lastSlowdown = 0;
            ++lastSlowdown;
        }
        lastCoord = pos;
        return success;
    }

    public static float calcYaw(float entityYaw){
        //log(Level.INFO, Float.toString(difference));
        return (LockViewConfig.rollerCoasterMode.get()) ? (entityYaw + normalize(yaw - entityYaw)) : (entityYaw + difference);
        //return entityYaw + difference;
    }


    private static float normalize(Float f){
        return (Math.abs(f) > 180) ? (f < 0) ? f + 360f : f - 360f : f;
    }

    private static float getEighthDirection(Float f){
        if(floatCircleDistance(f%90 , 0, 90) < 20){
            return ((float)Math.round(f/90))*90;
        }
        else return f;
    }

    private static float floatCircleDistance(float i1, float i2, float size){
        float dist1 = Math.abs(i1-i2);
        float dist2 = Math.abs(dist1 - size);
        return Math.min(dist1, dist2);
    }


    //-------------Mod initializer & debug-----------------

    public static List<String> getDebug(){
        List<String> list = Lists.newArrayList();
        list.add(MOD_NAME + " debug info - can be turned off");
        boolean bl1 = false;
        boolean bl2 = false;
        if(gotVelocity != null) {
            bl1 = true;
            list.add("\"fake\" velocity: " + gotVelocity.length());
        }
        if(posVelocity != null){
            list.add("real velocity: " + posVelocity.length());
            if(posVelocity.length() > 0.00000001) bl2 = true;
        }
        if(bl1 && bl2){
            list.add("quotient(fake/real): " + gotVelocity.length()/posVelocity.length());
        }
        else list.add("quotient(fake/real): ∞");
        list.add("minecart's yaw: " + rawYaw);
        list.add("Last slowdown (collision): " + lastSlowdown);
        list.add("Last success update: " + tickAfterLastFollow);
        list.add("Last piston belt rail: " + tickAfterPistonRail + " CD: " + pistorRailTick);
        return list;
    }

    /*@Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing", true);

        //setup Config
        AutoLockViewConfig.register(LockViewLockViewConfig.class, GsonConfigSerializer::new);
        config = AutoLockViewConfig.getConfigHolder(LockViewLockViewConfig.class).getConfig();
        enabled = LockViewConfig.enabled;


        //setup key
        keyBinding = FabricKeyBinding.Builder.create(
                new Identifier(MOD_ID, "toggle"),
                net.minecraft.client.util.InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                MOD_NAME
        ).build();          //pre-create key
        KeyBindingRegistry.INSTANCE.addCategory(MOD_NAME);
        KeyBindingRegistry.INSTANCE.register(keyBinding);   //register key
        ClientTickCallback.EVENT.register(e ->*/
    public static void keyEvent(InputEvent.KeyInputEvent e)
                                          {
                                              if (keyBinding.isDown()){
                                                  if(isHeld)return;
                                                  isHeld = true;
                                                  enabled = onStartRiding();
                                              }
                                              else if (isHeld){
                                                  isHeld = false;
                                              }
                                          }/*);
    }
    //net.minecraft.client.render.item.HeldItemRenderer*/
    public static void log(Level level, String message, boolean always){
        if (always || LockViewConfig.showDebug.get()) LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
    public static void log(Level level, String message){
        log(level, message, false);
    }

}