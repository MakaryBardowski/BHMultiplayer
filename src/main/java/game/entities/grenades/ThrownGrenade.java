package game.entities.grenades;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.Collidable;
import game.entities.mobs.Mob;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
public abstract class ThrownGrenade extends Collidable {

    @Getter
    protected Vector3f serverLocation; // updated by the server
    
    @Getter
    protected Quaternion serverRotation;

    @Getter
    @Setter
    protected float posInterpolationValue;

    @Getter
    @Setter
    protected float rotInterpolationValue;
    
    @Getter
    protected float speed = 120;

    public ThrownGrenade(int id, String name, Node node) {
        super(id, name, node);
        this.serverLocation = node.getWorldTranslation();
        this.serverRotation = node.getLocalRotation();
    }

    public abstract void explodeClient();
    public abstract void explodeServer();

    @Override
    public void onCollisionClient(Collidable other) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onCollisionServer(Collidable other) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setPosition(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    public void setServerLocation(Vector3f serverLocation) {
        this.serverLocation = serverLocation;
        this.posInterpolationValue = 0;
    }

    public void setServerRotation(Quaternion serverRotation) {
        this.serverRotation = serverRotation;
        this.rotInterpolationValue = 0;
    }


}
