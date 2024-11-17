/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import java.util.ArrayList;
import lombok.Getter;
import messages.TwoWayMessage;
import server.ServerMain;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new item.
 */
@Serializable
public class SetDefaultItemMessage extends TwoWayMessage {

    @Getter
    protected int itemId;

    @Getter
    protected int mobId;

    protected int interactionTypeIndex;

    public SetDefaultItemMessage() {
    }

    public SetDefaultItemMessage(Item item, HumanMob mob) {
        this.itemId = item.getId();
        this.mobId = mob.getId();
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        setHumanMobDefaultItem(this);
    }

    private void setHumanMobDefaultItem(SetDefaultItemMessage dmsg) {
        enqueueExecution(() -> {
            HumanMob human = (HumanMob) getMobByIdClient(dmsg.getMobId());
            Item item = getItemByIdClient(dmsg.getItemId());
            if (item instanceof Vest v) {
                human.setDefaultVest(v);
            } else if (item instanceof Helmet h) {
                human.setDefaultHelmet(h);
            } else if (item instanceof Gloves g) {
                human.setDefaultGloves(g);
            } else if (item instanceof Boots b) {
                human.setDefaultBoots(b);
            }
        });

    }

}
