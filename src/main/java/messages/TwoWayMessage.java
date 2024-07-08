/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Chest;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.Equippable;
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

    protected Chest getChestByIdClient(int id) {
        return ((Chest) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Chest getChestByIdServer(int id) {
        return ((Chest) ServerMain.getInstance().getLevelManagerMobs().get(id));
    }

    protected InteractiveEntity getEntityByIdServer(int id) {
        return ServerMain.getInstance().getLevelManagerMobs().get(id);
    }

    protected Mob getMobByIdServer(int id) {
        return ((Mob) ServerMain.getInstance().getLevelManagerMobs().get(id));
    }

    protected Item getItemByIdServer(int id) {
        return ((Item) ServerMain.getInstance().getLevelManagerMobs().get(id));
    }

    protected Destructible getDestructibleByIdServer(int id) {
        return ((Destructible) ServerMain.getInstance().getLevelManagerMobs().get(id));
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

    protected Destructible getDestructibleByIdClient(int id) {
        return ((Destructible) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected void removeItemFromMobEquipmentClient(int mobId, int itemId) {
        var mob = getMobByIdClient(mobId);
        var item = getItemByIdClient(itemId);
        if (item == null) {
            return;
        }
        var mobEquipment = mob.getEquipment();
        for (int i = 0; i < mobEquipment.length; i++) {
            if (mobEquipment[i] != null && mobEquipment[i].getId() == item.getId()) {
                mob.unequip(item);
                mobEquipment[i] = null;
            }
        }
    }

    protected boolean entityExistsLocallyClient(int mobId) {
        return ClientGameAppState.getInstance().getMobs().get(mobId) != null;
    }

    protected boolean entityExistsLocallyServer(int mobId) {
        return ServerMain.getInstance().getLevelManagerMobs().get(mobId) != null;
    }

    protected boolean entityNotExistsLocallyClient(int mobId) {
        return ClientGameAppState.getInstance().getMobs().get(mobId) == null;
    }

    protected boolean entityNotExistsLocallyServer(int mobId) {
        return ServerMain.getInstance().getLevelManagerMobs().get(mobId) == null;
    }

    protected HostedConnection getHostedConnectionByPlayer(Player p) {
        return ServerMain.getInstance().getHostsByPlayerId().get(p.getId());
    }

    protected void placeMob(Vector3f pos, Mob p) {
        ClientGameAppState.getInstance().getDestructibleNode().attachChild(p.getNode());
        p.getNode().setLocalTranslation(pos);
        ClientGameAppState.getInstance().getGrid().insert(p);
    }

    protected void enqueueExecution(Runnable runnable) {
        Main.getInstance().enqueue(runnable);
    }
}
