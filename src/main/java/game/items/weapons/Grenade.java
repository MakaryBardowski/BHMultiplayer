/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.ItemTemplates;
import lombok.Getter;
import messages.GrenadeThrownMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.NewGrenadeMessage;
import static client.ClientGameAppState.removeEntityByIdClient;
import game.items.Holdable;

/**
 *
 * @author 48793
 */
public class Grenade extends ThrowableWeapon {

    @Getter
    private final float throwSpeed = 40;

    public Grenade(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, damage, template, name, node);
    }

    public Grenade(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, damage, template, name, node, droppable);
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewGrenadeMessage msg = new NewGrenadeMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void attack(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerAttack(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerHoldInRightHand(Player p) {
        p.setEquippedRightHand(this);

        if (isEquippedByMe(p)) {

            AssetManager assetManager = Main.getInstance().getAssetManager();
            Node model = (Node) assetManager.loadModel(template.getFpPath());

            model.move(-.67f, -.7f, 2.3f);
//        model.setLocalRotation((new Quaternion()).fromAngleAxis(-FastMath.PI / 1, new Vector3f(-.0f, .0f, 0)));
            model.rotate(0, 1.5f * FastMath.DEG_TO_RAD, 0);
            Geometry ge = (Geometry) model.getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);

            p.getGunNode().attachChild(model);
            model.scale(0.75f);
            p.getFirstPersonCameraNode().attachChild(p.getGunNode());
            model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));

        }
    }

    @Override
    public void playerUseInRightHand(Player p) {
                System.out.println("holds trigger " +p.isHoldsTrigger());

        var cs = ClientGameAppState.getInstance();
        var grenadeInitialPosition = cs.getCamera().getLocation();
        var throwDirection = cs.getCamera().getDirection().normalize();

        var gtm = new GrenadeThrownMessage(id, grenadeInitialPosition, throwDirection);
        gtm.setReliable(true);

        cs.getClient().send(gtm);

        p.removeFromEquipment(this);
        p.unequip(this);
        removeEntityByIdClient(id);
        

    }

    @Override
    public void playerEquip(Player p) {
        Holdable unequippedItem = p.getEquippedRightHand();
        if (unequippedItem != null) {
            unequippedItem.playerUnequip(p);
        }
        p.setHoldsTrigger(false);
        playerHoldInRightHand(p);
    }

    @Override
    public void playerUnequip(Player p) {
        p.setEquippedRightHand(null);
        p.getGunNode().detachAllChildren();
    }

    @Override
    public void playerServerEquip(HumanMob m) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerServerUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private boolean isEquippedByMe(Player p) {
        return p == ClientGameAppState.getInstance().getPlayer();
    }

}
