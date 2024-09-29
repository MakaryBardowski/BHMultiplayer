/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import com.jme3.math.Vector3f;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class GridUtils {

    private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();
//    private static final byte[][][] grid = ServerMain.getInstance().getMap();

    public static int worldToGridCoordinate(float coordinate) {
        return (int) (Math.floor(coordinate / BLOCK_SIZE));
    }

    public static boolean isSpotEmpty(Vector3f coordinates, byte[][][] map) {
        int gridX = worldToGridCoordinate(coordinates.x);
        int gridY = worldToGridCoordinate(coordinates.y);
        int gridZ = worldToGridCoordinate(coordinates.z);
        return isSpotEmpty(gridX, gridY, gridZ,map);
    }

    public static boolean isSpotEmpty(int x, int y, int z,byte[][][] map) {
        return isInGridBounds(x,y,z,map) && map[x][y][z] == 0;
    }

    public static boolean isInGridBounds(int x, int y, int z,byte[][][] map) {
        return x < map.length && x >= 0
                && y < map[0].length && y >= 0
                && z < map[0][0].length && z >= 0;
    }
}
