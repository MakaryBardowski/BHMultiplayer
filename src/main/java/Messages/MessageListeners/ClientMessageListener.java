/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Messages.MessageListeners;

import Game.CameraAndInput.InputEditor;
import Game.Mobs.Player;
import Messages.MobUpdateMessage;
import Messages.MobUpdatePosRotMessage;
import Messages.PlayerJoined;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.Networking.Client.ClientMain;
import com.jme3.math.Vector3f;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author 48793
 */
/*
Klasa nas�uchuj�ca wszystkich komunikat�w wys�anych przez serwer do klienta
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

        if (m instanceof PlayerJoined nmsg) {
            Vector3f pos = new Vector3f(nmsg.getX(), nmsg.getY(), nmsg.getZ());

            if (clientApp.getMobs().get(nmsg.getId()) == null) {
                /* jesli klient kt�ry odbierze
                wiadomo�� nie ma moba o takim ID, to go dodaje
                 */
                clientApp.enqueue(
                        () -> {
                            /*
                            jesli klient, ktory odebral wiadomosc nie ma jeszcze przypisanego gracza
                            to wtedy
                             */
                            Player p = clientApp.registerPlayer(nmsg.getId());
                            p.getNode().setLocalTranslation(pos);
                            if (clientApp.getPlayer() == null) {
                                clientApp.setPlayer(p);
                                new InputEditor().setupInput(clientApp);
                            }
                        }
                );
            }

        } else if (m instanceof MobUpdatePosRotMessage nmsg) {

            if (clientApp.getMobs().get(nmsg.getId()) != null) {
                Vector3f pos = new Vector3f(nmsg.getX(), nmsg.getY(), nmsg.getZ());
                clientApp.getMobs().get(nmsg.getId()).setServerLocation(pos);
//                clientApp.enqueue(() -> {clientApp.getMobs().get(nmsg.getId()).getNode().setLocalTranslation(pos);               
//                });
            }
        }

    }

}
