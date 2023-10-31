/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities.grenades;

import projectiles.controls.*;
import game.effects.GradientParticleEmitter;
import client.ClientGameAppState;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import static game.map.collision.MovementCollisionUtils.calculateNewPosInLogicMap;
import java.io.IOException;
import java.util.Random;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class ServerThrownGrenadeControl extends AbstractControl implements Savable, Cloneable {

    private static final float BOUNCE_ELASTICITY = .7f;
    private final ThrownGrenade grenade;
    private final Vector3f direction;
    private final float speed;
    private final float gravity = 0.3f;
    private Vector3f moveVec = new Vector3f();
    private boolean startedMoving = false;
    private byte bounces = 0;
    private final byte maxBounces = 5;

    public ServerThrownGrenadeControl(ThrownGrenade grenade, Vector3f direction, float speed) {
        this.grenade = grenade;
        this.direction = direction;
        this.speed = speed;
    } // empty serialization constructor

    /**
     * Optional custom constructor with arguments that can init custom fields.
     * Note: you cannot modify the spatial here yet!
     */
    /**
     * This method is called when the control is added to the spatial, and when
     * the control is removed from the spatial (setting a null value). It can be
     * used for both initialization and cleanup.
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            spatial.lookAt(direction.add(spatial.getWorldTranslation()), Vector3f.UNIT_Y);
        }
        /* Example:
        
        else{
            // cleanup
        }
         */
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!startedMoving) {
            moveVec = spatial.getLocalRotation().getRotationColumn(2).mult(speed * tpf);
            startedMoving = true;
        }

        Vector3f dragForce = moveVec.negate().normalize().mult(moveVec.lengthSquared() * 0.06f);
        moveVec.addLocal(dragForce);
        moveVec.subtractLocal(0, gravity * tpf, 0);

        var posInMapNow = calculateNewPosInLogicMap(spatial, new Vector3f(), ServerMain.getInstance().getBLOCK_SIZE());
        var posInMapAfterMove = calculateNewPosInLogicMap(spatial, moveVec, ServerMain.getInstance().getBLOCK_SIZE());

        if (ServerMain.getInstance().getMap()[(int) posInMapNow.getX()][(int) posInMapAfterMove.getY()][(int) posInMapNow.getZ()] != 0 && bounces <= maxBounces) {
            moveVec.setY(-moveVec.getY() * BOUNCE_ELASTICITY);
            bounces++;
        }

        if (bounces > maxBounces) {
            grenade.explodeServer();
            spatial.removeFromParent();
            spatial.removeControl(this);
        } else {
            spatial.move(moveVec);
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        final ServerThrownGrenadeControl control = new ServerThrownGrenadeControl(grenade, direction, speed);
        /* Optional: use setters to copy userdata into the cloned control */
        // control.setIndex(i); // example
        control.setSpatial(spatial);
        return control;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        /* Optional: rendering manipulation (for advanced users) */
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        // im.getCapsule(this).read(...);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        // ex.getCapsule(this).write(...);
    }

}
