/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import static game.map.blocks.AmbientOcclusion.mapContainsX;
import static game.map.blocks.AmbientOcclusion.mapContainsY;
import static game.map.blocks.AmbientOcclusion.mapContainsZ;
import client.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author 48793
 */
public class VoxelLighting {

    public static void addLight(int xv, int yv, int zv, Block[][][] map, int range) {

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (mapContainsX(xv + x, map) && mapContainsZ(zv + z, map) && map[xv + x][yv][zv + z] != null) {

                    Vector3f or = new Vector3f(xv, 0, zv);
                    Vector3f ne = new Vector3f(xv + x, 0, zv + z);

                    float dist = or.distance(ne);

//                dist = range - dist;
                    float lighting = (0.2f + dist / range) / 1.2f; // 0.4f fuller lighting , 0.1f rounder

                    float min = (0.2f) / 1.2f;
                    float max = (0.2f + (range * (float) Math.sqrt(2)) / range) / 1.2f;

                    lighting = (lighting - min) / (max - min);

//                float lighting = (0.2f +  range / dist   ) / 1.2f; // 0.4f fuller lighting , 0.1f rounder
                    map[xv + x][yv][zv + z].setLightLevel(map[xv + x][yv][zv + z].getLightLevel() + (1 - lighting));
                }

            }

        }

//updateBlockState(xv,yv,zv,map,range);
    }

    public static void updateBlockState(int xv, int yv, int zv, Block[][][] map, int range, HashMap<String, Chunk> chunks) {

        for (int x = -range - 1; x <= range + 1; x++) {
            for (int z = -range - 1; z <= range + 1; z++) {

                if (mapContainsX(xv + x, map) && mapContainsZ(zv + z, map)) {
                    updateLighting(xv + x, yv, zv + z, map, chunks);
                }

            }

        }

//          for (int x = -range ; x <= range ; x++) {
//            for (int z = -range ; z <= range ; z++) {
//
//                if (mapContainsX(xv + x, map) && mapContainsZ(zv + z, map)) {
//                    updateLighting(xv + x, yv, zv + z, map);
//                }
//
//            }
//
//        }
    }

    public static void deleteLight(int xv, int yv, int zv, Block[][][] map, int range) {

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (mapContainsX(xv + x, map) && mapContainsZ(zv + z, map) && map[xv + x][yv][zv + z] != null) {

                    Vector3f or = new Vector3f(xv, 0, zv);
                    Vector3f ne = new Vector3f(xv + x, 0, zv + z);

                    float dist = or.distance(ne);

//                dist = range - dist;
                    float lighting = (0.2f + dist / range) / 1.2f; // 0.4f fuller lighting , 0.1f rounder

                    float min = (0.2f) / 1.2f;
                    float max = (0.2f + (range * (float) Math.sqrt(2)) / range) / 1.2f;

                    lighting = (lighting - min) / (max - min);

                    map[xv + x][yv][zv + z].setLightLevel(map[xv + x][yv][zv + z].getLightLevel() - (1 - lighting));

//                        map[xv + x][yv + y][zv + z].lightLevel = map[xv + x][yv + y][zv + z].lightLevel - lighting;
                }

            }

        }

//       updateBlockState(xv,yv,zv,map,range);
    }

    public static void updateLighting(int x, int y, int z, Block[][][] map, HashMap<String, Chunk> chunks) {
        int CHUNK_SIZE = 16;
        if (map[x][y][z] != null) {
            int chunkX = (int) (Math.floor(x / CHUNK_SIZE) * CHUNK_SIZE);
            int chunkZ = (int) (Math.floor(z / CHUNK_SIZE) * CHUNK_SIZE);
//long t = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append(chunkX);
            sb.append(chunkZ);

            String name = sb.toString();
            Chunk c = chunks.get(name);

            if (c == null) {

                return;
            }

            Mesh bgm = c.getGeometry().getMesh(); // batched node is always the last one
            int updatedGeomindex = map[x][y][z].getVertexOffsetInParentChunk() * 4;

            FloatBuffer m = bgm.getFloatBuffer(VertexBuffer.Type.Color);
            m.position(updatedGeomindex);

            int dx = (mapContainsX(x + 1, map) && mapContainsX(x - 1, map)) ? 1 : 0;
            int dy = (mapContainsY(y + 1, map) && mapContainsY(y - 1, map)) ? 1 : 0;

            int dz = (mapContainsZ(z + 1, map) && mapContainsZ(z - 1, map)) ? 1 : 0;

            int vertexCount = 0; //test

            int cnt = 0;

            for (int i = 0; i < map[x][y][z].getFaceOffsetIndexes().length; i++) {
                if (map[x][y][z].getFaceOffsetIndexes()[i] != -1) {
                    cnt++;
                }
            }

            cnt *= 4;
            // index = changedGeomIndex*24*4 ; index < changedGeomIndex+(ilosc wartosci innych niz -1 z tablicy)*4;index++
            for (int i = 0; i < cnt; i++) {
                int index = i;
//                float lightVal = map[x][y][z].lightLevel;
                float lightVal = 0;

                float r = 0;
                float g = 0;
                float b = 0;

                if (map[x][y][z].getFaceOffsetIndexes()[0] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[0] + 3) { // gorna ----------------------------------------------------------------
                    r = 1;

                    float n1 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x - dx][y][z + dz] == null ? 0 : map[x - dx][y][z + dz].getLightLevel());
                    float n3 = (map[x - dx][y][z] == null ? 0l : map[x - dx][y][z].getLightLevel());
                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n5 = (map[x][y + dy][z + dz] == null ? 0 : map[x][y + dy][z + dz].getLightLevel());
                    float n6 = (map[x - dx][y + dy][z + dz] == null ? 0 : map[x - dx][y + dy][z + dz].getLightLevel());
                    float n7 = (map[x - dx][y + dy][z] == null ? 0 : map[x - dx][y + dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[0] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[0] + 2) {
                    g = 1;

                    float n1 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x + dx][y][z + dz] == null ? 0 : map[x + dx][y][z + dz].getLightLevel());
                    float n3 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n5 = (map[x][y + dy][z + dz] == null ? 0 : map[x][y + dy][z + dz].getLightLevel());
                    float n6 = (map[x + dx][y + dy][z + dz] == null ? 0 : map[x + dx][y + dy][z + dz].getLightLevel());
                    float n7 = (map[x + dx][y + dy][z] == null ? 0 : map[x + dx][y + dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[0] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[0] + 1) {
                    b = 1;

                    float n1 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n2 = (map[x + dx][y][z - dz] == null ? 0 : map[x + dx][y][z - dz].getLightLevel());
                    float n3 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n5 = (map[x][y + dy][z - dz] == null ? 0 : map[x][y + dy][z - dz].getLightLevel());
                    float n6 = (map[x + dx][y + dy][z - dz] == null ? 0 : map[x + dx][y + dy][z - dz].getLightLevel());
                    float n7 = (map[x + dx][y + dy][z] == null ? 0 : map[x + dx][y + dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[0] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[0] + 0) {
                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel()); //g

                    float n1 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel()); //g
                    float n5 = (map[x][y + dy][z - dz] == null ? 0 : map[x][y + dy][z - dz].getLightLevel());

                    float n2 = (map[x - dx][y][z - dz] == null ? 0 : map[x - dx][y][z - dz].getLightLevel()); //g
                    float n6 = (map[x - dx][y + dy][z - dz] == null ? 0 : map[x - dx][y + dy][z - dz].getLightLevel());

                    float n3 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel()); //g
                    float n7 = (map[x - dx][y + dy][z] == null ? 0 : map[x - dx][y + dy][z].getLightLevel()); //g

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[1] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[1] + 3) { // dolna ----------------------------------------------------------------
                    r = 1;

                    float n1 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x - dx][y][z + dz] == null ? 0 : map[x - dx][y][z + dz].getLightLevel());
                    float n3 = (map[x - dx][y][z] == null ? 0l : map[x - dx][y][z].getLightLevel());
                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z + dz] == null ? 0 : map[x][y - dy][z + dz].getLightLevel());
                    float n6 = (map[x - dx][y - dy][z + dz] == null ? 0 : map[x - dx][y - dy][z + dz].getLightLevel());
                    float n7 = (map[x - dx][y - dy][z] == null ? 0 : map[x - dx][y - dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[1] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[1] + 2) {
                    g = 1;

                    float n1 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x + dx][y][z + dz] == null ? 0 : map[x + dx][y][z + dz].getLightLevel());
                    float n3 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z + dz] == null ? 0 : map[x][y - dy][z + dz].getLightLevel());
                    float n6 = (map[x + dx][y - dy][z + dz] == null ? 0 : map[x + dx][y - dy][z + dz].getLightLevel());
                    float n7 = (map[x + dx][y - dy][z] == null ? 0 : map[x + dx][y - dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[1] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[1] + 1) {
                    b = 1;

                    float n1 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n2 = (map[x + dx][y][z - dz] == null ? 0 : map[x + dx][y][z - dz].getLightLevel());
                    float n3 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z - dz] == null ? 0 : map[x][y - dy][z - dz].getLightLevel());
                    float n6 = (map[x + dx][y - dy][z - dz] == null ? 0 : map[x + dx][y - dy][z - dz].getLightLevel());
                    float n7 = (map[x + dx][y - dy][z] == null ? 0 : map[x + dx][y - dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[1] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[1]) {
                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel()); //g

                    float n1 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel()); //g
                    float n5 = (map[x][y - dy][z - dz] == null ? 0 : map[x][y - dy][z - dz].getLightLevel());

                    float n2 = (map[x - dx][y][z - dz] == null ? 0 : map[x - dx][y][z - dz].getLightLevel()); //g
                    float n6 = (map[x - dx][y - dy][z - dz] == null ? 0 : map[x - dx][y - dy][z - dz].getLightLevel());

                    float n3 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel()); //g
                    float n7 = (map[x - dx][y - dy][z] == null ? 0 : map[x - dx][y - dy][z].getLightLevel()); //g

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[2] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[2] + 3) { // lewa ----------------------------------------------------------------
                    r = 1;

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n6 = (map[x - dx][y + dy][z] == null ? 0 : map[x - dx][y + dy][z].getLightLevel());
                    float n3 = (map[x - dx][y + dy][z + dz] == null ? 0 : map[x - dx][y + dy][z + dz].getLightLevel());
                    float n1 = (map[x][y + dy][z + dz] == null ? 0 : map[x][y + dy][z + dz].getLightLevel());

                    float n5 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel());
                    float n7 = (map[x - dx][y][z + dz] == null ? 0 : map[x - dx][y][z + dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[2] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[2] + 2) {

                    g = 1;

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n6 = (map[x - dx][y - dy][z] == null ? 0 : map[x - dx][y - dy][z].getLightLevel());
                    float n3 = (map[x - dx][y - dy][z + dz] == null ? 0 : map[x - dx][y - dy][z + dz].getLightLevel());
                    float n1 = (map[x][y - dy][z + dz] == null ? 0 : map[x][y - dy][z + dz].getLightLevel());

                    float n5 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel());
                    float n7 = (map[x - dx][y][z + dz] == null ? 0 : map[x - dx][y][z + dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[2] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[2] + 1) {

                    b = 1;

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n6 = (map[x - dx][y + dy][z] == null ? 0 : map[x - dx][y + dy][z].getLightLevel());
                    float n3 = (map[x - dx][y + dy][z - dz] == null ? 0 : map[x - dx][y + dy][z - dz].getLightLevel());
                    float n1 = (map[x][y + dy][z - dz] == null ? 0 : map[x][y + dy][z - dz].getLightLevel());

                    float n5 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n2 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel());
                    float n7 = (map[x - dx][y][z - dz] == null ? 0 : map[x - dx][y][z - dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[2] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[2]) {
                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());

                    float n1 = (map[x][y - dy][z - dz] == null ? 0 : map[x][y - dy][z - dz].getLightLevel());
                    float n5 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());

                    float n2 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel());
                    float n6 = (map[x - dx][y - dy][z] == null ? 0 : map[x - dx][y - dy][z].getLightLevel());

                    float n3 = (map[x - dx][y - dy][z - dz] == null ? 0 : map[x - dx][y - dy][z - dz].getLightLevel());
                    float n7 = (map[x - dx][y][z - dz] == null ? 0 : map[x - dx][y][z - dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);
                } else if (map[x][y][z].getFaceOffsetIndexes()[3] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[3] + 1) { // prawa ----------------------------------------------------------------
                    r = 1;

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n6 = (map[x + dx][y + dy][z] == null ? 0 : map[x + dx][y + dy][z].getLightLevel());
                    float n3 = (map[x + dx][y + dy][z + dz] == null ? 0 : map[x + dx][y + dy][z + dz].getLightLevel());
                    float n1 = (map[x][y + dy][z + dz] == null ? 0 : map[x][y + dy][z + dz].getLightLevel());

                    float n5 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());
                    float n7 = (map[x + dx][y][z + dz] == null ? 0 : map[x + dx][y][z + dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[3] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[3]) {

                    g = 1;

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n6 = (map[x + dx][y - dy][z] == null ? 0 : map[x + dx][y - dy][z].getLightLevel());
                    float n3 = (map[x + dx][y - dy][z + dz] == null ? 0 : map[x + dx][y - dy][z + dz].getLightLevel());
                    float n1 = (map[x][y - dy][z + dz] == null ? 0 : map[x][y - dy][z + dz].getLightLevel());

                    float n5 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());
                    float n7 = (map[x + dx][y][z + dz] == null ? 0 : map[x + dx][y][z + dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[3] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[3] + 2) {

                    b = 1;

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n6 = (map[x + dx][y + dy][z] == null ? 0 : map[x + dx][y + dy][z].getLightLevel());
                    float n3 = (map[x + dx][y + dy][z - dz] == null ? 0 : map[x + dx][y + dy][z - dz].getLightLevel());
                    float n1 = (map[x][y + dy][z - dz] == null ? 0 : map[x][y + dy][z - dz].getLightLevel());

                    float n5 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n2 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());
                    float n7 = (map[x + dx][y][z - dz] == null ? 0 : map[x + dx][y][z - dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[3] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[3] + 3) {
                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());

                    float n1 = (map[x][y - dy][z - dz] == null ? 0 : map[x][y - dy][z - dz].getLightLevel());
                    float n5 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());

                    float n2 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());
                    float n6 = (map[x + dx][y - dy][z] == null ? 0 : map[x + dx][y - dy][z].getLightLevel());

                    float n3 = (map[x + dx][y - dy][z - dz] == null ? 0 : map[x + dx][y - dy][z - dz].getLightLevel());
                    float n7 = (map[x + dx][y][z - dz] == null ? 0 : map[x + dx][y][z - dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);
                } else if (map[x][y][z].getFaceOffsetIndexes()[5] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[5] + 3) { // tylna ----------------------------------------------------------------
                    r = 1;

                    float n1 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n2 = (map[x - dx][y][z + dz] == null ? 0 : map[x - dx][y][z + dz].getLightLevel());
                    float n3 = (map[x - dx][y][z] == null ? 0l : map[x - dx][y][z].getLightLevel());

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n5 = (map[x][y + dy][z + dz] == null ? 0 : map[x][y + dy][z + dz].getLightLevel());
                    float n6 = (map[x - dx][y + dy][z + dz] == null ? 0 : map[x - dx][y + dy][z + dz].getLightLevel());
                    float n7 = (map[x - dx][y + dy][z] == null ? 0 : map[x - dx][y + dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[5] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[5] + 2) { // git
                    g = 1;

                    float n3 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n1 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());
                    float n2 = (map[x + dx][y][z + dz] == null ? 0 : map[x + dx][y][z + dz].getLightLevel());

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z + dz] == null ? 0 : map[x][y - dy][z + dz].getLightLevel());
                    float n6 = (map[x + dx][y - dy][z] == null ? 0 : map[x + dx][y - dy][z].getLightLevel());
                    float n7 = (map[x + dx][y - dy][z + dz] == null ? 0 : map[x + dx][y - dy][z + dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[5] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[5] + 1) { // git
                    b = 1;

                    float n3 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel());
                    float n1 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel());
                    float n2 = (map[x - dx][y][z + dz] == null ? 0 : map[x - dx][y][z + dz].getLightLevel());

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z + dz] == null ? 0 : map[x][y - dy][z + dz].getLightLevel());
                    float n6 = (map[x - dx][y - dy][z] == null ? 0 : map[x - dx][y - dy][z].getLightLevel());
                    float n7 = (map[x - dx][y - dy][z + dz] == null ? 0 : map[x - dx][y - dy][z + dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[5] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[5]) {
                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel()); //g

                    float n1 = (map[x][y][z + dz] == null ? 0 : map[x][y][z + dz].getLightLevel()); //g
                    float n5 = (map[x][y + dy][z + dz] == null ? 0 : map[x][y + dy][z + dz].getLightLevel());

                    float n2 = (map[x + dx][y][z + dz] == null ? 0 : map[x + dx][y][z + dz].getLightLevel()); //g
                    float n6 = (map[x + dx][y + dy][z + dz] == null ? 0 : map[x + dx][y + dy][z + dz].getLightLevel());

                    float n3 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel()); //g
                    float n7 = (map[x + dx][y + dy][z] == null ? 0 : map[x + dx][y + dy][z].getLightLevel()); //g

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[4] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[4]) { // przednia ----------------------------------------------------------------
                    r = 1;

                    float n3 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n1 = (map[x - dx][y][z] == null ? 0 : map[x - dx][y][z].getLightLevel());
                    float n2 = (map[x - dx][y][z - dz] == null ? 0 : map[x - dx][y][z - dz].getLightLevel());

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z - dz] == null ? 0 : map[x][y - dy][z - dz].getLightLevel());
                    float n6 = (map[x - dx][y - dy][z] == null ? 0 : map[x - dx][y - dy][z].getLightLevel());
                    float n7 = (map[x - dx][y - dy][z - dz] == null ? 0 : map[x - dx][y - dy][z - dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[4] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[4] + 1) {
                    g = 1;

                    float n3 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n1 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel());
                    float n2 = (map[x + dx][y][z - dz] == null ? 0 : map[x + dx][y][z - dz].getLightLevel());

                    float n4 = (map[x][y - dy][z] == null ? 0 : map[x][y - dy][z].getLightLevel());
                    float n5 = (map[x][y - dy][z - dz] == null ? 0 : map[x][y - dy][z - dz].getLightLevel());
                    float n6 = (map[x + dx][y - dy][z] == null ? 0 : map[x + dx][y - dy][z].getLightLevel());
                    float n7 = (map[x + dx][y - dy][z - dz] == null ? 0 : map[x + dx][y - dy][z - dz].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[4] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[4] + 2) {
                    b = 1;

                    float n1 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel());
                    float n2 = (map[x - dx][y][z - dz] == null ? 0 : map[x - dx][y][z - dz].getLightLevel());
                    float n3 = (map[x - dx][y][z] == null ? 0l : map[x - dx][y][z].getLightLevel());

                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel());
                    float n5 = (map[x][y + dy][z - dz] == null ? 0 : map[x][y + dy][z - dz].getLightLevel());
                    float n6 = (map[x - dx][y + dy][z - dz] == null ? 0 : map[x - dx][y + dy][z - dz].getLightLevel());
                    float n7 = (map[x - dx][y + dy][z] == null ? 0 : map[x - dx][y + dy][z].getLightLevel());

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                } else if (map[x][y][z].getFaceOffsetIndexes()[4] != -1 && index == map[x][y][z].getFaceOffsetIndexes()[4] + 3) {
                    float n4 = (map[x][y + dy][z] == null ? 0 : map[x][y + dy][z].getLightLevel()); //g
                    float n5 = (map[x][y + dy][z - dz] == null ? 0 : map[x][y + dy][z - dz].getLightLevel());
                    float n7 = (map[x + dx][y + dy][z] == null ? 0 : map[x + dx][y + dy][z].getLightLevel()); //g
                    float n6 = (map[x + dx][y + dy][z - dz] == null ? 0 : map[x + dx][y + dy][z - dz].getLightLevel());

                    float n2 = (map[x + dx][y][z - dz] == null ? 0 : map[x + dx][y][z - dz].getLightLevel()); //g
                    float n1 = (map[x][y][z - dz] == null ? 0 : map[x][y][z - dz].getLightLevel()); //g

                    float n3 = (map[x + dx][y][z] == null ? 0 : map[x + dx][y][z].getLightLevel()); //g

                    lightVal = ((map[x][y][z].getLightLevel() + n1 + n2 + n3) + (n4 + n5 + n6 + n7)) * (0.125f);

                }

                m.put(lightVal);
                m.put(lightVal);
                m.put(lightVal);
                m.put(1);

            }

            bgm.setBuffer(VertexBuffer.Type.Color, 4, m);

        }
    }

    public static void setupModelLight(Node node) {
        for (Spatial c : node.getChildren()) {

            if (c != null) {
                if (c instanceof Geometry g) {
                    Material originalMaterial = g.getMaterial();
                    if (originalMaterial.getTextureParam("BaseColorMap") != null) {
                        Material newMaterial = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
                        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                        g.setMaterial(newMaterial);
                    }
                } else if (c instanceof Node n) {
                    setupModelLight(n);
                }
            }
        }
    }

}
