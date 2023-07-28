/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectiles.controls;

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
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class BulletTracerControl extends AbstractControl implements Savable, Cloneable {

    private static final int MOVEMENT_STEPS = 100;

    private float distanceToTravel;
    private boolean reachedDestination;
    private final Node bulletNode;
    private final Vector3f destination;
    private final float speed;
    private final GradientParticleEmitter p;

    public BulletTracerControl(Node node, Vector3f destination, float speed, GradientParticleEmitter p) {
        this.p = p;
        this.bulletNode = node;
        this.destination = destination;
        this.speed = speed;
        bulletNode.lookAt(destination, Vector3f.UNIT_Y);
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
        /* Example:
        if (spatial != null){
            // initialize
        }else{
            // cleanup
        }
         */
    }

    /**
     * Implement your spatial's behaviour here. From here you can modify the
     * scene graph and the spatial (transform them, get and set userdata, etc).
     * This loop controls the spatial while the Control is enabled.
     */
    @Override
    protected void controlUpdate(float tpf) {
        distanceToTravel = bulletNode.getWorldTranslation().distance(destination);
        if (distanceToTravel > tpf * speed) {
            for (int i = 0; i < MOVEMENT_STEPS; i += 2) {
                p.emitParticles(1);
                bulletNode.move(bulletNode.getLocalRotation().getRotationColumn(2).mult(tpf * speed / MOVEMENT_STEPS));
            }
        }  else if(!reachedDestination){
            reachedDestination = true;
            for (int i = 0; i < distanceToTravel / (tpf * speed / MOVEMENT_STEPS); i++) {
                p.emitParticles(1);
                bulletNode.move(bulletNode.getLocalRotation().getRotationColumn(2).mult(tpf * speed / MOVEMENT_STEPS));
            }
        } else if (p.getNumVisibleParticles() == 0) {
            bulletNode.removeFromParent();
            bulletNode.removeControl(this);
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        final BulletTracerControl control = new BulletTracerControl(bulletNode, destination.clone(), speed, p);
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

    private float getDistance2D(Vector3f a, Vector3f b) {

        return (float) Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getZ() - b.getZ()) * (a.getZ() - b.getZ()));

    }

}
