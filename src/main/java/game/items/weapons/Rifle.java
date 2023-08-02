/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import game.effects.GradientParticleEmitter;
import game.effects.GradientParticleMesh;
import game.items.ItemTemplates.ItemTemplate;
import game.mobs.Mob;
import game.mobs.Player;
import projectiles.controls.BulletTracerControl;
import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import game.mobs.InteractiveEntity;

/**
 *
 * @author tomasz potoczko
 */
public class Rifle extends RangedWeapon {

    private Node muzzleNode;
    private static final float BULLET_SPEED = 1200f;
    private CameraRecoilControl camRecoil;
    private RecoilControl gunRecoil;

    public Rifle(float damage, ItemTemplate template) {
        super(damage, template);
    }

    public Rifle(float damage, ItemTemplate template, boolean droppable) {
        super(damage, template, droppable);
    }

    @Override
    public void playerEquip(Player p) {
        playerHoldRight(p);
    }

    @Override
    public void playerUnequip(Player m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack(Mob m) {

    }

    @Override
    public void playerHoldRight(Player p) {
        p.setEquippedRightHand(this);

        AssetManager assetManager = Main.getInstance().getAssetManager();
        Node model = (Node) assetManager.loadModel(template.getFpPath());

        model.move(-.57f, -.65f, 2.3f);
        model.setLocalRotation((new Quaternion()).fromAngleAxis(FastMath.PI / 48, new Vector3f(-.15f, .5f, 0)));

//        model.move(-.48f, -.52f, 1.8f);
//        model.setLocalRotation((new Quaternion()).fromAngleAxis(FastMath.PI / 32, new Vector3f(-.15f, .5f, 0)));
        /// i don't know why the setupModelLight() method doesn't work <<-- big congo
        Geometry ge = (Geometry) ((Node) model.getChild(0)).getChild(0);
        Material originalMaterial = ge.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        ge.setMaterial(newMaterial);
        ///

        SkinningControl skinningControl = model.getChild(0).getControl(SkinningControl.class);
        muzzleNode = skinningControl.getAttachmentsNode("muzzleAttachmentBone");

        gunRecoil = new RecoilControl(0.2f, -.0f, .0f, .00f);
        camRecoil = new CameraRecoilControl(2f, -.3f, .3f, .1f);
        p.getGunNode().addControl(gunRecoil);
        p.getMainCameraNode().addControl(camRecoil);

        p.getGunNode().detachAllChildren();
        p.getGunNode().attachChild(model);
        p.getFirstPersonCameraNode().attachChild(p.getGunNode());
        model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));
    }

    @Override
    public void playerUseRight(Player p) {
        playerAttack(p);
    }

    @Override
    public void playerAttack(Player p) {
        if (!hitscan(p, ClientGameAppState.getInstance().getDestructibleNode(), false)) {
            hitscan(p, ClientGameAppState.getInstance().getMapNode(), true);
        }

    }

    private boolean hitscan(Player p, Node collsionNode, boolean wallCheck) {
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        collsionNode.collideWith(ray, results);

        Vector3f cp = null;
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            cp = closest.getContactPoint();
            if (!wallCheck) {
                Integer hitId = Integer.valueOf(closest.getGeometry().getName());
                InteractiveEntity mobHit = ClientGameAppState.getInstance().getMobs().get(hitId);
                mobHit.onShot(p,damage);
            }
            createBullet(cp);
            recoilFire();
            return true;
        }
        return false;
    }

    private void recoilFire() {
        camRecoil.recoilFire();
        gunRecoil.recoilFire();

    }

    private void createBullet(Vector3f destination) {
        Node bullet = new Node("boolet");
        ClientGameAppState.getInstance().getDebugNode().attachChild(bullet);
        bullet.move(muzzleNode.getWorldTranslation());
        GradientParticleEmitter trail = createTrail();
        bullet.attachChild(trail);
        bullet.addControl(new BulletTracerControl(bullet, destination, BULLET_SPEED, trail));

    }

    private GradientParticleEmitter createTrail() {
        Material trailMat = createTrailMaterial();
        GradientParticleEmitter fire = new GradientParticleEmitter("Debris", GradientParticleMesh.Type.Triangle, 400);
        fire.setMaterial(trailMat);
        fire.setLowLife(0.7f);
        fire.setHighLife(0.7f);
        fire.setStartSize(0.02f);
        fire.setEndSize(0.00f);
        fire.setRotateSpeed(0);
        fire.setParticlesPerSec(30);
        fire.setSelectRandomImage(true);
        fire.setVelocityVariation(1);
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -0.4f, 0));
        fire.setGravity(0, -0.6f, 0);
        fire.setStartColor(new ColorRGBA(252f / 255f, 115f / 255f, 3f / 255f, 1));
        float gray = 16f / 255f;
        fire.setEndColor(new ColorRGBA(gray, gray, gray, 1));
        fire.getParticleInfluencer().setVelocityVariation(0.4f);
        fire.setParticlesPerSec(0);
        return fire;
    }

    private Material createTrailMaterial() {
        Material mat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
        Texture tex1 = Main.getInstance().getAssetManager().loadTexture("Effects/Particles/part_beam.png");
        mat.setTexture("Texture", tex1);
        return mat;
    }

}
