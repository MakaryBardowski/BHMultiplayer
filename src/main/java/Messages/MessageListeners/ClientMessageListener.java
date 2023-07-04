/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Messages.MessageListeners;

import Game.CameraAndInput.InputEditor;
import Game.Mobs.Mob;
import Game.Mobs.Player;
import Messages.MobHealthUpdateMessage;
import Messages.MobUpdateMessage;
import Messages.MobPosUpdateMessage;
import Messages.MobRotUpdateMessage;
import Messages.MobsInGameMessage;
import Messages.PlayerJoinedMessage;
import Messages.SetPlayerMessage;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.Networking.Client.ClientMain;
import com.Networking.Client.PlayerHUD;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author 48793
 */
/*
Klasa nas³uchuj¹ca wszystkich komunikatów wys³anych przez serwer do klienta
w klasie ClientMain (Main aplikacji klienta) jest 

 */
public class ClientMessageListener implements MessageListener<Client> {

    private ClientMain clientApp;

    public ClientMessageListener(ClientMain c) {
        this.clientApp = c;
    }

    /* metoda wykonuje sie przy otrzymaniu wiadomosci od serwera. Troche spaghetti code,
    ale prosty do przepisania a nie planujemy chyba miec 10000 typow wiadomosci 
     */
    @Override
    public void messageReceived(Client s, Message m) {
        if (m instanceof MobRotUpdateMessage nmsg) {
            if (clientApp.getMobs().get(nmsg.getId()) != null) {

                clientApp.getMobs().get(nmsg.getId()).setServerRotation(nmsg.getRot());
            }
        } else if (m instanceof MobPosUpdateMessage nmsg) {

            if (clientApp.getMobs().get(nmsg.getId()) != null) {
                clientApp.getMobs().get(nmsg.getId()).setServerLocation(nmsg.getPos());

            }
        } else if (m instanceof MobHealthUpdateMessage hmsg) {
            if (clientApp.getMobs().get(hmsg.getId()) != null) {
                clientApp.getMobs().get(hmsg.getId()).setHealth(hmsg.getHealth());

            }
        } else if (m instanceof MobsInGameMessage nmsg) {

            if (clientApp.getMobs().get(nmsg.getId()) == null) {
                Vector3f pos = new Vector3f(nmsg.getX(), nmsg.getY(), nmsg.getZ());

                /* jesli klient który odbierze
                wiadomoœæ nie ma moba o takim ID, to go dodaje
                 */
                clientApp.enqueue(
                        () -> {
                            /*
                            jesli klient, ktory odebral wiadomosc nie ma jeszcze przypisanego gracza
                            to wtedy
                             */
                            Mob p = clientApp.registerPlayer(nmsg.getId());
                            p.getNode().setLocalTranslation(pos);

                        }
                );
            }

        } else if (m instanceof PlayerJoinedMessage nmsg) {

            if (clientApp.getMobs().get(nmsg.getId()) == null) {
                Vector3f pos = new Vector3f(nmsg.getX(), nmsg.getY(), nmsg.getZ());

                /* jesli klient który odbierze
                wiadomoœæ nie ma moba o takim ID, to go dodaje
                 */
                clientApp.enqueue(
                        () -> {
                            /*
                            jesli klient, ktory odebral wiadomosc nie ma jeszcze przypisanego gracza
                            to wtedy
                             */
                            Player p = clientApp.registerPlayer(nmsg.getId());
                            p.getNode().setLocalTranslation(pos);

                        }
                );
            }

        } else if (m instanceof SetPlayerMessage nmsg) {

            clientApp.enqueue(() -> {
                Vector3f pos = new Vector3f(nmsg.getX(), nmsg.getY(), nmsg.getZ());
                Player p = clientApp.registerPlayer(nmsg.getId());
                clientApp.getPickableNode().attachChild(p.getNode());
                p.getNode().setLocalTranslation(pos);
                clientApp.setPlayer(p);
                new InputEditor().setupInput(clientApp);
                clientApp.getStateManager().attach(new PlayerHUD(clientApp));
                p.getNode().setCullHint(Spatial.CullHint.Always);
            });
        }

    }

}
