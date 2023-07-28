/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.mobs;

import game.effects.EmitterPooler;
import game.items.Equippable;
import game.items.Item;
import static game.map.collision.MovementCollisionUtils.canMoveToLocationGround;

import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import client.ClientGameAppState;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;

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

    @Override
    public void equip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.playerEquip(this);
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


        /*tpf is time per frame,
which makes movement rate independent of fps,  checks for WSAD input and moves if detected
         */
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

            boolean[] canMoveOnAxes = canMoveToLocationGround(node, UMC, cm.getMap().getBlockWorld().getLogicMap(), cm.getBLOCK_SIZE());
            boolean canMoveOnAxisX = canMoveOnAxes[0];
            boolean canMoveOnAxisZ = canMoveOnAxes[2];

            if (canMoveOnAxisZ) {
                node.move(0, 0, movementVector.getZ());
            }
            if (canMoveOnAxisX) {
                node.move(movementVector.getX(), 0, 0);
            }

            if (node.getWorldTranslation().distance(serverLocation) > speed * tpf) {
                MobPosUpdateMessage posu = new MobPosUpdateMessage(id, node.getWorldTranslation());
                cm.getClient().send(posu);
            }

//            insert(cm.getWorldGrid());
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
        return "Player{ mainCameraNode=" + mainCameraNode + ", firstPersonCameraNode=" + firstPersonCameraNode + ", rotationNode=" + rotationNode + ", mainCamera=" + mainCamera + ", gunNode=" + gunNode + '}';
    }

}
