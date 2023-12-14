/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.effects;

import game.entities.mobs.Player;
import client.ClientGameAppState;
import client.Main;
import com.epagagames.particles.Emitter;
import com.epagagames.particles.emittershapes.EmitterCircle;
import com.epagagames.particles.influencers.ColorInfluencer;
import com.epagagames.particles.influencers.SizeInfluencer;
import com.epagagames.particles.influencers.VelocityInfluencer;
import com.epagagames.particles.valuetypes.ColorValueType;
import com.epagagames.particles.valuetypes.Curve;
import com.epagagames.particles.valuetypes.Gradient;
import com.epagagames.particles.valuetypes.ValueType;
import com.epagagames.particles.valuetypes.VectorValueType;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author 48793
 */
public class EmitterPooler {

    private static final Queue<ParticleEmitter> bloodEmitters = new LinkedList<>();
    private static final Queue<Emitter> shotSmokeEmitters = new LinkedList<>();

    private static ParticleEmitter temp;
    private static Emitter temp1;
    private static final byte BLOOD_POOL_SIZE = 6;
    private static final byte SHOT_SMOKE_POOL_SIZE = 6;

    static {
        for (int i = 0; i < BLOOD_POOL_SIZE; i++) {
            bloodEmitters.offer(setupBloodEmitter());
        }
        for (int i = 0; i < SHOT_SMOKE_POOL_SIZE; i++) {
            shotSmokeEmitters.offer(setupBulletHitSmoke());
        }
    }

    public static ParticleEmitter getBlood() {
        temp = bloodEmitters.poll();
        bloodEmitters.offer(temp);
        return temp;
    }

    public static Emitter getShotSmoke() {
        temp1 = shotSmokeEmitters.poll();
        shotSmokeEmitters.offer(temp1);
        return temp1;
    }

    private static ParticleEmitter setupBloodEmitter() {
        ParticleEmitter blood
                = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 500);
        Material mat_red = new Material(Main.getInstance().getAssetManager(),
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", Main.getInstance().getAssetManager().loadTexture(
                "Textures/Gameplay/Decals/testBlood0.png"));
        mat_red.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        blood.setQueueBucket(RenderQueue.Bucket.Transparent);
        blood.setMaterial(mat_red);
        blood.setImagesX(1);
        blood.setImagesY(1); // 2x2 texture animation
        blood.setSelectRandomImage(true);
        blood.setRandomAngle(true);
        blood.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
        blood.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        blood.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1, 0));
        blood.setStartSize(1.2f); //1.2f
        blood.setEndSize(0.6f); //0.6f
        blood.setGravity(0, 9, 0);
        blood.setLowLife(2f);
        blood.setHighLife(3f);
        blood.getParticleInfluencer().setVelocityVariation(3f);
        ClientGameAppState.getInstance().getDebugNode().attachChild(blood);
        blood.move(0, 2, 0);
        blood.setParticlesPerSec(0);
        return blood;
    }

    private static Emitter setupBulletHitSmoke() {
        var smokeColor = ColorRGBA.DarkGray;
        Material mat_red = new Material(Main.getInstance().getAssetManager(),
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", Main.getInstance().getAssetManager().loadTexture(
                "Textures/Gameplay/Effects/smoke_1.png"));
        mat_red.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        var ci = new ColorInfluencer();
        ci.setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(smokeColor.clone().setAlpha(0), 0.0f)
                        .addGradPoint(smokeColor.clone().setAlpha(1), 0.015f)
                        .addGradPoint(smokeColor.clone().setAlpha(0.8f), 0.65f)
                        .addGradPoint(smokeColor.clone().setAlpha(0), 1f)
        ));

        var cs = ClientGameAppState.getInstance();

        VelocityInfluencer vi = new VelocityInfluencer();

        var vvt = new VectorValueType();
        vvt.setValue(new Vector3f(0, 0.2f, 0));
        vi.setLinear(vvt);
        SizeInfluencer si = new SizeInfluencer();
        
        si.setSizeOverTime(new ValueType(14));
        Emitter emitter = new Emitter("test", mat_red, 30, si, vi, ci);
//        emitter.setStartColor(new ColorValueType(ColorRGBA.Black.clone().setAlpha(0.5f)));
        emitter.setLifeMin(new ValueType(20));
        emitter.setLifeMax(new ValueType(20));
        emitter.setStartSize(new ValueType(1));

        emitter.setParticlesPerEmission(1);
        EmitterCircle shape = new EmitterCircle();
        shape.setRadius(1.6f);
        shape.setRadiusThickness(1.6f * 0);
        emitter.setShape(shape);

        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);
        emitter.setEnabled(true);
        emitter.setQueueBucket(RenderQueue.Bucket.Transparent);

        cs.getDebugNode().attachChild(emitter);
        emitter.move(0, .01f, 0);
        return emitter;
    }
}
