/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Filters;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.Player;
import game.items.consumable.Medpack;
import lombok.Getter;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class EntityConsumableUse extends EntityUpdateMessage {
    protected int consumableId;
    
    public EntityConsumableUse() {
    }

    public EntityConsumableUse(int id, int consumableId) {
        super(id);
        this.consumableId = consumableId;
    }

    @Override
    public void handleServer(ServerMain server) {
        Player p = (Player) getEntityByIdServer(id);
        var consumable = ((Medpack)getEntityByIdServer(consumableId));
        consumable.playerServerEquip(p);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
//        if (entityExistsLocallyClient(id)) {
//            Player p = (Player)getEntityByIdClient(id);
//            
//            ((Medpack)(getEntityByIdClient(id))).playerUseInRightHand(p);
//        }
    }


}
