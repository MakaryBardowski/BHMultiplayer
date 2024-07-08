/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs;

import game.items.Equippable;
import game.items.Item;

import messages.MobRotUpdateMessage;
import client.ClientGameAppState;
import com.jme3.anim.SkinningControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import game.entities.Attribute;
import game.entities.FloatAttribute;
import FirstPersonHands.FirstPersonHands;
import client.Main;
import static client.Main.CAM_ROT_SPEED;
import static client.Main.CAM__MOVE_SPEED;
import com.jme3.anim.AnimComposer;
import com.jme3.network.AbstractMessage;
import game.entities.mobs.playerClasses.PlayerClass;
import static game.map.collision.MovementCollisionUtils.collisionCheckWithMap;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import messages.NewMobMessage;
import messages.PlayerPosUpdateRequest;

/**
 *
 * @author 48793
 */
public class Player extends HumanMob {

    public static final float IDENTIFY_RANGE = 8;
    public static final float PICKUP_RANGE = 800;

    private static final int HOTBAR_SIZE = 10;

    @Getter
    @Setter
    private boolean forward, backward, right, left, holdsTrigger;

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

    @Getter
    @Setter
    protected Item lastTargetedItem;

    @Getter
    @Setter
    protected ViewPort gunViewPort;

    @Getter
    @Setter
    protected FirstPersonHands firstPersonHands;
    
    
    @Getter
    private PlayerClass playerClass;

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

    public Player(int id, Node node, String name, Camera mainCamera, SkinningControl skinningControl, AnimComposer composer,PlayerClass playerClass) {
        super(id, node, name, skinningControl, composer);
        this.playerClass = playerClass;
        this.mainCamera = mainCamera;
        firstPersonHands = new FirstPersonHands(this);
        hotbar = new Item[HOTBAR_SIZE];
    }

    @Override
    public void receiveDamage(float damage) {
        super.receiveDamage(damage);
    }

    public Item[] getHotbar() {
        return hotbar;
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
    public void move(float tpf) {
       var cm = ClientGameAppState.getInstance();
        if ((forward || backward || left || right) && !isMovementControlLocked()) {
             movementVector.set(0,0,0);

            WorldGrid collisionGrid = cm.getGrid();
            collisionGrid.remove(this);

            if (forward) {

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(cachedSpeed * tpf).clone();
                movementVector.addLocal(moveVec);

            }
            if (backward) {

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(cachedSpeed * tpf).clone().negateLocal();
                movementVector.addLocal(moveVec);

            }
            if (left) {

                Vector3f moveVec = cm.getCamera().getLeft().mult(cachedSpeed * tpf);
                movementVector.addLocal(moveVec);
            }
            if (right) {

                Vector3f moveVec = cm.getCamera().getLeft().negateLocal().mult(cachedSpeed * tpf);
                movementVector.addLocal(moveVec);
            }

            movementVector.setY(0);

            if (wouldNotCollideWithSolidEntitiesAfterMove(new Vector3f(0, 0, movementVector.getZ()))) {
                node.move(0, 0, movementVector.getZ());
            }
            
            if (wouldNotCollideWithSolidEntitiesAfterMove(new Vector3f(movementVector.getX(), 0, 0))) {
                node.move(movementVector.getX(), 0, 0);
            }
            
            if (node.getWorldTranslation().distance(serverLocation) > cachedSpeed * tpf) {
                PlayerPosUpdateRequest posu = new PlayerPosUpdateRequest(id, node.getWorldTranslation());
                cm.getClient().send(posu);
            }

            checkCollisionWithPassableEntitiesClient();

            collisionGrid.insert(this);
        }

    }

    @Override
    public void die() {
        if (gunViewPort != null) {
            gunViewPort.detachScene(gunNode);
        }
        Main.getInstance().getFlyCam().setMoveSpeed(CAM__MOVE_SPEED);
        Main.getInstance().getFlyCam().setRotationSpeed(CAM_ROT_SPEED);

        super.die();
        forward = false;
        backward = false;
        left = false;
        right = false;
    }

    @Override
    public String toString() {
        return "Player{" + id + "} (visible name =  \"" + name + "\"  )";
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

    @Override
    public void attributeChangedNotification(int attributeId, Attribute copy) {
        if (attributeId == SPEED_ATTRIBUTE) {
            cachedSpeed = ((FloatAttribute) copy).getValue();
        }
    }

}
