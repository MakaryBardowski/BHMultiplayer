/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.effects;

import game.entities.mobs.Player;
import client.ClientGameAppState;
import client.Main;
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
    private static ParticleEmitter temp;
    private static final byte BLOOD_POOL_SIZE = 6;

    static {
        for (int i = 0; i < BLOOD_POOL_SIZE; i++) {
            bloodEmitters.offer(setupBloodEmitter());
        }
    }


    public static ParticleEmitter getBlood() {
        temp = bloodEmitters.poll();
        bloodEmitters.offer(temp);
        return temp;

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
}
