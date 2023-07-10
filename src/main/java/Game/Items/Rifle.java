/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Mobs.Mob;
import Game.Mobs.Player;
import com.Networking.Client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author tomasz potoczko
 */
public class Rifle extends RangedWeapon {

    @Override
    public void playerEquip(Player p) {
        playerHoldRight(p);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerHoldRight(Player p) {
        AssetManager assetManager = Main.getInstance().getAssetManager();
        Node model = (Node) assetManager.loadModel("Models/testRifleFP/testRifleFP.j3o");
        model.move(-.48f, -.52f, 1.8f);
        model.setLocalRotation((new Quaternion()).fromAngleAxis(FastMath.PI / 32, new Vector3f(-.15f, .5f, 0)));

        /// i don't know why the setupModelLight() method doesn't work <<-- big congo
        Geometry ge = (Geometry) ((Node) model.getChild(0)).getChild(0);
        Material originalMaterial = ge.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        ge.setMaterial(newMaterial);
        ///

        SkinningControl skinningControl = model.getChild(0).getControl(SkinningControl.class);
        Node muzzleNode = skinningControl.getAttachmentsNode("muzzleAttachmentBone");

        p.getGunNode().detachAllChildren();
        p.getGunNode().attachChild(model);

        model.move((p.getGunNode().getLocalTranslation().clone().subtract(model.getLocalTranslation().clone())).mult(.9f));

    }

    @Override
    public void playerUseRight(Player p) {
        attack();
    }

}
