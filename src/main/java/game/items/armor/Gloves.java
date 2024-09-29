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
import com.jme3.math.FastMath;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import messages.items.MobItemInteractionMessage;
import messages.items.NewBootsMessage;
import messages.items.NewGlovesMessage;

/**
 *
 * @author 48793
 */
public class Gloves extends Armor {

    public Gloves(int id, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, template, name, node);
    }

    public Gloves(int id, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
    }

    @Override
    public void playerEquip(Player m) {
        humanMobEquip(m);
        m.getFirstPersonHands().setFpHands(this);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void humanMobEquip(HumanMob m) {
        m.setGloves(this);

        Node r = m.getSkinningControl().getAttachmentsNode("HandR");
        r.detachAllChildren();

        Node gloveR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        gloveR.move(0.449f, 0, 0);
        setupModelLight(gloveR);
        setupModelShootability(gloveR, m.getId());
        r.attachChild(gloveR);

        gloveR.getChild(0).rotate(0, -FastMath.DEG_TO_RAD * 180, 0);
    }

    @Override
    public void humanMobUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
        NewGlovesMessage msg = new NewGlovesMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void playerServerEquip(HumanMob m) {
        m.setGloves(this);
    }

    @Override
    public void playerServerUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("Armor value: ");
        builder.append(armorValue);
        return builder.toString();
    }

}
