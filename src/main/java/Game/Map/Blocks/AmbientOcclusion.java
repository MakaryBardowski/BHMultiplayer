/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Map.Blocks;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 *
 * @author 48793
 */
public class AmbientOcclusion {
       public static void calculateAO(Geometry g, float voxelSize, Block[][][] map) {
//        System.out.println("\n\n\n\n\n\n\n\n----------------------------NOWY VOXEL------------------------------------");
        Vector3f location = g.getWorldTranslation().divide(voxelSize);
        int vmX = (int) location.getX();  // voxelMapLocationX
        int vmY = (int) location.getY();
        int vmZ = (int) location.getZ();
//        System.out.println("VOXEL POS: " + location);

        Mesh m = g.getMesh();

        float[] colorArray = new float[24 * 4]; // vertex count in mesh *4 for RGBA
//System.out.println("BUFFER LENGTH "+ m.getFloatBuffer(VertexBuffer.Type.Position).position());
        for (int i = 0; i < m.getFloatBuffer(VertexBuffer.Type.Position).capacity(); i++) {
//            System.out.println(m.getFloatBuffer(VertexBuffer.Type.Position).capacity());
            float vertexX = m.getFloatBuffer(VertexBuffer.Type.Position).get(i++);
            float vertexY = m.getFloatBuffer(VertexBuffer.Type.Position).get(i++);
            float vertexZ = m.getFloatBuffer(VertexBuffer.Type.Position).get(i);

            int currVertexNum = (int) (i / 3);

//            VoxelAdding.addBox(g.getWorldTranslation().clone().getX() + vertexX, g.getWorldTranslation().clone().getY() + vertexY, g.getWorldTranslation().clone().getZ() + vertexZ, ColorRGBA.Green);
            int[] voxelPos = new int[]{vmX, vmY, vmZ};
            int[] vertexPos = new int[]{(int) (vertexX / voxelSize), (int) (vertexY / voxelSize), (int) (vertexZ / voxelSize)};

            calcAO_1(map, voxelPos, vertexPos, colorArray, currVertexNum, currVertexNum);

//            System.out.println("vX " + vertexX + "vY " + vertexY + " vZ " + vertexZ + "\n\n");
        }
//        System.out.println(Arrays.toString(colorArray));
        m.setBuffer(VertexBuffer.Type.Color, 4, colorArray);

    }

    public static float vertexAO(boolean side1, boolean side2, float corner) {
//        System.out.println("s1 " + side1 + "   s2 " + side2 + "   c = " + corner);
        if (side1 && side2) {
//            System.out.println("AO level: " + 0);

            return 0;
        }

//        System.out.println("AO level: " + (3 - ((side1 ? 1 : 0) + (side2 ? 1 : 0) + corner)));
        return 3 - ((side1 ? 1 : 0) + (side2 ? 1 : 0) + corner);
    }

    public static void calcAO_1(Block[][][] map, int[] voxelPos, int[] vertexPos, float[] colorArray, int currentVertex, int index) {
        currentVertex *= 4;
        // vertex pos is local to voxelPos

        int vx = voxelPos[0];
        int vy = voxelPos[1];
        int vz = voxelPos[2];

        boolean top = false;
        boolean floor = false;
        boolean left = false;
        boolean right = false;
        boolean front = false;
        boolean back = false;
        
        
        Block td  = map[vx][vy][vz];
        

        
        
        

//        System.out.println("Vertex pos: "+Arrays.toString(vertexPos));
        if (index == 23 || index == 22 || index == 21 || index == 20) { // bottom face ( -y)
            floor = true;
//                System.out.println("floor");
        } else if (index == 19 || index == 18 || index == 17 || index == 16) { // top face (+y)

            top = true;
//                                System.out.println("top");

        } else if (index == 15 || index == 14 || index == 13 || index == 12) { // left face (-x)

            left = true;
//                                System.out.println("left");

        } else if (index == 7 || index == 6 || index == 5 || index == 4) { // right face (+x)

            right = true;
//                                System.out.println("right");

        } else if (index == 11 || index == 10 || index == 9 || index == 8) { // back face (+z)

            back = true;
//                                System.out.println("back");

        } else if (index == 0 || index == 1 || index == 2 || index == 3) { // front face (-z)

            front = true;
//                                System.out.println("front");

        }

        int dx = vertexPos[0];
        int dy = vertexPos[1];
        int dz = vertexPos[2];

        boolean side1 = false;
        boolean side2 = false;
        boolean cornerBool = false;
//        System.out.println("dx: " + dx + "  dy: " + dy + "   dz: " + dz);

        if (top || floor) {
            if (dy == 0) {
                dy = -1;
            }
            if (dx == 0) {
                dx = -1;
            }
            if (dz == 0) {
                dz = -1;
            }

            side1 = mapContainsX(vx + dx, map) && mapContainsY(vy + dy, map) && map[vx + dx][vy + dy][vz] != null;
            side2 = mapContainsZ(vz + dz, map) && mapContainsY(vy + dy, map) && map[vx][vy + dy][vz + dz] != null;
            cornerBool = mapContainsX(vx + dx, map) && mapContainsY(vy + dy, map) && mapContainsZ(vz + dz, map) && map[vx + dx][vy + dy][vz + dz] != null;
//            System.out.println("testing side1: " + (vx + dx) + " , " + (vy + dy) + " , " + vz);
//            System.out.println("testing side2: " + vx + " , " + (vy + dy) + " , " + (vz + dz));
        } else if (left || right) {
            if (dx == 0) {
                dx = -1;
            }

//            System.out.println("--------VOXEL POS: "+Arrays.toString(voxelPos));
//            System.out.println("-----vertex position " + Arrays.toString(vertexPosClone));
//            System.out.println("testing side1: " + (vx + dx) + " , " + (vy + dy) + " , " + vz);
//            System.out.println("testing side2: " + vx + " , " + (vy + dy) + " , " + (vz + dz));
//            System.out.println("testing CORNERRRRR: " + (vx+dx) + " , " + (vy + (dy==0?-1:1)) + " , " + (vz + (dz==0?-1:1)) );
//                    System.out.println("vy "+vy+" dy "+dy+"   dy bool" +(dy==0?-1:1));
//
//            System.out.println("vz "+vz+"   dz" +(dz==0?-1:1));
//            
            side1 = mapContainsX(vx + dx, map) && mapContainsY(vy + (dy == 0 ? -1 : 1), map) && map[vx + dx][vy + (dy == 0 ? -1 : 1)][vz] != null;
            side2 = mapContainsX(vx + dx, map) && mapContainsZ(vz + (dz == 1 ? 1 : -1), map) && map[vx + dx][vy][vz + (dz == 1 ? 1 : -1)] != null;

            cornerBool = mapContainsX(vx + dx, map) && mapContainsY(vy + (dy == 0 ? -1 : 1), map) && mapContainsZ(vz + (dz == 0 ? -1 : 1), map) && map[vx + dx][vy + (dy == 0 ? -1 : 1)][vz + (dz == 0 ? -1 : 1)] != null;

// jesli corner y = 0 to corner y = -1 jesli 1 to +1. jesli z = 1 to corner z = +1 jesli z = 0 to corner z = -1
        } else if (front || back) {
            if (dz == 0) {
                dz = -1;
            }

            side1 = mapContainsX(vx + (dx == 0 ? -1 : 1), map) && mapContainsZ(vz + dz, map) && map[vx + (dx == 0 ? -1 : 1)][vy][vz + dz] != null;
            side2 = mapContainsY(vy + (dy == 0 ? -1 : 1), map) && mapContainsZ(vz + (dz == 1 ? 1 : -1), map) && map[vx][vy + (dy == 0 ? -1 : 1)][vz + (dz == 1 ? 1 : -1)] != null;

            cornerBool = mapContainsX(vx + (dx == 0 ? -1 : 1), map) && mapContainsY(vy + (dy == 0 ? -1 : 1), map) && mapContainsZ(vz + dz, map) && map[vx + (dx == 0 ? -1 : 1)][vy + (dy == 0 ? -1 : 1)][vz + dz] != null;

        }

//        System.out.println("VoxelPos " + Arrays.toString(voxelPos));
        int corner = cornerBool ? 1 : 0;

//        System.out.println(currentVertex);
        colorArray[currentVertex++] = vertexAO(side1, side2, corner) / 3f ; // AO value for top back left vertex
        colorArray[currentVertex++] = vertexAO(side1, side2, corner) / 3f;
        colorArray[currentVertex++] = vertexAO(side1, side2, corner) / 3f;

        colorArray[currentVertex] = 1;

    }

    public static boolean mapContainsX(int x, Block[][][] map) {

        return x < map.length && x >= 0;
    }

    public static boolean mapContainsY(int y, Block[][][] map) {

        return y < map[0].length && y >= 0;
    }

    public static boolean mapContainsZ(int z, Block[][][] map) {

        return z < map[0][0].length && z >= 0;
    }
}
