/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import client.ClientGameAppState;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.ItemTemplates;
import java.util.ArrayList;

/**
 *
 * @author tomasz_potoczko
 */
public class Axe extends MeleeWeapon{
    private WeaponSwingControl weaponSwingControl;
    
    public Axe(float damage, ItemTemplates.ItemTemplate template, boolean droppable) {
        super(damage, template, droppable);
        
    }
    
    public Axe(float damage, ItemTemplates.ItemTemplate template) {
        super(damage, template);

    }
    
    @Override
    public void playerEquip(Player p) {
        playerHoldRight(p);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack(Mob m) {

    }
    
    @Override
    public void playerHoldRight(Player p) {
        p.setEquippedRightHand(this);
        AssetManager assetManager = Main.getInstance().getAssetManager();
        Node model = (Node) assetManager.loadModel(template.getFpPath());
        
        model.scale(.13f);
        model.move(-.1f, -.45f, .9f);
        model.rotate(-FastMath.PI/10, 1.1f*FastMath.PI/2, 0);
        
        Geometry ge = (Geometry) model.getChild(0);
        Material originalMaterial = ge.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        ge.setMaterial(newMaterial);
        
        weaponSwingControl = new WeaponSwingControl(ClientGameAppState.getInstance().getMobs().values());
        p.getGunNode().detachAllChildren();
        p.getGunNode().attachChild(model);
        p.getGunNode().addControl(weaponSwingControl);
        p.getFirstPersonCameraNode().attachChild(p.getGunNode());
    }
    
    @Override
    public void playerUseRight(Player p) {
        playerAttack(p);
    }
    
    @Override
    public void playerAttack(Player p) {
        ArrayList<InteractiveEntity> collisions = weaponSwingControl.swing();
        collisions.forEach(collision -> {
            Destructible mobHit = (Destructible)ClientGameAppState.getInstance().getMobs().get(collision.getId());
            mobHit.onShot(p, damage);
        });
    }
    
}
