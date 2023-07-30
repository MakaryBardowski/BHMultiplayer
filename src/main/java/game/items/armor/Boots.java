/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.mobs.Player;
import client.Main;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public class Boots extends Armor {

    public Boots(ItemTemplates.ItemTemplate template) {
        super(template);
    }

    public Boots(ItemTemplates.ItemTemplate template, boolean droppable) {
        super(template, droppable);
    }

    @Override
    public void playerEquip(Player m) {
        Node r = m.getSkinningControl().getAttachmentsNode("LegR");
        Node l = m.getSkinningControl().getAttachmentsNode("LegL");
        r.detachAllChildren();
        l.detachAllChildren();

        Node bootR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        setupModelLight(bootR);
        setupModelShootability(bootR, m.getId());
        r.attachChild(bootR);

        Node bootL = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "L"));
        setupModelLight(bootL);
        setupModelShootability(bootL, m.getId());
        l.attachChild(bootL);
        bootR.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
        bootL.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
