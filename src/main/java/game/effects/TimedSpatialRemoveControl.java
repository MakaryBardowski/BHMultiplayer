package game.effects;

import com.epagagames.particles.Emitter;
import com.epagagames.particles.influencers.ParticleInfluencer;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author 48793
 */
public class TimedSpatialRemoveControl extends AbstractControl implements Savable, Cloneable {

    private float timeToLive;
    private float time = 0;

    public TimedSpatialRemoveControl(float timeToLive) {
        this.timeToLive = timeToLive;
    }

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
        time += tpf;
        if (time >= timeToLive) {
            if (spatial instanceof Emitter e) {
                e.setEnabled(false);
                var it = e.getInfluencerMap().iterator();
                while (it.hasNext()) {
                    it.next();
                    it.remove();
                }
//                System.out.println(e.getActiveParticleCount());
            }
            spatial.removeFromParent();
            spatial.removeControl(this);
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        final TimedSpatialRemoveControl control = new TimedSpatialRemoveControl(timeToLive);
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
