/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs;

import Game.Items.ItemInterface;
import Messages.MobUpdatePosRotMessage;
import com.Networking.Client.ClientMain;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author 48793
 */
public class Player extends HumanMob {
    // ustawiane przy klikaniu WSAD (forward = idz do przodu , left = idz w lewo  itd)
    private boolean forward, backward, right, left;
    
    private ItemInterface[] hotbar;
    private String equipment;

    public Player(int id, Node node,String name) {
        super(id, node,name);
        hotbar = new ItemInterface[10];
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
        Node playerNode = new Node(""+id);
        playerNode.move(0, 6, 0);

        
        String name = "Gracz_"+id;
        Player p = new Player(id, playerNode,name);

        Box box = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geometry = new Geometry("Box", box);

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        ColorRGBA color = new ColorRGBA(id / 2f, id / 2f, id / 2f, 1f);
        material.setColor("Color", color);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geometry.setMaterial(material);
        playerNode.attachChild(geometry);
        worldNode.attachChild(playerNode);
        
        
        
        
        
        // bardzo wazne - KA¯DE DZIECKO NODA MUSI MIEC NAZWE ID!!!!!!!!!!
        playerNode.getChildren().stream().forEach(x-> x.setName(""+id));
        
        return p;
    }

    public void move(float tpf, ClientMain gs) {
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

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(speed / 2 * tpf).clone().negateLocal();

                movementVector.addLocal(moveVec);

            }
            if (left) {

                Vector3f moveVec = gs.getCamera().getLeft().mult(speed / 2 * tpf);
                movementVector.addLocal(moveVec);
            }
            if (right) {

                Vector3f moveVec = gs.getCamera().getLeft().negateLocal().mult(speed / 2 * tpf);;
                movementVector.addLocal(moveVec);
            }
            
            // UMC odpowiada za to zebys nie mogl stanac bardzo blisko sciany, daje minimalna odleglosc miedzy toba a sciana

//            removeFromGrid(gs.getWorldGrid());
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

//            Vector3f locationAfterMovementInCell = new Vector3f((float) Math.floor(positionNode.getWorldTranslation().add(UMC.mult(1f)).getX() / gs.getWorldGrid().getCellSize()), 0, (float) Math.floor(positionNode.getWorldTranslation().add(UMC.mult(1f)).getZ() / gs.getWorldGrid().getCellSize()));
            node.move(movementVector.getX(), 0, movementVector.getZ());

//            if(node.getWorldTranslation().distance(serverLocation) > 0.5f){
            MobUpdatePosRotMessage upd = new MobUpdatePosRotMessage(id, node.getWorldTranslation().getX(), node.getWorldTranslation().getY(), node.getWorldTranslation().getZ(), null);
            gs.getClient().send(upd);
//            }
            
//            if (locationAfterMovementInCell.getZ() < gs.getTileDataMap()[0][0].length && locationAfterMovementInCell.getZ() > -1 && gs.getTileDataMap()[(int) Math.floor(positionNode.getWorldTranslation().getY() / gs.getWorldGrid().getCellSize())][(int) Math.floor(positionNode.getWorldTranslation().getX() / gs.getWorldGrid().getCellSize())][(int) locationAfterMovementInCell.getZ()].getTileType() == TileType.EMPTY) {
//                getPositionNode().move(0, 0, movementVector.getZ());
//            }
//            if (locationAfterMovementInCell.getX() < gs.getTileDataMap()[0].length && locationAfterMovementInCell.getX() > -1 && gs.getTileDataMap()[(int) Math.floor(positionNode.getWorldTranslation().getY() / gs.getWorldGrid().getCellSize())][(int) locationAfterMovementInCell.getX()][(int) Math.floor(positionNode.getWorldTranslation().getZ() / gs.getWorldGrid().getCellSize())].getTileType() == TileType.EMPTY) {
//                getPositionNode().move(movementVector.getX(), 0, 0);
//            }

//            insert(gs.getWorldGrid());
        }

    }
    
    
    

}
