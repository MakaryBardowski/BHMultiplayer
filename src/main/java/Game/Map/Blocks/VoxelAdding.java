/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Map.Blocks;

import static Game.Map.Blocks.AmbientOcclusion.mapContainsX;
import static Game.Map.Blocks.AmbientOcclusion.mapContainsY;
import static Game.Map.Blocks.AmbientOcclusion.mapContainsZ;
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

    public static Block addBox(float x, float y, float z, float voxelSize, int index, AssetManager asm, BlockType bt) {
        Mesh m = new Mesh();

//        float FACE_OFFSET = -0.2f;
        float FACE_OFFSET = 0;

        // Vertex positions in space
        Vector3f[] vertices = new Vector3f[24];
        vertices[0] = new Vector3f(x, y, FACE_OFFSET + z); // front bottom left
        vertices[1] = new Vector3f(voxelSize + x, y, FACE_OFFSET + z); // front bottom right
        vertices[2] = new Vector3f(x, voxelSize + y, FACE_OFFSET + z); // front top left
        vertices[3] = new Vector3f(voxelSize + x, voxelSize + y, FACE_OFFSET + z); // front top right

        vertices[4] = new Vector3f(voxelSize - FACE_OFFSET + x, y, voxelSize + z); // back bottom right 
        vertices[5] = new Vector3f(voxelSize - FACE_OFFSET + x, voxelSize + y, voxelSize + z); // back top right 
        vertices[6] = new Vector3f(voxelSize - FACE_OFFSET + x, voxelSize + y, z); // front top right
        vertices[7] = new Vector3f(voxelSize - FACE_OFFSET + x, y, z); // front bottom right

        vertices[8] = new Vector3f(voxelSize + x, voxelSize + y, voxelSize - FACE_OFFSET + z); // back top right 
        vertices[9] = new Vector3f(x, y, voxelSize - FACE_OFFSET + z); // back bottom left 
        vertices[10] = new Vector3f(voxelSize + x, y, voxelSize - FACE_OFFSET + z); // back bottom right 
        vertices[11] = new Vector3f(x, voxelSize + y, voxelSize - FACE_OFFSET + z); // back top left 

        vertices[12] = new Vector3f(FACE_OFFSET + x, y, z); // front bottom left
        vertices[13] = new Vector3f(FACE_OFFSET + x, voxelSize + y, z); // front top left
        vertices[14] = new Vector3f(FACE_OFFSET + x, y, voxelSize + z); // back bottom left 
        vertices[15] = new Vector3f(FACE_OFFSET + x, voxelSize + y, voxelSize + z); // back top left

        vertices[16] = new Vector3f(x, voxelSize - FACE_OFFSET + y, z);
        vertices[17] = new Vector3f(voxelSize + x, voxelSize - FACE_OFFSET + y, z);
        vertices[18] = new Vector3f(voxelSize + x, voxelSize - FACE_OFFSET + y, voxelSize + z);
        vertices[19] = new Vector3f(x, voxelSize - FACE_OFFSET + y, voxelSize + z);

        vertices[20] = new Vector3f(x, y - FACE_OFFSET, z);
        vertices[21] = new Vector3f(voxelSize + x, y - FACE_OFFSET, z);
        vertices[22] = new Vector3f(voxelSize + x, y - FACE_OFFSET, z + voxelSize);
        vertices[23] = new Vector3f(x, y - FACE_OFFSET, z + voxelSize);

        //                                                   
        int[] indexes = {1, 0, 2, 2, 3, 1, 4, 7, 6, 6, 5, 4, 8, 9, 10, 8, 11, 9,
            14, 13, 12, 14, 15, 13,
            18, 17, 16, 16, 19, 18,
            23, 20, 21, 21, 22, 23

        };

        for (int i = 0; i < indexes.length; i++) {

            indexes[i] += index * 24;

        }

        // Texture coordinates 
// front face GIT
        Vector2f[] texCoord = new Vector2f[24];
        texCoord[0] = bt.minTexCoord;
        texCoord[1] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
        texCoord[2] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());
        texCoord[3] = bt.maxTexCoord;

        // right face GIT
        texCoord[7] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
        texCoord[4] = bt.minTexCoord;
        texCoord[6] = bt.maxTexCoord;
        texCoord[5] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());

////        left face GIT
        texCoord[13] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());
        texCoord[12] = bt.minTexCoord;
        texCoord[15] = bt.maxTexCoord;
        texCoord[14] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
//        back face GIT
        texCoord[9] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY());
        texCoord[10] = bt.minTexCoord;
        texCoord[11] = bt.maxTexCoord;
        texCoord[8] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY());

////        top face GIT
        texCoord[17] = bt.minTexCoord; // a = 18
        texCoord[18] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY()); // 19 = b
        texCoord[16] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY()); // 23  = c
        texCoord[19] = bt.maxTexCoord; // 6 = d

// floor face GIT
        texCoord[20] = bt.minTexCoord; // a = 18
        texCoord[23] = new Vector2f(bt.minTexCoord.getX(), bt.maxTexCoord.getY()); // 19 = b
        texCoord[21] = new Vector2f(bt.maxTexCoord.getX(), bt.minTexCoord.getY()); // 23  = c
        texCoord[22] = bt.maxTexCoord; // 6 = d
/*
         texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);

        // right face GIT
        texCoord[7] = new Vector2f(1, 0);
        texCoord[4] = new Vector2f(0, 0);
        texCoord[6] = new Vector2f(1, 1);
        texCoord[5] = new Vector2f(0, 1);
    
////        left face GIT

        texCoord[13] = new Vector2f(0, 1);
        texCoord[12] = new Vector2f(0, 0);
        texCoord[15] = new Vector2f(1, 1);
        texCoord[14] = new Vector2f(1, 0);
//        back face GIT
        texCoord[9] = new Vector2f(1, 0);
        texCoord[10] = new Vector2f(0, 0);
        texCoord[11] = new Vector2f(1, 1);
        texCoord[8] = new Vector2f(0, 1);

        
        
////        top face GIT
        texCoord[17] = new Vector2f(0, 0); // a = 18
        texCoord[18] = new Vector2f(0, 1); // 19 = b
        texCoord[16] = new Vector2f(1, 0); // 23  = c
        texCoord[19] = new Vector2f(1, 1); // 6 = d

// floor face GIT
        texCoord[20] = new Vector2f(0, 0); // a = 18
        texCoord[23] = new Vector2f(0, 1); // 19 = b
        texCoord[21] = new Vector2f(1, 0); // 23  = c
        texCoord[22] = new Vector2f(1, 1); // 6 = d

         */

        m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));

        m.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexes));

        m.getFloatBuffer(VertexBuffer.Type.TexCoord).position(0);
//        System.out.println("Voxel mid generation");
//                for(int i = 0 ; i< m.getFloatBuffer(VertexBuffer.Type.TexCoord).limit();i++)
//        System.out.print(m.getFloatBuffer(VertexBuffer.Type.TexCoord).get()+", ");

        m.updateBound();

        Geometry coloredMesh = new Geometry("v" + index++, m);
        Material matVC = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");

        Texture t = asm.loadTexture(bt.textureName);
        t.setMagFilter(Texture.MagFilter.Nearest);
        matVC.setTexture("ColorMap", t);
//matVC.setColor("Color", ColorRGBA.White);
        matVC.setBoolean("VertexColor", true);

        float[] colorArray = new float[24 * 4];
        int colorIndex = 0;

        List<Vector4f> colors = new ArrayList<>();

        //Set custom RGBA value for each Vertex. Values range from 0.0f to 1.0f
        for (int i = 0; i < 24; i++) {
            colors.add(new Vector4f(1, 1, 1, 1));
            // Red value (is increased by .2 on each next vertex here)
            colorArray[colorIndex++] = 1f;
            // Green value (is reduced by .2 on each next vertex)
            colorArray[colorIndex++] = 1f;
            // Blue value (remains the same in our case)
            colorArray[colorIndex++] = 1f;
            // Alpha value (no transparency set here)
            colorArray[colorIndex++] = 1;
        }

        m.setBuffer(Type.Color, 4, colorArray);
        coloredMesh.setMaterial(matVC);

//        rn.attachChild(coloredMesh);
        Block b = new Block();
        b.setPositions(Arrays.asList(vertices));

        List<Integer> inds = new ArrayList<>();

        for (int i = 0; i < indexes.length; i++) {
            inds.add(indexes[i]);
        }

        b.setIndices(inds);
        b.setUvs(Arrays.asList(texCoord));
        b.setColors(colors);

//        System.out.println("\n\nvoxel adding final");
//                m.getFloatBuffer(VertexBuffer.Type.TexCoord).position(0);
//
//        for(int i = 0 ; i< m.getFloatBuffer(VertexBuffer.Type.TexCoord).limit();i++){
//        System.out.print(m.getFloatBuffer(VertexBuffer.Type.TexCoord).get()+", ");
//        }
//        System.out.println("\n\n");
        return b;
    }

    public static Block AddOptimizedBox(Chunk chunk, Block[][][] map, byte[][][] logicMap, int vmX, int vmY, int vmZ, float voxelSize, int index, AssetManager asm, BlockType bt, float x, float y, float z) {

        Block b = new Block();

//        System.out.println("------------------------chunk "+chunk.getChunkPos());
//        System.out.println("cull x "+x);
//        System.out.println("cull y "+y);
//        System.out.println("cull z "+z);
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

        if (vmY == 0) {
            top = true;
            floor = false;
            back = false;
            front = false;
            left = false;
            right = false;

        } else {
            top = true;
            floor = true;
            back = true;
            front = true;
            left = true;
            right = true;
        }

        int neighborCnt = (left ? 1 : 0) + (right ? 1 : 0) + (back ? 1 : 0) + (front ? 1 : 0) + (top ? 1 : 0) + (floor ? 1 : 0);
        int vertexCount = neighborCnt * 4;
        int c = 0;

        float FACE_OFFSET = 0;

        // Vertex positions in space
        Vector3f[] vertices = new Vector3f[vertexCount]; // vertexCount

        ArrayList<Short> overridenIndexes = new ArrayList<>();

        Vector2f[] texCoord = new Vector2f[vertexCount];

        int usedBooleanCount = 0;

        if (front) {

//            vertices[c++] = new Vector3f(0, 0, FACE_OFFSET); // front bottom left
//            vertices[c++] = new Vector3f(voxelSize, 0, FACE_OFFSET); // front bottom right
//            vertices[c++] = new Vector3f(0, voxelSize, FACE_OFFSET); // front top left
//            vertices[c++] = new Vector3f(voxelSize, voxelSize, FACE_OFFSET); // front top right
            vertices[c++] = new Vector3f(x, y, FACE_OFFSET + z); // front bottom left
            vertices[c++] = new Vector3f(voxelSize + x, y, FACE_OFFSET + z); // front bottom right
            vertices[c++] = new Vector3f(x, voxelSize + y, FACE_OFFSET + z); // front top left
            vertices[c++] = new Vector3f(voxelSize + x, voxelSize + y, FACE_OFFSET + z); // front top right

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

        }

        if (right) {
//            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET, 0, voxelSize); // back bottom right 
//            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET, voxelSize, voxelSize); // back top right 
//            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET, voxelSize, 0); // front top right
//            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET, 0, 0); // front bottom right

            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET + x, y, voxelSize + z); // back bottom right 
            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET + x, voxelSize + y, voxelSize + z); // back top right 
            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET + x, voxelSize + y, z); // front top right
            vertices[c++] = new Vector3f(voxelSize - FACE_OFFSET + x, y, z); // front bottom right

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

            usedBooleanCount++;

        }

        if (back) {

//            vertices[c++] = new Vector3f(voxelSize, voxelSize, voxelSize - FACE_OFFSET); // back top right 
//            vertices[c++] = new Vector3f(0, 0, voxelSize - FACE_OFFSET); // back bottom left 
//            vertices[c++] = new Vector3f(voxelSize, 0, voxelSize - FACE_OFFSET); // back bottom right 
//            vertices[c++] = new Vector3f(0, voxelSize, voxelSize - FACE_OFFSET); // back top left 
            vertices[c++] = new Vector3f(voxelSize + x, voxelSize + y, voxelSize - FACE_OFFSET + z); // back top right 
            vertices[c++] = new Vector3f(x, y, voxelSize - FACE_OFFSET + z); // back bottom left 
            vertices[c++] = new Vector3f(voxelSize + x, y, voxelSize - FACE_OFFSET + z); // back bottom right 
            vertices[c++] = new Vector3f(x, voxelSize + y, voxelSize - FACE_OFFSET + z); // back top left 

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
            usedBooleanCount++;

        }

        if (left) {
//            vertices[c++] = new Vector3f(FACE_OFFSET, 0, 0); // front bottom left
//            vertices[c++] = new Vector3f(FACE_OFFSET, voxelSize, 0); // front top left
//            vertices[c++] = new Vector3f(FACE_OFFSET, 0, voxelSize); // back bottom left 
//            vertices[c++] = new Vector3f(FACE_OFFSET, voxelSize, voxelSize); // back top left

            vertices[c++] = new Vector3f(FACE_OFFSET + x, y, z); // front bottom left
            vertices[c++] = new Vector3f(FACE_OFFSET + x, voxelSize + y, z); // front top left
            vertices[c++] = new Vector3f(FACE_OFFSET + x, y, voxelSize + z); // back bottom left 
            vertices[c++] = new Vector3f(FACE_OFFSET + x, voxelSize + y, voxelSize + z); // back top left

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

            usedBooleanCount++;

        }

        if (top) {
//            vertices[c++] = new Vector3f(0, voxelSize - FACE_OFFSET, 0);
//            vertices[c++] = new Vector3f(voxelSize, voxelSize - FACE_OFFSET, 0);
//            vertices[c++] = new Vector3f(voxelSize, voxelSize - FACE_OFFSET, voxelSize);
//            vertices[c++] = new Vector3f(0, voxelSize - FACE_OFFSET, voxelSize);

            vertices[c++] = new Vector3f(x, voxelSize - FACE_OFFSET + y, z);
            vertices[c++] = new Vector3f(voxelSize + x, voxelSize - FACE_OFFSET + y, z);
            vertices[c++] = new Vector3f(voxelSize + x, voxelSize - FACE_OFFSET + y, voxelSize + z);
            vertices[c++] = new Vector3f(x, voxelSize - FACE_OFFSET + y, voxelSize + z);

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

            usedBooleanCount++;
        }

        if (floor) {

//            vertices[c++] = new Vector3f(0, 0 - FACE_OFFSET, 0);
//            vertices[c++] = new Vector3f(voxelSize, 0 - FACE_OFFSET, 0);
//            vertices[c++] = new Vector3f(voxelSize, 0 - FACE_OFFSET, voxelSize);
//            vertices[c++] = new Vector3f(0, 0 - FACE_OFFSET, voxelSize);
            vertices[c++] = new Vector3f(x, y - FACE_OFFSET, z);
            vertices[c++] = new Vector3f(voxelSize + x, y - FACE_OFFSET, z);
            vertices[c++] = new Vector3f(voxelSize + x, y - FACE_OFFSET, z + voxelSize);
            vertices[c++] = new Vector3f(x, y - FACE_OFFSET, z + voxelSize);

            //        23, 20 ,21 ,21,22,23
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

            usedBooleanCount++;
        }

        Short[] inds = new Short[overridenIndexes.size()];
        inds = overridenIndexes.toArray(inds);
        Integer[] indexes = new Integer[inds.length];

        for (int i = 0; i < indexes.length; i++) {
//            System.out.println("chunk vs "+chunk.getVertexCount());
            indexes[i] = (int) inds[i] + chunk.getVertexCount();  // + index*24 pobawic sie indeksacja i bedzie dzialac

        }

// floor face GIT
        List<Vector4f> colors = new ArrayList<>();

        //Set custom RGBA value for each Vertex. Values range from 0.0f to 1.0f
        for (int i = 0; i < vertices.length; i++) {
            colors.add(new Vector4f(1, 1, 1, 1));

        }

        b.setPositions(Arrays.asList(vertices));
        b.setIndices(Arrays.asList(indexes));
//        System.out.println("INDICES "+b.getIndices().size());

        b.setUvs(Arrays.asList(texCoord));
        b.setColors(colors);

//        System.out.println("box indices "+ b.getIndices() + "  size "+b.getIndices().size());
//        System.out.println("\n\nvoxel adding final");
//                m.getFloatBuffer(VertexBuffer.Type.TexCoord).position(0);
//
//        for(int i = 0 ; i< m.getFloatBuffer(VertexBuffer.Type.TexCoord).limit();i++){
//        System.out.print(m.getFloatBuffer(VertexBuffer.Type.TexCoord).get()+", ");
//        }
//        System.out.println("\n\n");
//System.out.println(new HashSet<Vector3f>(Arrays.asList(vertices)));
//        System.out.println(b.getPositions());
        return b;

    }

}
