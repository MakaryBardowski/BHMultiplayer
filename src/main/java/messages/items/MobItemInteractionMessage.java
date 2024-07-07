/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.items;

import client.ClientGameAppState;
import static client.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.Mob;
import game.items.Item;
import lombok.Getter;
import messages.TwoWayMessage;
import server.ServerMain;

/**
 *
 * @author 48793 this type of message is sent by the server and informs about a
 * new item.
 */
@Serializable
public class MobItemInteractionMessage extends TwoWayMessage {

    @Getter
    protected int itemId;

    @Getter
    protected int mobId;

    protected int interactionTypeIndex;

    public MobItemInteractionMessage() {
    }

    public MobItemInteractionMessage(Item item, Mob mob, ItemInteractionType type) {
        this(item.getId(),mob.getId(),type);
    }
    
    
        public MobItemInteractionMessage(int itemId, int mobId, ItemInteractionType type) {
        this.itemId = itemId;
        this.mobId = mobId;
        this.interactionTypeIndex = type.ordinal();
    }

    @Override
    public void handleServer(ServerMain server) {
        if (getInteractionType() == ItemInteractionType.PICK_UP) {
            var mob = getMobByIdServer(mobId);
            var newItem = getItemByIdServer(itemId);
            boolean doesntHaveItem = true;
            for (var item : mob.getEquipment()) {
                if (item == newItem) {
                    doesntHaveItem = false;
                }
            }

            if (doesntHaveItem) {
                mob.addToEquipment(newItem);
                server.getServer().broadcast(this);
            }

        } else if (getInteractionType() == ItemInteractionType.EQUIP) {
            getMobByIdServer(mobId).equipServer(getItemByIdServer(itemId));
            server.getServer().broadcast(this);
        } else if (getInteractionType() == ItemInteractionType.UNEQUIP) {
//            getMobById(imsg.getMobId()).unequip(getItemById(imsg.getItemId()));
            server.getServer().broadcast(this);
        } else if (getInteractionType() == ItemInteractionType.DROP) {

        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Main.getInstance().enqueue(() -> {
            switch (getInteractionType()) {
                case EQUIP:
                    Item equipped = getItemByIdClient(itemId);
                    if (equipped == null) {
                        throw new NullPointerException("THE item with ID = " + itemId + " doesnt exist!");
                    }
                    getMobByIdClient(mobId).equip(equipped);
                    break;
                case UNEQUIP:
                    Item unequipped = getItemByIdClient(itemId);
                    getMobByIdClient(mobId).unequip(unequipped);
                    break;
                case PICK_UP:
                    Item pickedUp = getItemByIdClient(itemId);

                    if (pickedUp.getDroppedItemNode() != null) {
                        pickedUp.getDroppedItemNode().removeFromParent();
                    }

                    getMobByIdClient(mobId).addToEquipment(pickedUp);
                    System.out.println(getMobByIdClient(mobId) + " podniosl item " + pickedUp);
                    break;
                case DROP:
                    break;
                case DESTROY:
                    removeItemFromMobEquipmentClient(mobId, itemId);
                    removeEntityByIdClient(itemId);
                default:
                    break;
            }
        });
    }

    public enum ItemInteractionType {
        PICK_UP,
        DROP,
        EQUIP,
        UNEQUIP,
        DESTROY
    }

    public ItemInteractionType getInteractionType() {
        return ItemInteractionType.values()[interactionTypeIndex];
    }

}
