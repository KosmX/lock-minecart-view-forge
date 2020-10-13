package com.kosmx.lockMinecartView;

import com.google.common.collect.Lists;
/*
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;*/      //Fabric, not Forge
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
//import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static Vec3d gotVelocity = null;
    @Nullable
    private static Vec3d posVelocity = null;

    @Nullable
    private static Vec3d lastCoord = null;
    private static float lastYaw = 0f;
    private static Vec3d lastVelocity;
    private static float rawLastYaw;
    private static float rawYaw;
    private static int tickAfterLastFollow = 0;
    private static int tickAfterPistonRail;
    private static int pistorRailTick = 0;
    private static float difference;
    private static int lastSlowdown = 0;


    //-----------------CORE METHOD-----------------------

    public static void update(EntityMinecartEmpty minecart){
        lastYaw = yaw;
        boolean update = setMinecartDirection(minecart);
        if (tickAfterLastFollow++ > LockViewConfig.threshold){
            lastYaw = yaw;
        }
        else if(doCorrection){
            lastYaw = normalize(lastYaw + 180f);
        }
        doCorrection = false;
        if(update) tickAfterLastFollow = 0;
        difference = normalize(yaw - lastYaw);
    }

    private static Vec3d getMinecartVelocity(EntityMinecart minecartEmpty){
        return new Vec3d(minecartEmpty.motionX, minecartEmpty.motionY, minecartEmpty.motionZ);
    }

    public static boolean setMinecartDirection(EntityMinecartEmpty minecart){
        boolean update = false;
        float yawF = rawYaw;
        boolean correction = false;
        boolean successUpdate = updateSmartCorrection(minecart);
        if (getMinecartVelocity(minecart).lengthSquared()>0.000002f) {
            if (tickAfterPistonRail != LockViewConfig.threshold) tickAfterPistonRail++;
            yawF = sphericalFromVec3d(getMinecartVelocity(minecart));
            update = true;
            correction = true;
            if(pistorRailTick != 0)pistorRailTick--;
        }
        else if(getMinecartVelocity(minecart).lengthSquared() == 0f && successUpdate && posVelocity.lengthSquared() > 0.02f){
            if(pistorRailTick != LockViewConfig.threshold){
                pistorRailTick++;
            }
            else {
                tickAfterPistonRail = 0;
            }
            yawF = getEighthDirection(sphericalFromVec3d(posVelocity));
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




    private static float sphericalFromVec3d(Vec3d vec3d){
        //float f = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
        //pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
        return (float)(MathHelper.atan2(-vec3d.x, vec3d.z) * 57.2957763671875D);
    }

    public static boolean onStartRiding(){
        lastCoord = null;
        tickAfterLastFollow = 100;
        lastVelocity = Vec3d.ZERO;
        lastSlowdown = 100;
        tickAfterPistonRail = LockViewConfig.threshold;
        pistorRailTick = 0;
        return !enabled;
    }

    private static void setMinecartDirection(float yawF){
        if (LockViewConfig.smoothMode){
            if(!LockViewConfig.rollerCoasterMode && tickAfterLastFollow > LockViewConfig.threshold){
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


    private static void checkSmartCorrection(EntityMinecartEmpty minecart, boolean successUpdate){
        boolean correction = false;
        if(LockViewConfig.smartMode){
            float ang = 60f;
            if (tickAfterPistonRail == LockViewConfig.threshold && floatCircleDistance(rawLastYaw, rawYaw, 360) > 180f-ang && floatCircleDistance(rawLastYaw, rawYaw, 360) <180+ang) {
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
                    boolean bl1 = posVelocity.lengthSquared() > 0.00004f && Math.abs(posVelocity.normalize().dotProduct(gotVelocity.normalize())) < 0.8f;//vectors dot product ~0, if vectors are ~perpendicular to each other
                    boolean bl2 = (!bl1) || lastSlowdown < LockViewConfig.threshold && Math.abs(posVelocity.normalize().dotProduct(gotVelocity.normalize())) < 0.866f && gotVelocity.lengthSquared() < 0.32;
                    if (bl1 && !bl2) {
                        correction = false;
                        log(Level.INFO, "correction cancelled");
                    }
                }
            }
        }
        doCorrection = correction;
    }

    private static boolean updateSmartCorrection(EntityMinecartEmpty minecart){
        boolean success = lastCoord != null;
        Vec3d pos = minecart.getPositionVector();
        if(success) {
            posVelocity = new Vec3d(pos.x - lastCoord.x, 0, pos.z - lastCoord.z);
            lastVelocity = (gotVelocity == null) ? new Vec3d(0, 0, 0) : gotVelocity;
            gotVelocity = new Vec3d(getMinecartVelocity(minecart).x, 0, getMinecartVelocity(minecart).x);
            if( gotVelocity.lengthVector() != 0 && lastVelocity.lengthVector()/gotVelocity.lengthVector() > 2.4d) lastSlowdown = 0;
            ++lastSlowdown;
        }
        lastCoord = pos;
        return success;
    }

    public static float calcYaw(float entityYaw){
        //log(Level.INFO, Float.toString(difference));
        return (LockViewConfig.rollerCoasterMode) ? (entityYaw + normalize(yaw - entityYaw)) : (entityYaw + difference);
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
            list.add("\"fake\" velocity: " + gotVelocity.lengthVector());
        }
        if(posVelocity != null){
            list.add("real velocity: " + posVelocity.lengthVector());
            if(posVelocity.lengthVector() > 0.00000001) bl2 = true;
        }
        if(bl1 && bl2){
            list.add("quotient(fake/real): " + gotVelocity.lengthVector()/posVelocity.lengthVector());
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
    //net.minecraft.client.render.item.HeldItemRenderer*/
    public static void log(Level level, String message, boolean always){
        if (always || LockViewConfig.showDebug) LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
    public static void log(Level level, String message){
        log(level, message, false);
    }

}