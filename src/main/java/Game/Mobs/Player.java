/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs;

import Game.Items.ItemInterface;
import static Game.Map.Collision.MovementCollisionUtils.canMoveToLocationGround;

import Messages.MobHealthUpdateMessage;
import Messages.MobPosUpdateMessage;
import Messages.MobRotUpdateMessage;
import com.Networking.Client.ClientMain;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;

/**
 *
 * @author 48793
 */
public class Player extends HumanMob {

    private static final int HOTBAR_SIZE = 10;

    private boolean forward, backward, right, left;

    private ItemInterface[] hotbar;
    private String equipment;

    private Node firstPersonHandsNode;
    private Node firstPersonHandsCameraNode;

    public Player(int id, Node node, String name) {
        super(id, node, name);
        hotbar = new ItemInterface[HOTBAR_SIZE];
    }

    public ItemInterface[] getHotbar() {
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

    // metoda tworzaca nowego gracza, poki co gracz to po prostu kolorowe pudelko
    public static Player spawnPlayer(int id, AssetManager assetManager, Node worldNode) {
        Node playerNode = (Node) assetManager.loadModel("Models/testHuman/testHuman.j3o");

        Node gunNodeFP = (Node) assetManager.loadModel("Models/testRifleFP/testRifleFP.j3o");
        playerNode.attachChild(gunNodeFP);
        gunNodeFP.move(0, 2, 0);

        for (Spatial c : gunNodeFP.getChildren()) {
            c.setName("" + id);
            if (c != null && c instanceof Geometry) {
                Geometry g = (Geometry) c;
                Material originalMaterial = g.getMaterial();
                Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                g.setMaterial(newMaterial);

            }
        }

        for (Spatial c : playerNode.getChildren()) {
            if (c != null && c instanceof Geometry) {
                Geometry g = (Geometry) c;
                Material originalMaterial = g.getMaterial();
                Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

                newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                g.setMaterial(newMaterial);

            }
        }

        Debugging.DebugUtils.addArrow(playerNode, assetManager);

        playerNode.setName("" + id);
        playerNode.move(0, 5, 0);

        // bardzo wazne - KA¯DE DZIECKO NODA MUSI MIEC NAZWE ID!!!!!!!!!!
        playerNode.getChildren().stream().forEach(x -> x.setName("" + id));

        String name = "Gracz_" + id;
        Player p = new Player(id, playerNode, name);

        worldNode.attachChild(playerNode);

        return p;
    }

    @Override
    public void move(float tpf, ClientMain cm) {
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

                Vector3f moveVec = cm.getCamera().getLeft().negateLocal().mult(speed * tpf);;
                movementVector.addLocal(moveVec);
            }

            movementVector.setY(0);
            // UMC odpowiada za to zebys nie mogl stanac bardzo blisko sciany, daje minimalna odleglosc miedzy toba a sciana
//            removeFromGrid(cm.getWorldGrid());
            Vector3f UMC = movementVector.clone();
            if (UMC.getX() < 0) {
                UMC.setX(UMC.getX() - 0.5f);
            }
            if (UMC.getX() > 0) {
                UMC.setX(UMC.getX() + 0.5f);
            }

            if (UMC.getZ() < 0) {
                UMC.setZ(UMC.getZ() - 0.5f);
            }
            if (UMC.getZ() > 0) {
                UMC.setZ(UMC.getZ() + 0.5f);
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
        cm.getCamera().setLocation(new Vector3f(node.getWorldTranslation().x, 2.12f + node.getWorldTranslation().getY(), node.getWorldTranslation().z));

    }

    @Override
    public void die() {
        forward = false;
        backward = false;
        left = false;
        right = false;
    }

}
