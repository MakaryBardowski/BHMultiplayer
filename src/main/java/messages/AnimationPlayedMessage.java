/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messages;

import client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Animated;
import game.entities.Animation;
import game.entities.Collidable;
import game.entities.mobs.player.Player;
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

    private int animationOrdinal = -1;

    public AnimationPlayedMessage() {}

    public AnimationPlayedMessage(int id, Animation animation) {
        super(id);
        animationOrdinal = animation.ordinal();
    }
    
    public Animation getAnimation(){
        return Animation.values()[animationOrdinal];
    }


    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        server.getServer().broadcast(Filters.notEqualTo(hc),this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = getEntityByIdClient(id);
        
        if(entity == null){
        return;
        }
        
        if(entity instanceof Animated animatedEntity){
            animatedEntity.playAnimation(getAnimation());
        }
    }

}
