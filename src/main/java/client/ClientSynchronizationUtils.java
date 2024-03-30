/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import game.entities.mobs.Mob;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.grenades.ThrownGrenade;
import game.entities.mobs.HumanMob;

/**
 *
 * @author 48793
 */
public class ClientSynchronizationUtils {

    /* metoda interpoluj¹ca pozycjê moba (zeby nie teleportowal sie tam gdzie serwer powie ze jest,
    tylko zeby faktycznie przemieszczal sie tam ze swoja predkoscia
     */
    public static void interpolateMobPosition(Mob mob, float tpf) {

        mob.setPosInterpolationValue(Math.min(mob.getPosInterpolationValue() + (mob.getCachedSpeed() / mob.getNode().getWorldTranslation().distance(mob.getServerLocation())) * tpf, 1));

        Vector3f newPos = mob.getNode().getWorldTranslation().interpolateLocal(mob.getServerLocation(), mob.getPosInterpolationValue());
        
        if( mob instanceof HumanMob hm && newPos.equals(mob.getServerLocation())){
            hm.getModelComposer().setCurrentAction("Idle", "Legs");
        }
        
        mob.getNode().setLocalTranslation(newPos);

    }

    public static void interpolateGrenadePosition(ThrownGrenade grenade, float tpf) {
        grenade.setPosInterpolationValue(grenade.getPosInterpolationValue() + (grenade.getSpeed() / grenade.getNode().getWorldTranslation().distance(grenade.getServerLocation())) * tpf);
        Vector3f newPos = grenade.getNode().getWorldTranslation().clone().interpolateLocal(grenade.getServerLocation(), Math.min(grenade.getPosInterpolationValue(), 1));
        grenade.getNode().setLocalTranslation(newPos);
    }

    public static Quaternion GetXAxisRotation(Quaternion quaternion) {
        float w = quaternion.getW();
        float x = quaternion.getX();

        float a = (float) Math.sqrt((w * w) + (x * x));
        return new Quaternion(x, 0, 0, w / a);

    }

    public static Quaternion GetYAxisRotation(Quaternion quaternion) {
        float w = quaternion.getW();
        float y = quaternion.getY();
        float a = (float) Math.sqrt((w * w) + (y * y));
        return new Quaternion(0, y, 0, w / a);

    }

    public static Quaternion GetZAxisRotation(Quaternion quaternion) {
                float w = quaternion.getW();
        float z = quaternion.getZ();
        float a = (float) Math.sqrt((w * w) + (z * z));
        return new Quaternion(0, 0, z, w / a);
    }

}
