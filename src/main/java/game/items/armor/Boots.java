/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.armor;

import client.ClientGameAppState;
import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.items.ItemTemplates.BootsTemplate;
import messages.items.MobItemInteractionMessage;
import messages.items.NewBootsMessage;

/**
 *
 * @author 48793
 */
public class Boots extends Armor {

    public Boots(int id, BootsTemplate template, String name, Node node) {
        super(id, template, name, node);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    public Boots(int id, BootsTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    @Override
    public void humanMobEquip(HumanMob m) {
        var verticalOffset = 0.44f;
        m.setBoots(this);
        Node r = m.getSkinningControl().getAttachmentsNode("LegR");
        Node l = m.getSkinningControl().getAttachmentsNode("LegL");
        r.detachAllChildren();
        l.detachAllChildren();

        Node bootR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        setupModelLight(bootR);
        setupModelShootability(bootR, m.getId());

        bootR.move(0, verticalOffset, 0);

        r.attachChild(bootR);

        Node bootL = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "L"));
        setupModelLight(bootL);
        setupModelShootability(bootL, m.getId());
        bootL.move(0, verticalOffset, 0);

        l.attachChild(bootL);
        bootR.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
        bootL.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
    }

    @Override
    public void humanMobUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerEquip(Player m) {
        humanMobEquip(m);
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
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewBootsMessage msg = new NewBootsMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void playerServerEquip(HumanMob m) {
        m.setBoots(this);
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
