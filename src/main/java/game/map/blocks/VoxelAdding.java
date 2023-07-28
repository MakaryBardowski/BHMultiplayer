/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import static game.map.blocks.AmbientOcclusion.mapContainsX;
import static game.map.blocks.AmbientOcclusion.mapContainsY;
import static game.map.blocks.AmbientOcclusion.mapContainsZ;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class VoxelAdding {

    public static Block AddOptimizedBox(Chunk chunk, Block[][][] map, byte[][][] logicMap, int vmX, int vmY, int vmZ, float voxelSize, int index, AssetManager asm, BlockType bt, float x, float y, float z) {
        Block b = new Block();
        map[vmX][vmY][vmZ] = b;
        
        boolean top = false;
        boolean floor = false;
        boolean left = false;
        boolean right = false;
        boolean front = false;
        boolean back = false;

        if (!mapContainsX(vmX - 1, map) || logicMap[vmX - 1][vmY][vmZ] == 0) {

            left = true;
        }
        if (!mapContainsX(vmX + 1, map) || logicMap[vmX + 1][vmY][vmZ] == 0) {
            right = true;
        }
        if (!mapContainsZ(vmZ - 1, map) || logicMap[vmX][vmY][vmZ - 1] == 0) {
            front = true;
        }
        if (!mapContainsZ(vmZ + 1, map) || logicMap[vmX][vmY][vmZ + 1] == 0) {
            back = true;
        }
        if (!mapContainsY(vmY + 1, map) || logicMap[vmX][vmY + 1][vmZ] == 0) {
            top = true;
        }
        if (!mapContainsY(vmY - 1, map) || logicMap[vmX][vmY - 1][vmZ] == 0) {
            floor = true;
        }



        int neighborCnt = (left ? 1 : 0) + (right ? 1 : 0) + (back ? 1 : 0) + (front ? 1 : 0) + (top ? 1 : 0) + (floor ? 1 : 0);
        int vertexCount = neighborCnt * 4;
        int c = 0;

        float FACE_OFFSET = 0;

        // Vertex positions in space
        ArrayList<Vector3f> vertices = new ArrayList<>(); // vertexCount
        ArrayList<Vector3f> normals = new ArrayList<>();

        ArrayList<Short> overridenIndexes = new ArrayList<>();

        Vector2f[] texCoord = new Vector2f[vertexCount];

        int usedBooleanCount = 0;

        int TEST_VAL = 4;

        if (front) {
            vertices.add(new Vector3f(x, y, FACE_OFFSET + z)); // front bottom left
            vertices.add(new Vector3f(voxelSize + x, y, FACE_OFFSET + z)); // front bottom right
            vertices.add(new Vector3f(x, voxelSize + y, FACE_OFFSET + z)); // front top left
            vertices.add(new Vector3f(voxelSize + x, voxelSize + y, FACE_OFFSET + z)); // front top right

            overridenIndexes.add(((short) (1 + usedBooleanCount)));
            overridenIndexes.add(((short) (0 + usedBooleanCount)));
            overridenIndexes.add(((short) (2 + usedBooleanCount)));
            overridenIndexes.add(((short) (2 + usedBooleanCount)));
            overridenIndexes.add(((short) (3 + usedBooleanCount)));
            overridenIndexes.add(((short) (1 + usedBooleanCount)));

            map[vmX][vmY][vmZ].getFaceOffsetIndexes()[4] = 0 + usedBooleanCount;

            usedBooleanCount++;

            texCoord[0] = bt.minTexCoord;
            texCoord[1] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
            texCoord[2] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());
            texCoord[3] = bt.maxTexCoord;
            for (int i = 0; i < TEST_VAL; i++) {
                normals.add(new Vector3f(0, 0, -1));
            }

        }

        if (back) {
            vertices.add(new Vector3f(voxelSize + x, voxelSize + y, voxelSize - FACE_OFFSET + z)); // back top right 
            vertices.add(new Vector3f(x, y, voxelSize - FACE_OFFSET + z)); // back bottom left 
            vertices.add(new Vector3f(voxelSize + x, y, voxelSize - FACE_OFFSET + z)); // back bottom right 
            vertices.add(new Vector3f(x, voxelSize + y, voxelSize - FACE_OFFSET + z)); // back top left 

//        // 8, 9, 10, 8, 11, 9,
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (3 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));

            map[vmX][vmY][vmZ].getFaceOffsetIndexes()[5] = (0 + (4 * usedBooleanCount));

            texCoord[0 + (usedBooleanCount * 4)] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());
            texCoord[1 + (usedBooleanCount * 4)] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
            texCoord[2 + (usedBooleanCount * 4)] = bt.minTexCoord;
            texCoord[3 + (usedBooleanCount * 4)] = bt.maxTexCoord;
            for (int i = 0; i < TEST_VAL; i++) {
                normals.add(new Vector3f(0, 0, 1));
            }

            usedBooleanCount++;

        }
        if (right) {
            vertices.add(new Vector3f(voxelSize - FACE_OFFSET + x, y, voxelSize + z)); // back bottom right 
            vertices.add(new Vector3f(voxelSize - FACE_OFFSET + x, voxelSize + y, voxelSize + z)); // back top right 
            vertices.add(new Vector3f(voxelSize - FACE_OFFSET + x, voxelSize + y, z)); // front top right
            vertices.add(new Vector3f(voxelSize - FACE_OFFSET + x, y, z)); // front bottom right

            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount)))); // 4
            overridenIndexes.add(((short) (3 + (4 * usedBooleanCount)))); // 7 
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount)))); // 6
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount)))); // 6
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount)))); // 5
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount)))); // 4

            map[vmX][vmY][vmZ].getFaceOffsetIndexes()[3] = ((short) (0 + (4 * usedBooleanCount)));

            texCoord[0 + (usedBooleanCount * 4)] = bt.minTexCoord;
            texCoord[1 + (usedBooleanCount * 4)] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());
            texCoord[2 + (usedBooleanCount * 4)] = bt.maxTexCoord;
            texCoord[3 + (usedBooleanCount * 4)] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
            for (int i = 0; i < TEST_VAL; i++) {
                normals.add(new Vector3f(1, 0, 0));
            }

            usedBooleanCount++;

        }
//
        if (left) {

            vertices.add(new Vector3f(FACE_OFFSET + x, y, z)); // front bottom left
            vertices.add(new Vector3f(FACE_OFFSET + x, voxelSize + y, z)); // front top left
            vertices.add(new Vector3f(FACE_OFFSET + x, y, voxelSize + z)); // back bottom left 
            vertices.add(new Vector3f(FACE_OFFSET + x, voxelSize + y, voxelSize + z)); // back top left

            //              14, 13, 12, 14, 15, 13,   
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (3 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));

            map[vmX][vmY][vmZ].getFaceOffsetIndexes()[2] = (0 + (4 * usedBooleanCount));

            texCoord[0 + (usedBooleanCount * 4)] = bt.minTexCoord;
            texCoord[1 + (usedBooleanCount * 4)] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());
            texCoord[2 + (usedBooleanCount * 4)] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
            texCoord[3 + (usedBooleanCount * 4)] = bt.maxTexCoord;
            for (int i = 0; i < TEST_VAL; i++) {
                normals.add(new Vector3f(-1, 0, 0));
            }

            usedBooleanCount++;

        }

        if (top) {

            vertices.add(new Vector3f(x, voxelSize - FACE_OFFSET + y, z));
            vertices.add(new Vector3f(voxelSize + x, voxelSize - FACE_OFFSET + y, z));
            vertices.add(new Vector3f(voxelSize + x, voxelSize - FACE_OFFSET + y, voxelSize + z));
            vertices.add(new Vector3f(x, voxelSize - FACE_OFFSET + y, voxelSize + z));

            //                         18, 17 ,16,16,19,18,
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (3 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount))));

            map[vmX][vmY][vmZ].getFaceOffsetIndexes()[0] = (0 + (4 * usedBooleanCount));

            texCoord[0 + (usedBooleanCount * 4)] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY()); // 23  = c
            texCoord[1 + (usedBooleanCount * 4)] = bt.minTexCoord; // a = 18
            texCoord[2 + (usedBooleanCount * 4)] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY()); // 19 = b
            texCoord[3 + (usedBooleanCount * 4)] = bt.maxTexCoord; // 6 = d

            for (int i = 0; i < TEST_VAL; i++) {
                normals.add(new Vector3f(0, 1, 0));
            }

            usedBooleanCount++;
        }

        if (floor) {
            vertices.add(new Vector3f(x, y - FACE_OFFSET, z));
            vertices.add(new Vector3f(voxelSize + x, y - FACE_OFFSET, z));
            vertices.add(new Vector3f(voxelSize + x, y - FACE_OFFSET, z + voxelSize));
            vertices.add(new Vector3f(x, y - FACE_OFFSET, z + voxelSize));

            overridenIndexes.add(((short) (3 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (0 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (1 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (2 + (4 * usedBooleanCount))));
            overridenIndexes.add(((short) (3 + (4 * usedBooleanCount))));

            map[vmX][vmY][vmZ].getFaceOffsetIndexes()[1] = (0 + (4 * usedBooleanCount));

            texCoord[0 + (4 * usedBooleanCount)] = bt.minTexCoord; // a = 18
            texCoord[1 + (4 * usedBooleanCount)] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY()); // 23  = c
            texCoord[2 + (4 * usedBooleanCount)] = bt.maxTexCoord; // 6 = d
            texCoord[3 + (4 * usedBooleanCount)] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY()); // 19 = b
            for (int i = 0; i < TEST_VAL; i++) {
                normals.add(new Vector3f(0, -1, 0));
            }
            usedBooleanCount++;
        }

        Short[] inds = new Short[overridenIndexes.size()];
        inds = overridenIndexes.toArray(inds);
        Integer[] indexes = new Integer[inds.length];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = (int) inds[i] + chunk.getVertexCount();  // + index*24 pobawic sie indeksacja i bedzie dzialac

        }

        List<Vector4f> colors = new ArrayList<>();
        //Set custom RGBA value for each Vertex. Values range from 0.0f to 1.0f
        for (int i = 0; i < vertices.size(); i++) {
            colors.add(new Vector4f(0.1f, 0.1f, 0.1f, 1));

        }

        b.setPositions(vertices);
        b.setIndices(Arrays.asList(indexes));
        b.setNormals(normals);
        b.setUvs(Arrays.asList(texCoord));
        b.setColors(colors);
        return b;

    }

}
