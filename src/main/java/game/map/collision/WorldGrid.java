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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author 48793
 */
public class WorldGrid {

    private final int cellSize;
    private final HashMap<Integer, HashSet<Collidable>> contents;

    public WorldGrid(int mapSize, int blockSize, int cellSize) {
        this.cellSize = cellSize;
        contents = new HashMap();

        int cellNum = (int) Math.ceil((float) mapSize / (float) cellSize) * blockSize;
        for (int x = 0; x < cellNum; x++) {
            for (int y = 0; y < cellNum; y++) {
                for (int z = 0; z < cellNum; z++) {
                    contents.put(hash(new Vector3f(x * cellSize, y * cellSize, z * cellSize)), new HashSet<>(0));
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

        corners[0] = new float[]{centerX - width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[1] = new float[]{centerX + width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[2] = new float[]{centerX - width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[3] = new float[]{centerX + width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[4] = new float[]{centerX - width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[5] = new float[]{centerX + width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[6] = new float[]{centerX - width / 2, centerY + height / 2, centerZ + depth / 2};
        corners[7] = new float[]{centerX + width / 2, centerY + height / 2, centerZ + depth / 2};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            if (!cellContents.contains(entity)) {
                cellContents.add(entity);

            }
        }

    }

    public void remove(Collidable entity) {
        float centerX = entity.getHitboxNode().getWorldTranslation().getX();
        float centerY = entity.getHitboxNode().getWorldTranslation().getY();
        float centerZ = entity.getHitboxNode().getWorldTranslation().getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[1] = new float[]{centerX + width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[2] = new float[]{centerX - width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[3] = new float[]{centerX + width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[4] = new float[]{centerX - width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[5] = new float[]{centerX + width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[6] = new float[]{centerX - width / 2, centerY + height / 2, centerZ + depth / 2};
        corners[7] = new float[]{centerX + width / 2, centerY + height / 2, centerZ + depth / 2};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            if (cellContents.contains(entity)) {
                cellContents.remove(entity);
            }
        }
    }

    public Set<Collidable> getNearbyAfterMove(Collidable entity) {
        HashSet<Collidable> output = new HashSet<>();
        float centerX = entity.getHitboxNode().getWorldTranslation().getX();
        float centerY = entity.getHitboxNode().getWorldTranslation().getY();
        float centerZ = entity.getHitboxNode().getWorldTranslation().getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[1] = new float[]{centerX + width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[2] = new float[]{centerX - width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[3] = new float[]{centerX + width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[4] = new float[]{centerX - width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[5] = new float[]{centerX + width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[6] = new float[]{centerX - width / 2, centerY + height / 2, centerZ + depth / 2};
        corners[7] = new float[]{centerX + width / 2, centerY + height / 2, centerZ + depth / 2};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            output.addAll(cellContents);
        }
        return output;
    }
    
    
        public Set<Collidable> getNearbyAfterMove(Collidable entity,Vector3f moveVec) {
        HashSet<Collidable> output = new HashSet<>();
        Vector3f newPos = entity.getHitboxNode().getWorldTranslation().add(moveVec);
        float centerX = newPos.getX();
        float centerY = newPos.getY();
        float centerZ = newPos.getZ();
        float width = entity.getCollisionShape().getWidth();
        float height = entity.getCollisionShape().getHeight();
        float depth = entity.getCollisionShape().getLength();

        float[][] corners = new float[8][3];

        corners[0] = new float[]{centerX - width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[1] = new float[]{centerX + width / 2, centerY - height / 2, centerZ - depth / 2};
        corners[2] = new float[]{centerX - width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[3] = new float[]{centerX + width / 2, centerY + height / 2, centerZ - depth / 2};
        corners[4] = new float[]{centerX - width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[5] = new float[]{centerX + width / 2, centerY - height / 2, centerZ + depth / 2};
        corners[6] = new float[]{centerX - width / 2, centerY + height / 2, centerZ + depth / 2};
        corners[7] = new float[]{centerX + width / 2, centerY + height / 2, centerZ + depth / 2};

        for (float[] corner : corners) {
            Vector3f v = new Vector3f(corner[0], corner[1], corner[2]);
            var cellContents = contents.get(hash(v));
            output.addAll(cellContents);
        }
        return output;
    }

    public HashMap<Integer, HashSet<Collidable>> getContents() {
        return contents;
    }

}
