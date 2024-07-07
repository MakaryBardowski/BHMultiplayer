/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.CollisionShape;
import game.map.collision.RectangleAABB;
import game.map.collision.RectangleOBB;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public abstract class Collidable extends Movable {
    private static final Spatial.CullHint HITBOX_CULLING = Spatial.CullHint.Always;
    protected CollisionShape collisionShape;
    protected Node hitboxNode = new Node(); // hitbox center is at this node
    protected Geometry hitboxDebug;

    public Collidable(int id, String name, Node node) {
        super(id, name, node);
        node.attachChild(hitboxNode);
        hitboxNode.setCullHint(HITBOX_CULLING);
    }
    
    protected void createHitbox(){};

    public abstract void onCollisionClient(Collidable other);

    public abstract void onCollisionServer(Collidable other);

    public boolean wouldNotCollideWithSolidEntitiesAfterMove(Vector3f moveVec) {
        var newPos = collisionShape.getPosition().add(moveVec);
        float centerX = newPos.getX();
        float centerY = newPos.getY();
        float centerZ = newPos.getZ();
        float width = collisionShape.getWidth();
        float height = collisionShape.getHeight(); // height == 1.25??
        float depth = collisionShape.getLength();
        float[][] corners = new float[8][3];
        corners[0] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY + height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ + depth};
        corners[4] = new float[]{centerX - width, centerY, centerZ - depth};
        corners[5] = new float[]{centerX + width, centerY, centerZ - depth};
        corners[6] = new float[]{centerX - width, centerY, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY, centerZ + depth};
        var cellSize = ClientGameAppState.getInstance().getBLOCK_SIZE();
        for (var corner : corners) {
            int x = (int) (Math.floor(corner[0] / cellSize));
            int y = (int) (Math.floor(corner[1] / cellSize));
            int z = (int) (Math.floor(corner[2] / cellSize));

            if (ClientGameAppState.getInstance().getMap().getBlockWorld().getLogicMap()[x][y][z] != 0) {
                return false;
            }
        }
        // above is wall collision

        for (Collidable m : ClientGameAppState.getInstance().getGrid().getNearbyAfterMove(this, moveVec)) {
            if (isNotCollisionShapePassable(m) && this != m && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), newPos)) {
                return false;
            }
        }

        return true;
    }

    public boolean wouldNotCollideWithSolidEntitiesAfterMoveServer(Vector3f moveVec) {
        var newPos = collisionShape.getPosition().add(moveVec);
        float centerX = newPos.getX();
        float centerY = newPos.getY();
        float centerZ = newPos.getZ();
        float width = collisionShape.getWidth();
        float height = collisionShape.getHeight(); // height == 1.25??
        float depth = collisionShape.getLength();
        float[][] corners = new float[8][3];
        corners[0] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY + height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ + depth};
        corners[4] = new float[]{centerX - width, centerY, centerZ - depth};
        corners[5] = new float[]{centerX + width, centerY, centerZ - depth};
        corners[6] = new float[]{centerX - width, centerY, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY, centerZ + depth};
        var cellSize = ServerMain.getInstance().getBLOCK_SIZE();
        for (var corner : corners) {
            int x = (int) (Math.floor(corner[0] / cellSize));
            int y = (int) (Math.floor(corner[1] / cellSize));
            int z = (int) (Math.floor(corner[2] / cellSize));

            if (ServerMain.getInstance().getMap()[x][y][z] != 0) {
                return false;
            }
        }
        // above is wall collision

        for (Collidable m : ServerMain.getInstance().getGrid().getNearbyAfterMove(this, moveVec)) {
            if (isNotCollisionShapePassable(m) && this != m && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), newPos)) {
                return false;
            }
        }

        return true;
    }

    protected void showHitboxIndicator() {
        hitboxDebug = CollisionDebugUtils.createHitboxGeometry(collisionShape.getWidth(), collisionShape.getHeight(), collisionShape.getLength(), ColorRGBA.Green);
        hitboxDebug.setName("" + id);
        hitboxNode.attachChild(hitboxDebug);
    }

    protected void hideHitboxIndicator() {
        if (hitboxDebug != null) {
            hitboxDebug.removeFromParent();
        }
    }

    public static boolean isNotCollisionShapePassable(Collidable c) {
        return c.getCollisionShape() instanceof RectangleAABB;
    }

    public static boolean isCollisionShapePassable(Collidable c) {
        return c.getCollisionShape() instanceof RectangleOBB;
    }

    @Override
    public void moveServer(Vector3f moveVec) {
        if (isAbleToMove()) {
            WorldGrid grid = ServerMain.getInstance().getGrid();
            grid.remove(this);
       
            if (wouldNotCollideWithSolidEntitiesAfterMoveServer(new Vector3f(0, 0, moveVec.getZ()))) {
                node.move(0, 0, moveVec.getZ());
            }

            if (wouldNotCollideWithSolidEntitiesAfterMoveServer(new Vector3f(moveVec.getX(), 0, 0))) {
                node.move(moveVec.getX(), 0, 0);
            }

            grid.insert(this);
            checkCollisionWithPassableEntitiesServer();

        }
    }

    ;

    @Override
    public String toString() {
        return "Collidable{" + name + '}';
    }

    protected void checkCollisionWithPassableEntitiesClient() {
        Vector3f newPos = collisionShape.getPosition();
        for (Collidable m : ClientGameAppState.getInstance().getGrid().getNearby(this)) {
            if (isCollisionShapePassable(m) && this != m && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), newPos)) {
                m.onCollisionClient(this); // mine explodes etc
            }
        }

    }

    protected void checkCollisionWithPassableEntitiesServer() {
        Vector3f newPos = collisionShape.getPosition();
        for (Collidable m : ServerMain.getInstance().getGrid().getNearby(this)) {
            if (isCollisionShapePassable(m) && this != m && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), newPos)) {
                m.onCollisionServer(this); // mine explodes etc
            }
        }

    }

}
