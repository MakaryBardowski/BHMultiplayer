/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.mobs.Player;
import client.Main;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public class Helmet extends Armor {

    public Helmet(ItemTemplates.ItemTemplate template) {
        super(template);
    }

    @Override
    public void playerEquip(Player m) {
        Node n = m.getSkinningControl().getAttachmentsNode("Head");
        n.detachAllChildren();
        Node head = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath());
        setupModelLight(head);
        n.attachChild(head);
        setupModelShootability(head,m.getId());
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
