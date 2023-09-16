/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.messageListeners;

import game.cameraAndInput.InputController;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import messages.SystemHealthUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.NewMobMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import client.ClientGameAppState;
import client.Main;
import client.PlayerHUD;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.Chest;
import game.entities.Destructible;
import game.items.Item;
import game.items.factories.ItemFactory;
import game.items.weapons.Rifle;
import java.util.Arrays;
import java.util.concurrent.Callable;
import messages.DestructibleDamageReceiveMessage;
import messages.HitscanTrailMessage;
import messages.NewChestMessage;
import messages.items.ItemInteractionMessage;
import messages.items.ItemInteractionMessage.ItemInteractionType;
import messages.items.NewBootsMessage;
import messages.items.NewGlovesMessage;
import messages.items.NewHelmetMessage;
import messages.items.NewItemMessage;
import messages.items.NewRifleMessage;
import messages.items.NewVestMessage;

/**
 *
 * @author 48793
 */
public class ClientMessageListener implements MessageListener<Client> {

    private final ClientGameAppState clientApp;
    private final ItemFactory ifa;

    public ClientMessageListener(ClientGameAppState c) {
        this.clientApp = c;
        ifa = new ItemFactory(c.getAssetManager());
    }

    @Override
    public void messageReceived(Client s, Message m) {
        if (m instanceof MobRotUpdateMessage nmsg) {
            updateMobRotation(nmsg);
        } else if (m instanceof MobPosUpdateMessage nmsg) {
            updateMobPosition(nmsg);
        } else if (m instanceof DestructibleDamageReceiveMessage hmsg) {
            entityReceiveDamage(hmsg);
        } else if (m instanceof SystemHealthUpdateMessage hmsg) {
            updateEntityHealth(hmsg);
        } else if (m instanceof HitscanTrailMessage tmsg){
            handleHitscanTrail(tmsg);
        }else if (m instanceof ItemInteractionMessage iimsg) {
            handleItemInteraction(iimsg);
        } else if (m instanceof NewItemMessage imsg) {
            addNewItem(imsg);
        } else if (m instanceof NewMobMessage nmsg) {
            addMob(nmsg);
        } else if (m instanceof NewChestMessage nmsg) {
            addNewChest(nmsg);
        } else if (m instanceof PlayerJoinedMessage nmsg) {
            addNewPlayer(nmsg);
        } else if (m instanceof SetPlayerMessage nmsg) {
            System.out.println("got my player!");
            addMyPlayer(nmsg);
        }

    }

    private void createMyPlayer(SetPlayerMessage nmsg) {
        Player p = registerMyPlayer(nmsg);
        clientApp.setPlayer(p);
        placeMyPlayer(nmsg, p);
        addInputListeners();
        addPlayerHUD();
    }

    private void createOtherPlayer(PlayerJoinedMessage nmsg) {
        Player p = registerOtherPlayer(nmsg);
        placeOtherPlayer(nmsg, p);
    }

    private Player registerMyPlayer(SetPlayerMessage nmsg) {
        return clientApp.registerPlayer(nmsg.getId(), true);
    }

    private void placeMyPlayer(SetPlayerMessage nmsg, Player p) {
        Node playerNode = p.getNode();
        clientApp.getEntityNode().attachChild(playerNode);
        playerNode.setCullHint(Spatial.CullHint.Always);
        p.getNode().setLocalTranslation(nmsg.getPos());

    }

    private Player registerOtherPlayer(PlayerJoinedMessage nmsg) {
        return clientApp.registerPlayer(nmsg.getId(), false);
    }

    private void placeOtherPlayer(PlayerJoinedMessage nmsg, Player p) {
        placeMob(nmsg.getPos(), p);
    }

    private void placeMob(Vector3f pos, Mob p) {
        clientApp.getDestructibleNode().attachChild(p.getNode());
        p.getNode().setLocalTranslation(pos);
    }

    private void updateEntityHealth(SystemHealthUpdateMessage hmsg) {
        if (mobExistsLocally(hmsg.getId())) {
            getDestructibleById(hmsg.getId()).setHealth(hmsg.getHealth());
        }
    }

    private void updateMobPosition(MobPosUpdateMessage nmsg) {
        if (mobExistsLocally(nmsg.getId())) {
            getMobById(nmsg.getId()).setServerLocation(nmsg.getPos());
        }
    }

    private void updateMobRotation(MobRotUpdateMessage nmsg) {
        if (mobExistsLocally(nmsg.getId())) {
            getMobById(nmsg.getId()).setServerRotation(nmsg.getRot());
        }
    }

    private void addNewPlayer(PlayerJoinedMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(
                    () -> {
                        createOtherPlayer(nmsg);
                    }
            );
        }
    }

    private void addMyPlayer(SetPlayerMessage nmsg) {
        enqueueExecution(() -> {
            createMyPlayer(nmsg);
        });
    }

    private void addMob(NewMobMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(() -> {
                Mob p = clientApp.registerPlayer(nmsg.getId(), false);
                placeMob(nmsg.getPos(), p);
                p.setHealth(nmsg.getHealth());
            }
            );
        }
    }

    private boolean mobExistsLocally(int id) {
        return clientApp.getMobs().get(id) != null;
    }

    private boolean mobDoesNotExistLocally(int id) {
        return clientApp.getMobs().get(id) == null;
    }

    private void addInputListeners() {
        new InputController().createInputListeners(clientApp);
    }

    private void addPlayerHUD() {
        clientApp.getStateManager().attach(new PlayerHUD(clientApp));

    }

    private void enqueueExecution(Runnable runnable) {
        Main.getInstance().enqueue(runnable);
    }

    private Mob getMobById(int id) {
        return ((Mob) clientApp.getMobs().get(id));
    }

    private Item getItemById(int id) {
        return ((Item) clientApp.getMobs().get(id));
    }

    private Destructible getDestructibleById(int id) {
        return ((Destructible) clientApp.getMobs().get(id));
    }

    private void addNewChest(NewChestMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(() -> {
                Chest c = Chest.createRandomChestClient(nmsg.getId(), clientApp.getDestructibleNode(), nmsg.getPos(), clientApp.getAssetManager());
                clientApp.getMobs().put(c.getId(), c);
            });
        }
    }

    private void addNewItem(NewItemMessage imsg) {
        if (imsg instanceof NewHelmetMessage nhmsg) {
            Item i = ifa.createItem(nhmsg.getId(), nhmsg.getTemplate(), nhmsg.isDroppable());
            clientApp.registerEntity(i);
//            System.out.println("registering Helmet " + i + " with id = " + i.getId());

        } else if (imsg instanceof NewVestMessage nvmsg) {
            Item i = ifa.createItem(nvmsg.getId(), nvmsg.getTemplate(), nvmsg.isDroppable());
            clientApp.registerEntity(i);
//            System.out.println("registering Vest " + i + " with id = " + i.getId());

        } else if (imsg instanceof NewGlovesMessage ngmsg) {
            Item i = ifa.createItem(ngmsg.getId(), ngmsg.getTemplate(), ngmsg.isDroppable());
            clientApp.registerEntity(i);
//            System.out.println("registering Gloves " + i + " with id = " + i.getId());

        } else if (imsg instanceof NewBootsMessage nbmsg) {
            Item i = ifa.createItem(nbmsg.getId(), nbmsg.getTemplate(), nbmsg.isDroppable());
            clientApp.registerEntity(i);
//            System.out.println("registering Boots " + i + " with id = " + i.getId());
        } else if (imsg instanceof NewRifleMessage rmsg) {
            Item i = ifa.createItem(rmsg.getId(), rmsg.getTemplate(), rmsg.isDroppable());
            clientApp.registerEntity(i);
        }

    }

    private void handleItemInteraction(ItemInteractionMessage iimsg) {
        enqueueExecution(() -> {
            if (null != iimsg.getInteractionType()) {
                switch (iimsg.getInteractionType()) {
                    case EQUIP:
                        Item equipped = getItemById(iimsg.getItemId());
                        getMobById(iimsg.getMobId()).equip(equipped);
                        break;
                    case UNEQUIP:
                        Item unequipped = getItemById(iimsg.getItemId());
                        getMobById(iimsg.getMobId()).unequip(unequipped);
                        break;
                    case PICK_UP:
                        Item pickedUp = getItemById(iimsg.getItemId());

                        if (pickedUp.getDroppedItemNode() != null) {
                            pickedUp.getDroppedItemNode().removeFromParent();
                        }

                        getMobById(iimsg.getMobId()).addToEquipment(pickedUp);
                        System.out.println(getMobById(iimsg.getMobId()) + " podniosl item " + pickedUp);
                        break;
                    case DROP:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void entityReceiveDamage(DestructibleDamageReceiveMessage hmsg) {
        enqueueExecution(() -> {
            Destructible d = getDestructibleById(hmsg.getTargetId());
            d.receiveDamage(hmsg.getDamage());
        });
    }
    
    
    private void handleHitscanTrail(HitscanTrailMessage tmsg){
    enqueueExecution(() -> {
    Mob mob = getMobById(tmsg.getId());
    Rifle.createBullet(mob.getNode().getWorldTranslation().clone().add(0,1,0), tmsg.getShotPos());
    });
    }

}
