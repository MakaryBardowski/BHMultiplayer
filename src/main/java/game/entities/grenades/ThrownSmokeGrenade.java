package game.entities.grenades;

import client.ClientGameAppState;
import static client.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.epagagames.particles.Emitter;
import com.epagagames.particles.emittershapes.EmitterCircle;
import com.epagagames.particles.influencers.ColorInfluencer;
import com.epagagames.particles.influencers.GravityInfluencer;
import com.epagagames.particles.influencers.SizeInfluencer;
import com.epagagames.particles.influencers.VelocityInfluencer;
import com.epagagames.particles.valuetypes.ColorValueType;
import com.epagagames.particles.valuetypes.Gradient;
import com.epagagames.particles.valuetypes.ValueType;
import com.epagagames.particles.valuetypes.VectorValueType;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import game.effects.TimedSpatialRemoveControl;
import game.entities.Collidable;
import game.entities.Movable;
import game.entities.mobs.Mob;
import messages.ThrownGrenadeExplodedMessage;
import static messages.messageListeners.ServerMessageListener.enqueueExecutionServer;
import server.ServerMain;
import static server.ServerMain.removeEntityByIdServer;

/**
 *
 * @author 48793
 */
public class ThrownSmokeGrenade extends ThrownGrenade {

    private static final Material mat;
    private static final ColorInfluencer ci;
    private static final BlendMode BLEND_MODE = BlendMode.Alpha;
    private static final byte NUM_SMOKE_PARTICLES = 30;
    
    private static final float smokeLifetime = 15f;
    private static final float smokeSpeed = 0.265f;
    private static final ColorRGBA smokeColor = ColorRGBA.DarkGray;
    private static final float sizeFactor = 1.5f;
    private static final boolean looping = false;
    
    static {
               var a = Main.getInstance().getAssetManager();
            mat = new Material(a, "Common/MatDefs/Misc/Particle.j3md");
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

            Texture tex = a.loadTexture("Textures/Gameplay/Effects/smoke_1.png");
            mat.getAdditionalRenderState().setBlendMode(BLEND_MODE);
            mat.setTexture("Texture", tex);

            ci = new ColorInfluencer();
            ci.setColorOverTime(new ColorValueType(
                    new Gradient()
                            .addGradPoint(smokeColor.clone().setAlpha(0), 0.0f)
                            .addGradPoint(smokeColor.clone().setAlpha(1), 0.015f)
                            .addGradPoint(smokeColor.clone().setAlpha(0.8f), 0.65f)
                            .addGradPoint(smokeColor.clone().setAlpha(0), 1f)
            ));
    }

    public ThrownSmokeGrenade(int id, String name, Node node) {
        super(id, name, node);
    }

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
    public void setPositionServer(Vector3f newPos) { // grenades are managed by the server, so we never set position explicitly through this method
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void explodeClient() {

        var l = addLowerSmoke();
        var u = addUpperSmoke();

        l.addControl(new TimedSpatialRemoveControl(smokeLifetime + 2.5f));
        u.addControl(new TimedSpatialRemoveControl(smokeLifetime + 2.5f));
        removeEntityByIdClient(id);
    }

    @Override
    public void explodeServer() {
        ThrownGrenadeExplodedMessage gemsg = new ThrownGrenadeExplodedMessage(id, node.getWorldTranslation());
        gemsg.setReliable(true);
        ServerMain.getInstance().getServer().broadcast(gemsg);
        node.removeFromParent();

        removeEntityByIdServer(id);
    }

    public Emitter addLowerSmoke() {
        var cs = ClientGameAppState.getInstance();

        VelocityInfluencer vi = new VelocityInfluencer();

        var vvt = new VectorValueType();
        vvt.setValue(new Vector3f(smokeSpeed, 0.2f, smokeSpeed));
        vi.setLinear(vvt);
        SizeInfluencer si = new SizeInfluencer();
        si.setSizeOverTime(new ValueType(1.25f));

        Emitter emitter = new Emitter("test", mat, NUM_SMOKE_PARTICLES, si, vi, ci);
//        emitter.setStartColor(new ColorValueType(ColorRGBA.Black.clone().setAlpha(0.5f)));
        emitter.setLifeMin(new ValueType(smokeLifetime));
        emitter.setLifeMax(new ValueType(smokeLifetime));
        emitter.setStartSize(new ValueType(sizeFactor));

        emitter.setParticlesPerEmission(1);
        EmitterCircle shape = new EmitterCircle();
        shape.setRadius(1.6f * sizeFactor);
        shape.setRadiusThickness(1.6f * sizeFactor);
        emitter.setShape(shape);

        emitter.setLooping(looping);
        emitter.setEmissionsPerSecond(30);
        emitter.setEnabled(true);
        emitter.setQueueBucket(RenderQueue.Bucket.Transparent);

        cs.getDebugNode().attachChild(emitter);
        emitter.setLocalTranslation(node.getWorldTranslation().clone());
        emitter.move(0, .75f, 0);
        return emitter;
    }

    public Emitter addUpperSmoke() {
        var cs = ClientGameAppState.getInstance();

        VelocityInfluencer vi = new VelocityInfluencer();

        var vvt = new VectorValueType();
        vvt.setValue(new Vector3f(0, 0.2f, 0));
        vi.setLinear(vvt);

        GravityInfluencer gi = new GravityInfluencer();
        gi.setGravity(0, -2, 0);

        SizeInfluencer si = new SizeInfluencer();
        si.setSizeOverTime(new ValueType(1.25f));
        Emitter emitter = new Emitter("test", mat, NUM_SMOKE_PARTICLES, si, vi, ci, gi);
//        emitter.setStartColor(new ColorValueType(ColorRGBA.Black.clone().setAlpha(0.5f)));
        emitter.setLifeMin(new ValueType(smokeLifetime));
        emitter.setLifeMax(new ValueType(smokeLifetime));
        emitter.setStartSize(new ValueType(sizeFactor));
        emitter.setParticlesPerEmission(1);
        EmitterCircle shape = new EmitterCircle();
        shape.setRadius(1.5f * sizeFactor);
        shape.setRadiusThickness(1.5f * sizeFactor);
        emitter.setShape(shape);

        emitter.setLooping(looping);
        emitter.setEmissionsPerSecond(30);
        emitter.setEnabled(true);
        emitter.setQueueBucket(RenderQueue.Bucket.Transparent);

        cs.getDebugNode().attachChild(emitter);
        emitter.setLocalTranslation(node.getWorldTranslation().clone());
        emitter.move(0, 2f, 0);
        return emitter;
    }

    @Override
    public void move(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}
