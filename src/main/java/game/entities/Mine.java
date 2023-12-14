/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import client.ClientGameAppState;
import com.epagagames.particles.Emitter;
import com.epagagames.particles.emittershapes.EmitterCircle;
import com.epagagames.particles.influencers.ColorInfluencer;
import com.epagagames.particles.influencers.SizeInfluencer;
import com.epagagames.particles.influencers.VelocityInfluencer;
import com.epagagames.particles.valuetypes.ColorValueType;
import com.epagagames.particles.valuetypes.Gradient;
import com.epagagames.particles.valuetypes.ValueType;
import com.epagagames.particles.valuetypes.VectorValueType;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import game.effects.TimedSpatialRemoveControl;
import game.entities.mobs.Mob;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.RectangleAABB;
import game.map.collision.RectangleOBB;
import game.map.collision.WorldGrid;
import messages.DestructibleDamageReceiveMessage;
import messages.messageListeners.ServerMessageListener;
import server.ServerMain;
import statusEffects.DamageOverTimeEffect;
import statusEffects.EffectFactory;
import statusEffects.EffectProcType;
import statusEffects.TimedSlowEffect;
import static game.effects.DecalProjector.projectFromTo;

/**
 *
 * @author 48793
 */
public class Mine extends DestructibleDecoration {

    private float explosionSize = 3;
    private float damage = 25f;

    public Mine(int id, String name, Node node, DecorationTemplates.DecorationTemplate template) {
        super(id, name, node, template);
    }

    @Override
    protected void createHitbox() {
        float hitboxWidth = template.getCollisionShapeWidth();
        float hitboxHeight = template.getCollisionShapeHeight();
        float hitboxLength = template.getCollisionShapeLength();
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleOBB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength, 0);
        showHitboxIndicator();
    }

    @Override
    protected void showHitboxIndicator() {
        hitboxDebug = CollisionDebugUtils.createHitboxGeometry(collisionShape.getWidth(), collisionShape.getHeight(), collisionShape.getLength(), ColorRGBA.Red);
        hitboxDebug.setName("" + id);
        hitboxNode.attachChild(hitboxDebug);

//        Geometry explosionDebug = CollisionDebugUtils.createHitboxGeometry(3, 3, 3, ColorRGBA.Magenta);
//        explosionDebug.setName("" + id);
//        hitboxNode.attachChild(explosionDebug);
    }

    @Override
    public void die() {
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public void onDeathServer() {
        explode();
    }

    @Override
    public float calculateDamage(float damage) {
        return damage * 2;
    }

    @Override
    public void receiveDamage(float rawDamage) {
        health = health - calculateDamage(rawDamage);

        if (health <= 0) {
            spawnExplosionVisuals();
            die();
        }
    }

    @Override
    public void onCollisionClient(Collidable other) {
//        spawnExplosionVisuals();
        if (other instanceof Mob m) {
            float slowStrength = 90; //in %
            m.addEffect(new TimedSlowEffect("Mine slow", EffectProcType.PERIODICAL, 20, m, slowStrength));
        }

    }

    @Override
    public void onCollisionServer(Collidable other) {
        selfDestruct();
        explode();
    }

    private void explode() {

        ServerMain serverApp = ServerMain.getInstance();

        RectangleAABB explosionHitbox = new RectangleAABB(node.getWorldTranslation(), explosionSize, explosionSize, explosionSize);
        for (Collidable c : serverApp.getGrid().getNearbyCollisionShapeAtPos(explosionHitbox.getPosition(), explosionHitbox)) {
            if (c instanceof Destructible de && c.getCollisionShape().wouldCollideAtPosition(explosionHitbox, c.getCollisionShape().getPosition())) {
                if (c != this) {
                    var emsg = new DestructibleDamageReceiveMessage(de.getId(), damage);
                    emsg.handleDestructibleDamageReceive(de, serverApp);
                }

            }
        }
    }

    private void selfDestruct() {
        ServerMain serverApp = ServerMain.getInstance();
        Destructible d = this;
        float selfDestructDmg = 5000;
        d.setHealth(d.getHealth() - d.calculateDamage(selfDestructDmg));

        var hmsg = new DestructibleDamageReceiveMessage(this.getId(), selfDestructDmg);
        hmsg.setReliable(true);
        serverApp.getServer().broadcast(hmsg);

        if (d.getHealth() <= 0) {
            WorldGrid grid = serverApp.getGrid();
            grid.remove(d);
            serverApp.getMobs().remove(d.getId());
        }
    }

    private void spawnExplosionVisuals() {
        var cs = ClientGameAppState.getInstance().getDebugNode();
        Emitter smoke = createSmoke();
        ParticleEmitter explosion = createExplosion();
        cs.attachChild(smoke);
        cs.attachChild(explosion);
        smoke.setParticlesPerEmission(100);
        smoke.emitAllParticles();
        smoke.setParticlesPerEmission(0);
        explosion.emitAllParticles();

        projectFromTo(ClientGameAppState.getInstance(), node.getWorldTranslation().clone().add(0, 1, 0), new Vector3f(0, -1, 0), "Textures/Gameplay/Decals/mineCrater.png", 5);

        explosion.addControl(new TimedSpatialRemoveControl(1.25f));
        smoke.addControl(new TimedSpatialRemoveControl(1.25f));

    }

    private Emitter createSmoke() {
        ColorInfluencer ci = new ColorInfluencer();
        ci.setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(ColorRGBA.Black.clone().setAlpha(0), 0.0f)
                        .addGradPoint(ColorRGBA.Black.clone().setAlpha(1), 0.25f)
                        .addGradPoint(ColorRGBA.Black.clone().setAlpha(0.8f), 0.65f)
                        .addGradPoint(ColorRGBA.Black.clone().setAlpha(0), 1f)
        ));
        var assetManager = ClientGameAppState.getInstance().getAssetManager();
        Material smokeMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        smokeMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        Texture tex = assetManager.loadTexture("Textures/Gameplay/Effects/Mine/smoke.png");
        smokeMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        smokeMat.setTexture("Texture", tex);

        VelocityInfluencer vi = new VelocityInfluencer();

        var vvt = new VectorValueType();
        vvt.setValue(new Vector3f(0.4f, 0, 0.4f));
        vi.setLinear(vvt);

        SizeInfluencer si = new SizeInfluencer();

        Emitter smokeEmitter = new Emitter("test", smokeMat, 30, si, vi, ci);
        smokeEmitter.setStartSpeed(new ValueType(16.5f));
        smokeEmitter.setStartColor(new ColorValueType(ColorRGBA.Black.clone().setAlpha(0.5f)));
        smokeEmitter.setLifeMin(new ValueType(0.5f));
        smokeEmitter.setLifeMax(new ValueType(0.5f));

        smokeEmitter.setParticlesPerEmission(0);
        EmitterCircle shape = new EmitterCircle();
        shape.setRadius(0.5f);
        shape.setRadiusThickness(0.2f);
        smokeEmitter.setShape(shape);

        smokeEmitter.setLooping(false);
        smokeEmitter.setEmissionsPerSecond(0);
        smokeEmitter.setEnabled(true);
        smokeEmitter.setLocalTranslation(node.getWorldTranslation());

        smokeEmitter.move(0, 0.5f, 0);
        return smokeEmitter;
    }

    private ParticleEmitter createExplosion() {
        var assetManager = ClientGameAppState.getInstance().getAssetManager();

        ParticleEmitter explosionEmitter
                = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 1);
        Material explosionMat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        explosionMat.setTexture("Texture", assetManager.loadTexture(
                "Textures/Gameplay/Effects/Mine/flame.png"));
        explosionMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        explosionEmitter.setMaterial(explosionMat);
        explosionEmitter.setSelectRandomImage(false);
        explosionEmitter.setImagesX(4);
        explosionEmitter.setImagesY(4); // 2x2 texture animation
        explosionEmitter.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
        explosionEmitter.setLowLife(1f);
        explosionEmitter.setHighLife(1f);
        explosionEmitter.setStartSize(4);
        explosionEmitter.setEndSize(4);

        explosionEmitter.setStartColor(new ColorRGBA(1f, 1f, 0f, 1f)); // yellow
//        explosionEmitter.emitAllParticles();
        explosionEmitter.setParticlesPerSec(0);
        explosionEmitter.setLocalTranslation(node.getWorldTranslation());
        explosionEmitter.move(0, 3.6f, 0);

        return explosionEmitter;
    }

}
