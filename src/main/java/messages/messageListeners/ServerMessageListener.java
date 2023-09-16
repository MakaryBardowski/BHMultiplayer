/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.messageListeners;

import messages.SystemHealthUpdateMessage;
import messages.MobUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import client.ClientGameAppState;
import server.ServerMain;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Destructible;
import game.entities.mobs.Mob;
import game.items.Item;
import java.util.concurrent.ConcurrentLinkedQueue;
import messages.DestructibleDamageReceiveMessage;
import messages.HitscanTrailMessage;
import messages.items.ItemInteractionMessage;
import messages.items.ItemInteractionMessage.ItemInteractionType;

/**
 *
 * @author 48793
 */
@Serializable
public class ServerMessageListener implements MessageListener<HostedConnection> {

    private ServerMain serverApp;

    public ServerMessageListener() {
    }

    ;
    public ServerMessageListener(ServerMain s) {
        this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
        if (msg instanceof MobRotUpdateMessage nmsg) {
            serverApp.getMobs().get(nmsg.getId()).getNode().setLocalRotation(nmsg.getRot());

        } else if (msg instanceof MobPosUpdateMessage nmsg) {
            serverApp.getMobs().get(nmsg.getId()).getNode().setLocalTranslation(nmsg.getPos());

        } else if (msg instanceof DestructibleDamageReceiveMessage hmsg) {
            Destructible d = ((Destructible) serverApp.getMobs().get(hmsg.getTargetId()));
            d.setHealth(d.getHealth()-hmsg.getDamage());
            hmsg.setReliable(true);
            serverApp.getServer().broadcast(hmsg);
        } else if (msg instanceof HitscanTrailMessage hmsg){
            
           HostedConnection hc = serverApp.getServer().getConnection(hmsg.getClientId());
           serverApp.getServer().broadcast(Filters.notIn(hc),hmsg);

        } else if (msg instanceof ItemInteractionMessage imsg) {
            handleItemInteraction(imsg);
        }
    }

    public void handleItemInteraction(ItemInteractionMessage imsg) {
        if (imsg.getInteractionType() == ItemInteractionType.PICK_UP) {
//            getMobById(imsg.getMobId()).addToEquipment(getItemById(imsg.getItemId()));
            serverApp.getServer().broadcast(imsg);
        } else if (imsg.getInteractionType() == ItemInteractionType.EQUIP) {
//            getMobById(imsg.getMobId()).equip(getItemById(imsg.getItemId()));
            serverApp.getServer().broadcast(imsg);
        } else if (imsg.getInteractionType() == ItemInteractionType.UNEQUIP) {
//            getMobById(imsg.getMobId()).unequip(getItemById(imsg.getItemId()));
            serverApp.getServer().broadcast(imsg);
        } else if (imsg.getInteractionType() == ItemInteractionType.DROP) {

        }
    }

    private Mob getMobById(int id) {
        return ((Mob) serverApp.getMobs().get(id));
    }

    private Item getItemById(int id) {
        return ((Item) serverApp.getMobs().get(id));
    }

    private Destructible getDestructibleById(int id) {
        return ((Destructible) serverApp.getMobs().get(id));
    }
}
