/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.Player;
import client.Main;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;

/**
 *
 * @author 48793
 */
public class Gloves extends Armor {

    public Gloves(ItemTemplates.ItemTemplate template) {
        super(template);
    }

    public Gloves(ItemTemplates.ItemTemplate template, boolean droppable) {
        super(template, droppable);
    }

    @Override
    public void playerEquip(Player m) {

        Node r = m.getSkinningControl().getAttachmentsNode("HandR");
        Node l = m.getSkinningControl().getAttachmentsNode("HandL");
        r.detachAllChildren();
        l.detachAllChildren();

        Node gloveR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        setupModelLight(gloveR);
        setupModelShootability(gloveR, m.getId());
        r.attachChild(gloveR);

        Node gloveL = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "L"));
        setupModelLight(gloveL);
        setupModelShootability(gloveL, m.getId());
        l.attachChild(gloveL);
        gloveR.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
        gloveL.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
