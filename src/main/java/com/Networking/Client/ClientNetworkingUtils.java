/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Networking.Client;

import Game.Mobs.Mob;
import com.jme3.math.Vector3f;

/**
 *
 * @author 48793
 */
public class ClientNetworkingUtils {


    /* metoda interpoluj¹ca pozycjê moba (zeby nie teleportowal sie tam gdzie serwer powie ze jest,
    tylko zeby faktycznie przemieszczal sie tam ze swoja predkoscia
    */
    public static void interpolateMobPosition(Mob mob, float tpf) {
        mob.setInterpolationValue(mob.getInterpolationValue() + (mob.getSpeed() / mob.getNode().getWorldTranslation().distance(mob.getServerLocation())) * tpf);
        Vector3f newPos = mob.getNode().getWorldTranslation().clone().interpolateLocal(mob.getServerLocation(), Math.min(mob.getInterpolationValue(), 1));
        mob.getNode().setLocalTranslation(newPos);

    }

}
