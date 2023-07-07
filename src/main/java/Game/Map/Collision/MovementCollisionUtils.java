/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Map.Collision;

import com.Networking.Client.ClientGamAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Arrays;

/**
 *
 * @author 48793
 */
public class MovementCollisionUtils {

    public static boolean[] canMoveToLocationGround(Node node, Vector3f movementVector, byte[][][] logicMap, int blockSize) {
        Vector3f newPosInLogicMap = calculateNewPosInLogicMap(node,movementVector,blockSize);
        boolean[] canMoveOnAxes = new boolean[3];
        canMoveOnAxes[0] = canMoveToLocationGroundX(node, newPosInLogicMap, logicMap, blockSize);
//        canMoveOnAxes[1] = canMoveToLocationGroundY(node, newPosInLogicMap, logicMap, blockSize);
        canMoveOnAxes[2] = canMoveToLocationGroundZ(node, newPosInLogicMap, logicMap, blockSize);
        return canMoveOnAxes;
    }

    private static boolean canMoveToLocationGroundX(Node node, Vector3f newPosInLogicMap, byte[][][] logicMap, int blockSize) {
        return newPosInLogicMap.getX() < logicMap.length
                && newPosInLogicMap.getX() > -1
                && logicMap[(byte) newPosInLogicMap.getX()][(byte) Math.floor(node.getWorldTranslation().getY() / blockSize)][(byte) Math.floor(node.getWorldTranslation().getZ() / blockSize)] == 0;
    }

    private static boolean canMoveToLocationGroundZ(Node node, Vector3f newPosInLogicMap, byte[][][] logicMap, int blockSize) {
        return newPosInLogicMap.getZ() < logicMap[0][0].length
                && newPosInLogicMap.getZ() > -1
                && logicMap[(byte) Math.floor(node.getWorldTranslation().getX() / blockSize)][(byte) Math.floor(node.getWorldTranslation().getY() / blockSize)][(byte) newPosInLogicMap.getZ()] == 0;

    }

    private static Vector3f calculateNewPosInLogicMap(Node node, Vector3f UMC, int blockSize) {
        return new Vector3f((float) Math.floor(node.getWorldTranslation().add(UMC).getX() / blockSize),
                (float) Math.floor(node.getWorldTranslation().add(UMC).getY() / blockSize),
                (float) Math.floor(node.getWorldTranslation().add(UMC).getZ() / blockSize));
  }

    public static boolean canMoveToLocationFlying(Vector3f newPosition, ClientGamAppState cm) {

        return false;
    }

}
