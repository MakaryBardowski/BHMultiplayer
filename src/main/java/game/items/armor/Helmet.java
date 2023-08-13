/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.Player;
import client.Main;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Mob;
import game.items.ItemTemplates.ItemTemplate;
import messages.items.NewGlovesMessage;
import messages.items.NewHelmetMessage;

/**
 *
 * @author 48793
 */
public class Helmet extends Armor {

    public Helmet(int id, ItemTemplate template, String name, Node node) {
        super(id, template, name, node);
    }

    public Helmet(int id, ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
    }

    @Override
    public void playerEquip(Player m) {
        Node n = m.getSkinningControl().getAttachmentsNode("Head");
        n.detachAllChildren();
        Node head = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath());
        setupModelLight(head);
        n.attachChild(head);
        setupModelShootability(head, m.getId());
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewHelmetMessage msg = new NewHelmetMessage(this);
        msg.setReliable(true);
        return msg;
    }

}
