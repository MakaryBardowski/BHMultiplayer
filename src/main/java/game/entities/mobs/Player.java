/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs;

import game.effects.EmitterPooler;
import game.items.Equippable;
import game.items.Item;

import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import client.ClientGameAppState;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.HitboxDebugControl;
import static game.map.collision.MovementCollisionUtils.collisionCheckWithMap;
import game.map.collision.RectangleCollisionShape;
import game.map.collision.WorldGrid;
import java.util.List;

/**
 *
 * @author 48793
 */
public class Player extends HumanMob {

    private static final int HOTBAR_SIZE = 10;
    private boolean forward, backward, right, left;

    // camera
    private CameraNode mainCameraNode;
    private CameraNode firstPersonCameraNode;
    private Node rotationNode = new Node();

    private Camera mainCamera;
    private final Item[] hotbar;
    private final Node gunNode = new Node("gun node");

    //controlling player actions
    private boolean viewingEquipment;
    private boolean cameraMovementLocked;
    private boolean movementControlLocked;

    @Override
    public void equip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.playerEquip(this);
        }
    }

    @Override
    public void unequip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.playerUnequip(this);
        }
    }

    public Node getGunNode() {
        return gunNode;
    }

    public Player(int id, Node node, String name, Camera mainCamera, SkinningControl skinningControl) {
        super(id, node, name, skinningControl);
        this.mainCamera = mainCamera;
        hotbar = new Item[HOTBAR_SIZE];

    }

    @Override
    public void receiveDamage(float damage) {
        super.receiveDamage(damage);

    }

    public Item[] getHotbar() {
        return hotbar;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public CameraNode getMainCameraNode() {
        return mainCameraNode;
    }

    public void setMainCameraNode(CameraNode mainCameraNode) {
        this.mainCameraNode = mainCameraNode;
    }

    public CameraNode getFirstPersonCameraNode() {
        return firstPersonCameraNode;
    }

    public void setFirstPersonCameraNode(CameraNode firstPersonCameraNode) {
        this.firstPersonCameraNode = firstPersonCameraNode;
    }

    public Node getRotationNode() {
        return rotationNode;
    }

    public void setRotationNode(Node rotationNode) {
        this.rotationNode = rotationNode;
    }

    @Override
    public void move(float tpf, ClientGameAppState cm) {
        MobRotUpdateMessage rotu = new MobRotUpdateMessage(id, node.getLocalRotation());
        cm.getClient().send(rotu);

//        WorldGrid collisionGrid = ClientGameAppState.getInstance().getGrid();
//        collisionGrid.remove(this);
//        
//        System.out.println(collisionGrid);

        if (forward || backward || left || right) {
            Vector3f movementVector = new Vector3f(0, 0, 0);

            if (forward) {

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(speed * tpf).clone();

                movementVector.addLocal(moveVec);

            }
            if (backward) {

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(speed * tpf).clone().negateLocal();

                movementVector.addLocal(moveVec);

            }
            if (left) {

                Vector3f moveVec = cm.getCamera().getLeft().mult(speed * tpf);
                movementVector.addLocal(moveVec);
            }
            if (right) {

                Vector3f moveVec = cm.getCamera().getLeft().negateLocal().mult(speed * tpf);
                movementVector.addLocal(moveVec);
            }

            movementVector.setY(0);
            // UMC odpowiada za to zebys nie mogl stanac bardzo blisko sciany, daje minimalna odleglosc miedzy toba a sciana
//            removeFromGrid(cm.getWorldGrid());
            Vector3f UMC = movementVector.clone();
            float wallCollisionRange = 0.75f;
            if (UMC.getX() < 0) {
                UMC.setX(UMC.getX() - wallCollisionRange);
            }
            if (UMC.getX() > 0) {
                UMC.setX(UMC.getX() + wallCollisionRange);
            }

            if (UMC.getZ() < 0) {
                UMC.setZ(UMC.getZ() - wallCollisionRange);
            }
            if (UMC.getZ() > 0) {
                UMC.setZ(UMC.getZ() + wallCollisionRange);
            }

            boolean[] canMoveOnAxes = collisionCheckWithMap(node, UMC, cm.getMap().getBlockWorld().getLogicMap(), cm.getBLOCK_SIZE());
            boolean canMoveOnAxisX = canMoveOnAxes[0];
            boolean canMoveOnAxisZ = canMoveOnAxes[2];

            if (canMoveOnAxisZ && doesNotCollideWithOtherObjects(new Vector3f(0, 0, movementVector.getZ()))) {
                node.move(0, 0, movementVector.getZ());
            }
            if (canMoveOnAxisX && doesNotCollideWithOtherObjects(new Vector3f(movementVector.getX(), 0, 0))) {
                node.move(movementVector.getX(), 0, 0);
            }

            if (node.getWorldTranslation().distance(serverLocation) > speed * tpf) {
                MobPosUpdateMessage posu = new MobPosUpdateMessage(id, node.getWorldTranslation());
                cm.getClient().send(posu);
            }

            
            for(int i = 0 ;i < 10000;i++){
            doesNotCollideWithOtherObjects(new Vector3f(0, 0, movementVector.getZ()));
                        doesNotCollideWithOtherObjects(new Vector3f(0, 0, movementVector.getX()));

            }
            
//            collisionGrid.insert(this);
        }
    }

    @Override
    public void die() {
        super.die();
        forward = false;
        backward = false;
        left = false;
        right = false;
    }

    @Override
    public String toString() {
        return "Player{" + id + "}";
    }

    public boolean isViewingEquipment() {
        return viewingEquipment;
    }

    public void setViewingEquipment(boolean viewingEquipment) {
        this.viewingEquipment = viewingEquipment;
    }

    public boolean isMovementControlLocked() {
        return movementControlLocked;
    }

    public void setMovementControlLocked(boolean movementControlLocked) {
        this.movementControlLocked = movementControlLocked;
    }

    public boolean isCameraMovementLocked() {
        return cameraMovementLocked;
    }

    public void setCameraMovementLocked(boolean cameraMovementLocked) {
        this.cameraMovementLocked = cameraMovementLocked;
    }

    private boolean doesNotCollideWithOtherObjects(Vector3f moveVec) {

        List<InteractiveEntity> ent = ClientGameAppState.getInstance().getMobs().values().stream().toList();
        List<Destructible> mobs = ent.stream().filter(e -> e instanceof Destructible).map(e -> (Destructible) e).toList();
        for (Destructible m : mobs) {
            if (this != m && collisionShape.wouldCollideAfterMovement(m.getCollisionShape(), moveVec)) {
                return false;
            }
        }

        return true;
    }

}
