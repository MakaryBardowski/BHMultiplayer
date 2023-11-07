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
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.Chest;
import game.entities.Destructible;
import game.entities.DestructibleDecoration;
import game.entities.InteractiveEntity;
import game.entities.factories.DestructibleDecorationFactory;
import game.entities.grenades.ClientThrownGrenadeRotateControl;
import game.entities.grenades.ThrownGrenade;
import game.entities.grenades.ThrownSmokeGrenade;
import game.entities.mobs.HumanMob;
import game.items.AmmoPack;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.factories.ItemFactory;
import game.items.weapons.RangedWeapon;
import game.items.weapons.Rifle;
import java.util.Arrays;
import java.util.concurrent.Callable;
import messages.DestructibleDamageReceiveMessage;
import messages.GrenadePosUpdateMessage;
import messages.GrenadeThrownMessage;
import messages.HitscanTrailMessage;
import messages.InstantEntityPosCorrectionMessage;
import messages.NewChestMessage;
import messages.NewDestructibleDecorationMessage;
import messages.ThrownGrenadeExplodedMessage;
import messages.TwoWayMessage;
import messages.items.ChestItemInteractionMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.MobItemInteractionMessage.ItemInteractionType;
import static messages.items.MobItemInteractionMessage.ItemInteractionType.DROP;
import static messages.items.MobItemInteractionMessage.ItemInteractionType.EQUIP;
import static messages.items.MobItemInteractionMessage.ItemInteractionType.PICK_UP;
import static messages.items.MobItemInteractionMessage.ItemInteractionType.UNEQUIP;
import messages.items.NewAmmoPackMessage;
import messages.items.NewBootsMessage;
import messages.items.NewGlovesMessage;
import messages.items.NewGrenadeMessage;
import messages.items.NewHelmetMessage;
import messages.items.NewItemMessage;
import messages.items.NewMeleeWeaponMessage;
import messages.items.NewRangedWeaponMessage;
import messages.items.NewVestMessage;
import messages.items.SetDefaultItemMessage;

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
        if (m instanceof TwoWayMessage tm) {
            tm.handleClient(clientApp);
        } else if (m instanceof MobRotUpdateMessage nmsg) {
            updateMobRotation(nmsg);
        } else if (m instanceof MobPosUpdateMessage nmsg) {
            updateMobPosition(nmsg);
        } else if (m instanceof DestructibleDamageReceiveMessage hmsg) {
            entityReceiveDamage(hmsg);
        } else if (m instanceof SystemHealthUpdateMessage hmsg) {
            updateEntityHealth(hmsg);
        } else if (m instanceof GrenadePosUpdateMessage gmsg) {
            updateGrenadePosition(gmsg);
        } else if (m instanceof InstantEntityPosCorrectionMessage cmsg) {
            correctPosition(cmsg);
        } else if (m instanceof HitscanTrailMessage tmsg) {
            handleHitscanTrail(tmsg);
        } else if (m instanceof MobItemInteractionMessage iimsg) {
            handleMobItemInteraction(iimsg);
        } else if (m instanceof GrenadeThrownMessage gmsg) {
            addNewThrownGrenade(gmsg);
        } else if (m instanceof ThrownGrenadeExplodedMessage gemsg) {
            handleGrenadeExplosion(gemsg);
        } else if (m instanceof NewItemMessage imsg) {
            addNewItem(imsg);
        } else if (m instanceof NewMobMessage nmsg) {
            addMob(nmsg);
        } else if (m instanceof SetDefaultItemMessage dmsg) {
            setHumanMobDefaultItem(dmsg);
        } else if (m instanceof ChestItemInteractionMessage cimsg) {
            handleChestItemInteraction(cimsg);
        } else if (m instanceof NewDestructibleDecorationMessage nmsg) {
            addNewDestructibleDecoration(nmsg);
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
        ClientGameAppState.getInstance().getGrid().insert(p);
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
        ClientGameAppState.getInstance().getGrid().insert(p);
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

    private Chest getChestById(int id) {
        return ((Chest) clientApp.getMobs().get(id));
    }

    private Item getItemById(int id) {
        return ((Item) clientApp.getMobs().get(id));
    }

    private Destructible getDestructibleById(int id) {
        return ((Destructible) clientApp.getMobs().get(id));
    }

    private InteractiveEntity getEntityById(int id) {
        return clientApp.getMobs().get(id);
    }

    private void addNewChest(NewChestMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(() -> {
                Chest c = Chest.createRandomChestClient(nmsg.getId(), clientApp.getDestructibleNode(), nmsg.getPos(), clientApp.getAssetManager());
                clientApp.getMobs().put(c.getId(), c);
                c.setHealth(nmsg.getHealth());
                ClientGameAppState.getInstance().getGrid().insert(c);
            });
        }
    }

    private void addNewItem(NewItemMessage imsg) {
        if (imsg instanceof NewHelmetMessage nhmsg) {
            Helmet i = (Helmet) ifa.createItem(nhmsg.getId(), nhmsg.getTemplate(), nhmsg.isDroppable());
            i.setArmorValue(nhmsg.getArmorValue());
            clientApp.registerEntity(i);

        } else if (imsg instanceof NewVestMessage nvmsg) {
            Vest i = (Vest) ifa.createItem(nvmsg.getId(), nvmsg.getTemplate(), nvmsg.isDroppable());
            i.setArmorValue(nvmsg.getArmorValue());
            clientApp.registerEntity(i);

        } else if (imsg instanceof NewGlovesMessage ngmsg) {
            Gloves i = (Gloves) ifa.createItem(ngmsg.getId(), ngmsg.getTemplate(), ngmsg.isDroppable());
            i.setArmorValue(ngmsg.getArmorValue());
            clientApp.registerEntity(i);

        } else if (imsg instanceof NewBootsMessage nbmsg) {
            Boots i = (Boots) ifa.createItem(nbmsg.getId(), nbmsg.getTemplate(), nbmsg.isDroppable());
            i.setArmorValue(nbmsg.getArmorValue());
            clientApp.registerEntity(i);
        } else if (imsg instanceof NewRangedWeaponMessage rmsg) {
            RangedWeapon i = (RangedWeapon) ifa.createItem(rmsg.getId(), rmsg.getTemplate(), rmsg.isDroppable());
            i.setAmmo(rmsg.getAmmo());
            clientApp.registerEntity(i);
        } else if (imsg instanceof NewMeleeWeaponMessage rmsg) {
            Item i = (Item) ifa.createItem(rmsg.getId(), rmsg.getTemplate(), rmsg.isDroppable());
            clientApp.registerEntity(i);
        } else if (imsg instanceof NewGrenadeMessage rmsg) {
            Item i = ifa.createItem(rmsg.getId(), rmsg.getTemplate(), rmsg.isDroppable());
            clientApp.registerEntity(i);
        } else if (imsg instanceof NewAmmoPackMessage rmsg) {
            AmmoPack i = (AmmoPack) ifa.createItem(rmsg.getId(), rmsg.getTemplate(), rmsg.isDroppable());
            i.setAmmo(rmsg.getAmmo());
            clientApp.registerEntity(i);
        }

    }

    private void handleChestItemInteraction(ChestItemInteractionMessage cimsg) {
        enqueueExecution(() -> {
            if (null != cimsg.getInteractionType()) {
                switch (cimsg.getInteractionType()) {
                    case INSERT:
                        Item inserted = getItemById(cimsg.getItemId());
                        getChestById(cimsg.getChestId()).addToEquipment(inserted);
                        System.out.println(" inserted ");
                        break;
                    case TAKE_OUT:
                        Item takenOut = getItemById(cimsg.getItemId());
                        getChestById(cimsg.getChestId()).removeFromEquipment(takenOut);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void handleMobItemInteraction(MobItemInteractionMessage iimsg) {
        enqueueExecution(() -> {
            if (null != iimsg.getInteractionType()) {
                switch (iimsg.getInteractionType()) {
                    case EQUIP:
                        Item equipped = getItemById(iimsg.getItemId());
                        if (equipped == null) {
                            throw new NullPointerException("THE item with ID = " + iimsg.getItemId() + " doesnt exist!");
                        }
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
            if (mobExistsLocally(hmsg.getTargetId())) {

                Destructible d = getDestructibleById(hmsg.getTargetId());
                d.receiveDamage(hmsg.getDamage());
                if (d.getHealth() <= 0) {
                    clientApp.getMobs().remove(d.getId());
                }
            }
        }
        );

    }

    private void handleHitscanTrail(HitscanTrailMessage tmsg) {
        enqueueExecution(() -> {
            Mob mob = getMobById(tmsg.getId());
            Rifle.createBullet(mob.getNode().getWorldTranslation().clone().add(0, 1, 0), tmsg.getShotPos());
        });
    }

    private void setHumanMobDefaultItem(SetDefaultItemMessage dmsg) {
        enqueueExecution(() -> {
            HumanMob human = (HumanMob) getMobById(dmsg.getMobId());
            System.out.println("human " + human.getId() + " registering item...");
            Item item = getItemById(dmsg.getItemId());
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

    private void correctPosition(InstantEntityPosCorrectionMessage cmsg) {
        enqueueExecution(() -> {
            getEntityById(cmsg.getId()).setPosition(cmsg.getPos());
        });
    }

    private void addNewDestructibleDecoration(NewDestructibleDecorationMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(() -> {
                DestructibleDecoration d = DestructibleDecorationFactory.createDecoration(nmsg.getId(), clientApp.getDestructibleNode(), nmsg.getPos(), nmsg.getTemplate(), clientApp.getAssetManager());
                clientApp.getMobs().put(d.getId(), d);
                ClientGameAppState.getInstance().getGrid().insert(d);
            });
        }
    }

    private void addNewThrownGrenade(GrenadeThrownMessage gmsg) {
        enqueueExecution(() -> {
            Node model = (Node) clientApp.getAssetManager().loadModel(ItemTemplates.SMOKE_GRENADE.getDropPath());
            clientApp.getDebugNode().attachChild(model);
            model.setLocalTranslation(gmsg.getPos());
            model.scale(1f);

            Geometry ge = (Geometry) model.getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(clientApp.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);
            var rotControl = new ClientThrownGrenadeRotateControl();
            ge.addControl(rotControl);

            ThrownGrenade g = new ThrownSmokeGrenade(gmsg.getId(), "Thrown Smoke", model);
            clientApp.getMobs().put(g.getId(), g);
        });
    }

    private void updateGrenadePosition(GrenadePosUpdateMessage gmsg) {
        if (getEntityById(gmsg.getId()) != null) {
            ((ThrownGrenade) getEntityById(gmsg.getId())).setServerLocation(gmsg.getPos());
        }

    }

    private void handleGrenadeExplosion(ThrownGrenadeExplodedMessage gemsg) {
        enqueueExecution(() -> {
            ThrownGrenade g = (ThrownGrenade) getEntityById(gemsg.getId());
            g.getNode().setLocalTranslation(gemsg.getPos());
            g.explodeClient();
            clientApp.getMobs().remove(gemsg.getId());
            g.getNode().removeFromParent();

        });
    }
}
