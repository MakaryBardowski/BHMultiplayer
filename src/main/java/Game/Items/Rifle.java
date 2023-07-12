/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Items.ItemTemplates.ItemTemplate;
import Game.Mobs.Mob;
import Game.Mobs.Player;
import Projectiles.Controls.BulletTracerControl;
import com.Networking.Client.ClientGameAppState;
import com.Networking.Client.Main;
import com.epagagames.particles.Emitter;
import com.epagagames.particles.emittershapes.EmitterCircle;
import com.epagagames.particles.emittershapes.EmitterCone;
import com.epagagames.particles.influencers.ColorInfluencer;
import com.epagagames.particles.influencers.GravityInfluencer;
import com.epagagames.particles.influencers.PreferredDirectionInfluencer;
import com.epagagames.particles.influencers.SizeInfluencer;
import com.epagagames.particles.influencers.TrailInfluencer;
import com.epagagames.particles.valuetypes.ValueType;
import com.jme3.anim.SkinningControl;
import com.jme3.animation.SkeletonControl;
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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.util.Random;
import jme3utilities.mesh.PointMesh;

/**
 *
 * @author tomasz potoczko
 */
public class Rifle extends RangedWeapon {

    private Node muzzleNode;

    public Rifle(ItemTemplate template) {
        super(template);
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

         model.move(-.63f, -.65f, 2.3f);
        model.setLocalRotation((new Quaternion()).fromAngleAxis(FastMath.PI / 32, new Vector3f(-.15f, .5f, 0)));
        
        
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

        
//                Box box1 = new Box(0.5f, 0.5f, 0.5f);
//        Geometry blue = new Geometry("Box", box1);
//        blue.setMaterial(newMaterial);
//        Node bullet = new Node();
//        bullet.attachChild(blue);
//        muzzleNode.attachChild(bullet);
//  
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
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        ClientGameAppState.getInstance().getMapNode().collideWith(ray, results);

        Vector3f cp = null;
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            cp = closest.getContactPoint();

        }

        Material m = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        Texture tex = Main.getInstance().getAssetManager().loadTexture("Textures/Gameplay/Decals/testBlood0.png");
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        m.setTexture("ColorMap", tex);
        TrailInfluencer ti = new TrailInfluencer();
        Material mat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        Texture tex1 = Main.getInstance().getAssetManager().loadTexture("Effects/Particles/part_beam.png");
        mat.setTexture("Texture", tex1);
        ti.setTrailmat(mat);

        GravityInfluencer gi = new GravityInfluencer();
        gi.setGravity(0, -9, 0);

        Emitter emitter = new Emitter("test", m, 100);
        emitter.setStartSpeed(new ValueType(9.5f));
        emitter.setLifeFixedDuration(2.0f);
        emitter.setEmissionsPerSecond(20);
        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCircle());

//        emitter.setShape(new EmitterCone());
//        ((EmitterCone) emitter.getShape()).setRadius(0.005f);
        emitter.addInfluencer(ti);
        emitter.addInfluencer(gi);

        ClientGameAppState.getInstance().getDebugNode().attachChild(emitter);
        emitter.setLocalTranslation(muzzleNode.getWorldTranslation());
//emitter.setLocalTranslation(new Vector3f ( 0 , 1, 0));
//        System.out.println("emitter "+emitter.getWorldTranslation());
        Box box1 = new Box(0.02f, 0.02f, 0.02f);
        Geometry blue = new Geometry("Box", box1);
        blue.setMaterial(m);
        Node bullet = new Node();
        bullet.attachChild(blue);
        ClientGameAppState.getInstance().getDebugNode().attachChild(bullet);
        
        bullet.move(muzzleNode.getWorldTranslation());
        System.out.println("muzzle pos--- "+muzzleNode.getWorldTranslation());
        System.out.println("player pos "+p.getNode().getWorldTranslation());
        System.out.println("muzzle rot--- "+muzzleNode.getWorldRotation());
        System.out.println("cam node rot --- "+p.getFirstPersonCameraNode().getWorldRotation());
        System.out.println("rot node rot "+p.getRotationNode().getWorldRotation());

        bullet.addControl(new BulletTracerControl(bullet, cp, 3f));
    }

}
