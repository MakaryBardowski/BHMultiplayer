/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import game.entities.InteractiveEntity;
import game.entities.mobs.Mob;
import game.items.Item;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public abstract class TwoWayMessage extends AbstractMessage {

    public abstract void handleServer(ServerMain server);

    public abstract void handleClient(ClientGameAppState client);

    protected InteractiveEntity getEntityByIdServer(int id) {
        return ServerMain.getInstance().getMobs().get(id);
    }

    protected Mob getMobByIdServer(int id) {
        return ((Mob) ServerMain.getInstance().getMobs().get(id));
    }

    protected Item getItemByIdServer(int id) {
        return ((Item) ServerMain.getInstance().getMobs().get(id));
    }

    protected void removeItemFromMobEquipmentServer(int mobId, int itemId) {
        var mob = getMobByIdServer(mobId);
        var item = getItemByIdServer(itemId);
        var mobEquipment = mob.getEquipment();
        for (int i = 0; i < mobEquipment.length; i++) {
            if (mobEquipment[i] != null && mobEquipment[i].getId() == item.getId()) {
                mobEquipment[i] = null;
            }
        }
    }

    protected InteractiveEntity getEntityByIdClient(int id) {
        return ClientGameAppState.getInstance().getMobs().get(id);
    }

    protected Mob getMobByIdClient(int id) {
        return ((Mob) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Item getItemByIdClient(int id) {
        return ((Item) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected void removeItemFromMobEquipmentClient(int mobId, int itemId) {
        var mob = getMobByIdClient(mobId);
        var item = getItemByIdClient(itemId);
        var mobEquipment = mob.getEquipment();
        for (int i = 0; i < mobEquipment.length; i++) {
            if (mobEquipment[i] != null && mobEquipment[i].getId() == item.getId()) {
                mobEquipment[i] = null;
            }
        }
    }
}
