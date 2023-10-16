/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.collision;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.Collidable;
import game.entities.mobs.Mob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author 48793
 */
public class MovementCollisionUtils {

    public static boolean isValidMobMovement(Mob m, Vector3f newPos, WorldGrid grid, ArrayList<Collidable> solidCollidables) {
        return m.doesNotCollideWithEntitiesAtPositionServer(newPos, grid, solidCollidables);
    }

    public static void checkPassableCollisions(Mob m, WorldGrid grid, ArrayList<Collidable> passableCollidables) {
         m.checkPassableCollisionsServer(grid, passableCollidables);
    }

    public static void sortByPassability(Set<Collidable> all, ArrayList<Collidable> solid, ArrayList<Collidable> passable) {
        for (Collidable c : all) {
            if (Collidable.isCollisionShapePassable(c)) {
                passable.add(c);
            } else {
                solid.add(c);
            }
        }
    }

    public static boolean[] collisionCheckWithMap(Node node, Vector3f movementVector, byte[][][] logicMap, int blockSize) {
        Vector3f newPosInLogicMap = calculateNewPosInLogicMap(node, movementVector, blockSize);
        boolean[] canMoveOnAxes = new boolean[3];
        canMoveOnAxes[0] = canMoveToLocationGroundX(node, newPosInLogicMap, logicMap, blockSize);
//        canMoveOnAxes[1] = canMoveToLocationGroundY(node, newPosInLogicMap, logicMap, blockSize);
        canMoveOnAxes[2] = canMoveToLocationGroundZ(node, newPosInLogicMap, logicMap, blockSize);
        return canMoveOnAxes;
    }

    private static boolean canMoveToLocationGroundX(Node node, Vector3f newPosInLogicMap, byte[][][] logicMap, int blockSize) {
        return newPosInLogicMap.getX() < logicMap.length
                && newPosInLogicMap.getX() > -1
                && logicMap[(short) newPosInLogicMap.getX()][(short) Math.floor(node.getWorldTranslation().getY() / blockSize)][(short) Math.floor(node.getWorldTranslation().getZ() / blockSize)] == 0;
    }

    private static boolean canMoveToLocationGroundZ(Node node, Vector3f newPosInLogicMap, byte[][][] logicMap, int blockSize) {
        return newPosInLogicMap.getZ() < logicMap[0][0].length
                && newPosInLogicMap.getZ() > -1
                && logicMap[(short) Math.floor(node.getWorldTranslation().getX() / blockSize)][(short) Math.floor(node.getWorldTranslation().getY() / blockSize)][(short) newPosInLogicMap.getZ()] == 0;

    }

    private static Vector3f calculateNewPosInLogicMap(Node node, Vector3f UMC, int blockSize) {
        return new Vector3f((float) Math.floor(node.getWorldTranslation().add(UMC).getX() / blockSize),
                (float) Math.floor(node.getWorldTranslation().add(UMC).getY() / blockSize),
                (float) Math.floor(node.getWorldTranslation().add(UMC).getZ() / blockSize));
    }

}
