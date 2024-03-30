/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.serializing.Serializable;
import game.entities.Collidable;
import game.entities.mobs.Player;
import game.map.collision.MovementCollisionUtils;
import game.map.collision.WorldGrid;
import java.util.ArrayList;
import server.ServerMain;

/**
 *
 * @author 48793
 */
@Serializable
public class AnimationPlayedMessage extends EntityUpdateMessage {

    public AnimationPlayedMessage() {
    }

    public AnimationPlayedMessage(int id) {
        super(id);
    }



    @Override
    public void handleServer(ServerMain server) {
   
//                InstantEntityPosCorrectionMessage corrMsg = new InstantEntityPosCorrectionMessage(p, p.getNode().getWorldTranslation());
//                corrMsg.setReliable(true);
//                server.getServer().broadcast(Filters.in(getHostedConnectionByPlayer(p)), corrMsg);

    }

    @Override
    public void handleClient(ClientGameAppState client) {
        throw new UnsupportedOperationException("client got back a request.");
    }

}
