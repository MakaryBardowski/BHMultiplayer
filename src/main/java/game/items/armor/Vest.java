/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import client.ClientGameAppState;
import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.Player;
import client.Main;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Mob;
import java.util.Arrays;
import messages.items.ItemInteractionMessage;
import messages.items.NewHelmetMessage;
import messages.items.NewVestMessage;

/**
 *
 * @author 48793
 */
public class Vest extends Armor {

    public Vest(int id, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, template, name, node);
    }

    public Vest(int id, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
    }

    @Override
    public void playerEquip(Player m) {
        Node n = m.getSkinningControl().getAttachmentsNode("Spine");
        n.detachAllChildren();
        Node vest = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath());
        setupModelLight(vest);
        n.attachChild(vest);
        setupModelShootability(vest, m.getId());
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

        ClientGameAppState gs = ClientGameAppState.getInstance();
        ItemInteractionMessage imsg = new ItemInteractionMessage(this, gs.getPlayer(), ItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);

    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewVestMessage msg = new NewVestMessage(this);
        msg.setReliable(true);
        return msg;
    }

}
