/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Networking.Client;

import Game.Mobs.Mob;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

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

    public static void interpolateMobRotation(Mob mob, float tpf) {
        mob.setRotInterpolationValue(Math.min(mob.getRotInterpolationValue() + 6 * tpf,1));
        mob.getNode().getLocalRotation().slerp(mob.getServerRotation(), mob.getRotInterpolationValue());

    }

}
