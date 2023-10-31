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

/**
 *
 * @author 48793
 */
public class ClientSynchronizationUtils {

    private static final float MOB_ROTATION_RATE = 2;


    /* metoda interpoluj¹ca pozycjê moba (zeby nie teleportowal sie tam gdzie serwer powie ze jest,
    tylko zeby faktycznie przemieszczal sie tam ze swoja predkoscia
     */
    public static void interpolateMobPosition(Mob mob, float tpf) {
        mob.setPosInterpolationValue(mob.getPosInterpolationValue() + (mob.getSpeed() / mob.getNode().getWorldTranslation().distance(mob.getServerLocation())) * tpf);
        Vector3f newPos = mob.getNode().getWorldTranslation().clone().interpolateLocal(mob.getServerLocation(), Math.min(mob.getPosInterpolationValue(), 1));
        mob.getNode().setLocalTranslation(newPos);
    }
    
        public static void interpolateGrenadePosition(ThrownGrenade grenade, float tpf) {
        grenade.setPosInterpolationValue(grenade.getPosInterpolationValue() + (grenade.getSpeed() / grenade.getNode().getWorldTranslation().distance(grenade.getServerLocation())) * tpf);
        Vector3f newPos = grenade.getNode().getWorldTranslation().clone().interpolateLocal(grenade.getServerLocation(), Math.min(grenade.getPosInterpolationValue(), 1));
        grenade.getNode().setLocalTranslation(newPos);
    }

    public static void interpolateMobRotation(Mob mob, float tpf) {
        mob.setRotInterpolationValue(Math.min(mob.getRotInterpolationValue() + 6 * tpf,1));
        mob.getNode().getLocalRotation().slerp(mob.getServerRotation(), mob.getRotInterpolationValue());

    }

}
