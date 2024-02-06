/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.collision;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.Collidable;
import game.entities.InteractiveEntity;
import game.entities.mobs.MudBeetle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author 48793
 */
public class WorldGrid {

    private final int cellSize;
    private final ConcurrentHashMap<Integer, Set<Collidable>> contents;

    public WorldGrid(int mapSize, int blockSize, int cellSize) {
        this.cellSize = cellSize;
        contents = new ConcurrentHashMap();

        int cellNum = (int) Math.ceil((float) mapSize / (float) cellSize) * blockSize;
        for (int x = 0; x < cellNum; x++) {
            for (int y = 0; y < cellNum; y++) {
                for (int z = 0; z < cellNum; z++) {
                    contents.put(hash(new Vector3f(x * cellSize, y * cellSize, z * cellSize)), ConcurrentHashMap.newKeySet(20));
                }
            }
        }

    }

    public int getCellSize() {
        return cellSize;
    }

    public final Integer hash(Vector3f point) {
        int x = cellSize * (int) (Math.floor(point.getX() / cellSize));
        int y = cellSize * (int) (Math.floor(point.getY() / cellSize));
        int z = cellSize * (int) (Math.floor(point.getZ() / cellSize));
        return x * 1000 + z + y * 1_000_000;
    }

    public void insert(Collidable entity) {
        float centerX = entity.getHitboxNode().getWorldTranslation().getX();

        float centerY = entity.getHitboxNode().getWorldTranslation().getY();
        float centerZ = entity.getHitboxNode().getWorldTranslation().getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width, centerY - height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY - height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ - depth};

        corners[4] = new float[]{centerX - width, centerY - height, centerZ + depth};
        corners[5] = new float[]{centerX + width, centerY - height, centerZ + depth};
        corners[6] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY + height, centerZ + depth};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            if (!cellContents.contains(entity)) {
                cellContents.add(entity);

            }
        }
    }

    public Set<Collidable> getNearbyInRadius(Vector3f pos, Float radius) {
        Set<Collidable> results = new HashSet<>();
        var temp = new Vector3f(0, 0, 0);

        Set<Collidable> cellContents;
        for (float i = pos.x - radius; i < pos.x + radius; i += cellSize) {
            for (float k = pos.z - radius; k < pos.z + radius; k += cellSize) {

                cellContents = contents.get(
                        hash(
                                temp.set(i, 0, k)
                        )
                );
                if (cellContents == null) {
                    continue;
                }
                results.addAll(cellContents);

            }
        }
        return results;
    }

    public void remove(Collidable entity) {
        float centerX = entity.getHitboxNode().getWorldTranslation().getX();
        float centerY = entity.getHitboxNode().getWorldTranslation().getY();
        float centerZ = entity.getHitboxNode().getWorldTranslation().getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width, centerY - height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY - height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ - depth};

        corners[4] = new float[]{centerX - width, centerY - height, centerZ + depth};
        corners[5] = new float[]{centerX + width, centerY - height, centerZ + depth};
        corners[6] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY + height, centerZ + depth};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            if (cellContents.contains(entity)) {
                cellContents.remove(entity);
            }
        }
    }



    public Set<Collidable> getNearbyAtPosition(Collidable entity, Vector3f pos) {
        HashSet<Collidable> output = new HashSet<>();
        float centerX = pos.getX();
        float centerY = pos.getY();
        float centerZ = pos.getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width, centerY - height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY - height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ - depth};

        corners[4] = new float[]{centerX - width, centerY - height, centerZ + depth};
        corners[5] = new float[]{centerX + width, centerY - height, centerZ + depth};
        corners[6] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY + height, centerZ + depth};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            output.addAll(cellContents);
        }
        return output;
    }

    public Set<Collidable> getNearbyCollisionShapeAtPos(Vector3f pos, CollisionShape cs) {
        HashSet<Collidable> output = new HashSet<>();
        float centerX = pos.getX();
        float centerY = pos.getY();
        float centerZ = pos.getZ();
        float width = cs.getWidth();
        float height = cs.getHeight();
        float depth = cs.getLength();
        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width, centerY - height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY - height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ - depth};

        corners[4] = new float[]{centerX - width, centerY - height, centerZ + depth};
        corners[5] = new float[]{centerX + width, centerY - height, centerZ + depth};
        corners[6] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY + height, centerZ + depth};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            output.addAll(cellContents);
        }
        return output;
    }

    public Set<Collidable> getNearby(Collidable entity) {
        return getNearbyAtPosition(entity, entity.getHitboxNode().getWorldTranslation());
    }

    public Set<Collidable> getNearbyAfterMove(Collidable entity, Vector3f moveVec) {
        HashSet<Collidable> output = new HashSet<>();
        Vector3f newPos = entity.getHitboxNode().getWorldTranslation().add(moveVec);
        float centerX = newPos.getX();
        float centerY = newPos.getY();
        float centerZ = newPos.getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width, centerY - height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY - height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ - depth};

        corners[4] = new float[]{centerX - width, centerY - height, centerZ + depth};
        corners[5] = new float[]{centerX + width, centerY - height, centerZ + depth};
        corners[6] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY + height, centerZ + depth};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            output.addAll(cellContents);
        }
        return output;
    }

    public ConcurrentHashMap<Integer, Set<Collidable>> getContents() {
        return contents;
    }

//    public void printAllEntities() {
////        System.out.println("\n\n\n\n\n\n\n\n\nall entities in collision grid");
//
//        for (var entry : contents.entrySet()) {
//            
//            System.out.print("set: "+entry.getKey()+"\n");
//            if(entry.getValue().isEmpty())
//                continue;
//            
//            for (var c : entry.getValue()) {
////                if(c.getClass() != MudBeetle.class)
//                System.out.println(c.getName() + " id " + c.getId());
//            }
//            System.out.println("-");
//        }
//
//    }

}
