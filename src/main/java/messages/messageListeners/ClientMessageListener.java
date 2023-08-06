/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages.messageListeners;

import game.cameraAndInput.InputController;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import messages.DestructibleHealthUpdateMessage;
import messages.MobPosUpdateMessage;
import messages.MobRotUpdateMessage;
import messages.MobsInGameMessage;
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
import java.util.concurrent.Callable;
import messages.ChestsInGameMessage;

/**
 *
 * @author 48793
 */
public class ClientMessageListener implements MessageListener<Client> {

    private final ClientGameAppState clientApp;

    public ClientMessageListener(ClientGameAppState c) {
        this.clientApp = c;
    }

    @Override
    public void messageReceived(Client s, Message m) {
        if (m instanceof MobRotUpdateMessage nmsg) {
            updateMobRotation(nmsg);
        } else if (m instanceof MobPosUpdateMessage nmsg) {
            updateMobPosition(nmsg);
        } else if (m instanceof DestructibleHealthUpdateMessage hmsg) {
            updateEntityHealth(hmsg);
        } else if (m instanceof MobsInGameMessage nmsg) {
            addMob(nmsg);
        } else if (m instanceof ChestsInGameMessage nmsg) {
            addNewChest(nmsg);
        } else if (m instanceof PlayerJoinedMessage nmsg) {
            addNewPlayer(nmsg);
        } else if (m instanceof SetPlayerMessage nmsg) {
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

    private void updateEntityHealth(DestructibleHealthUpdateMessage hmsg) {
        if (mobExistsLocally(hmsg.getId())) {
            getDestructible(hmsg.getId()).setHealth(hmsg.getHealth());
        }
    }

    private void updateMobPosition(MobPosUpdateMessage nmsg) {
        if (mobExistsLocally(nmsg.getId())) {
            getMob(nmsg.getId()).setServerLocation(nmsg.getPos());
        }
    }

    private void updateMobRotation(MobRotUpdateMessage nmsg) {
        if (mobExistsLocally(nmsg.getId())) {
            getMob(nmsg.getId()).setServerRotation(nmsg.getRot());
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

    private void addMob(MobsInGameMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(() -> {
                Mob p = clientApp.registerPlayer(nmsg.getId(), false);
                placeMob(nmsg.getPos(), p);
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

    private Mob getMob(int id) {
        return ((Mob) clientApp.getMobs().get(id));
    }

    private Destructible getDestructible(int id) {
        return ((Destructible) clientApp.getMobs().get(id));
    }

    private void addNewChest(ChestsInGameMessage nmsg) {
        if (mobDoesNotExistLocally(nmsg.getId())) {
            enqueueExecution(() -> {
                Chest c = Chest.createRandomChestClient(nmsg.getId(), clientApp.getDestructibleNode(), nmsg.getPos(), clientApp.getAssetManager());
                clientApp.getMobs().put(c.getId(), c);
            });
        }
    }

}
