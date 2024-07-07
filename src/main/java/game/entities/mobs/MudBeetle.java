/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities.mobs;

import behaviorTree.BehaviorNode;
import behaviorTree.BehaviorTree;
import behaviorTree.LeafNode;
import behaviorTree.actions.humanActions.AttackAction;
import behaviorTree.actions.humanActions.CheckForTargetAction;
import behaviorTree.actions.humanActions.IsPathfindingNeededAction;
import behaviorTree.actions.humanActions.MoveInRangeAction;
import behaviorTree.actions.humanActions.PathfindAction;
import behaviorTree.actions.humanActions.ResetPathAction;
import behaviorTree.actions.humanActions.WalkAction;
import behaviorTree.composite.ParallelNode;
import behaviorTree.composite.SelectorNode;
import behaviorTree.composite.SequenceNode;
import behaviorTree.context.MudBeetleContext;
import game.effects.EmitterPooler;
import game.items.Item;
import game.map.collision.WorldGrid;
import client.ClientGameAppState;
import client.ClientSynchronizationUtils;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import game.effects.ParticleUtils;
import game.entities.Collidable;
import game.entities.Destructible;
import game.entities.FloatAttribute;
import game.entities.factories.MobSpawnType;
import static game.entities.mobs.Mob.MOB_ROTATION_RATE;
import static game.entities.mobs.Mob.SPEED_ATTRIBUTE;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.map.collision.RectangleAABB;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewMobMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class MudBeetle extends Mob {

    protected SkinningControl skinningControl;

    @Getter
    private AnimComposer modelComposer;

    public MudBeetle(int id, Node node, String name, SkinningControl skinningControl, AnimComposer modelComposer) {
        super(id, node, name);
        
        maxHealth = 8;
        health = 8;

        cachedSpeed = 6;
        attributes.put(SPEED_ATTRIBUTE, new FloatAttribute(cachedSpeed));

        this.skinningControl = skinningControl;
        this.modelComposer = modelComposer;

        var armature = skinningControl.getArmature();
        ArmatureMask mask = new ArmatureMask();
        mask.addBones(armature, "Leg1L");
        mask.addBones(armature, "Leg2L");
        mask.addBones(armature, "Leg3L");
        mask.addBones(armature, "Leg1R");
        mask.addBones(armature, "Leg2R");
        mask.addBones(armature, "Leg3R");

        mask.addBones(armature, "Root");
        modelComposer.makeLayer("Legs", mask);
        modelComposer.setCurrentAction("Idle", "Legs");
        modelComposer.setCurrentAction("Idle", "Default");

        createHitbox();
//                var radiusVisualizer = createCircle(16, ColorRGBA.randomColor());
//        radiusVisualizer.move(0,0.2f,0);
//        node.attachChild(radiusVisualizer);
    }

    public void addAi() {
        List<BehaviorNode> children = Arrays.asList(
                new SequenceNode(Arrays.asList(
                        new LeafNode(new CheckForTargetAction()),
                        new LeafNode(new ResetPathAction()),
                        new SelectorNode(Arrays.asList(
                                new LeafNode(new AttackAction()),
                                new SequenceNode(Arrays.asList(
                                        new LeafNode(new MoveInRangeAction()),
                                        new LeafNode(new AttackAction())
                                ))
                        ))
                )),
                new SelectorNode(Arrays.asList(
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new IsPathfindingNeededAction()),
                                new LeafNode(new PathfindAction())
                        )),
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new WalkAction()),
                                new LeafNode(new ResetPathAction())
                        ))
                ))
        );

        var rootNode = new ParallelNode(children);
        var context = new MudBeetleContext(this);
        behaviorTree = new BehaviorTree(rootNode, context);
        


    }

    @Override
    public void interpolateRotation(float tpf) {
        setRotInterpolationValue(Math.min(rotInterpolationValue + MOB_ROTATION_RATE * tpf, 1));

        node.getLocalRotation().nlerp(ClientSynchronizationUtils.GetYAxisRotation(serverRotation), rotInterpolationValue);
        node.setLocalRotation(node.getLocalRotation());
    }

    @Override
    protected void createHitbox() {
        float hitboxWidth = 0.4f;
        float hitboxHeight = 0.4f;
        float hitboxLength = 0.4f;
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        shooter.notifyServerAboutDealingDamage(damage, this);
    }

    @Override
    public void onInteract() {
        System.out.println("The " + name + " burps!");
    }

    @Override
    public void setPosition(Vector3f newPos) {
        WorldGrid grid = ClientGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerMain.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewMobMessage msg = new NewMobMessage(this, node.getWorldTranslation(), MobSpawnType.MUD_BEETLE);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void receiveDamage(float damage) {
        health -= calculateDamage(damage);

        ParticleEmitter blood = EmitterPooler.getBlood();
        Vector3f bloodPos = node.getWorldTranslation().clone().add(0, 1, 0);
        blood.setLocalTranslation(bloodPos);
        if (health <= 0) {
            blood.emitParticles(50);
            die();
        } else {
            blood.emitParticles(20);
        }

    }

    @Override
    public void die() {
        dropEquipment();
        Node gore = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore, node.getWorldTranslation().add(new Vector3f(0, 0.1f, 0)));
        gore.scale(new Random().nextFloat(0.8f, 1.25f));
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public float getArmorValue() {
        return 0;
    }

    @Override
    public float calculateDamage(float damage) {
        return damage;
    }

    @Override
    public void onCollision() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    @Override
//    public boolean wouldNotCollideWithSolidEntitiesAfterMoveClient(Vector3f moveVec) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void notifyServerAboutDealingDamage(float damage, Destructible mob) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(mob.getId(), damage);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    @Override
    public void equip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void equipServer(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequipServer(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void move(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    //-----------------------------TEST

    private static Geometry createCircle(float radius, ColorRGBA color) {
        Circle b = new Circle(radius, 60);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);

        return geom;
    }
    
    public static class Circle
        extends Mesh {

    /**
     * The center.
     *
     */
    private Vector3f center;

    /**
     * The radius.
     *
     */
    private float radius;

    /**
     * The samples.
     *
     */
    private int samples;

    /**
     * Constructs a new instance of this class.
     *
     *
     * @param radius
     *
     */
    public Circle(float radius) {

        this(Vector3f.ZERO, radius, 16);

    }

    /**
     * Constructs a new instance of this class.
     *
     *
     * @param radius
     * @param samples
     *
     */
    public Circle(float radius, int samples) {

        this(Vector3f.ZERO, radius, samples);

    }

    /**
     * Constructs a new instance of this class.
     *
     *
     * @param center
     * @param radius
     * @param samples
     *
     */
    public Circle(Vector3f center, float radius, int samples) {

        super();

        this.center = center;

        this.radius = radius;

        this.samples = samples;

        setMode(Mesh.Mode.Lines);

        updateGeometry();

    }

    protected void updateGeometry() {

        FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);

        FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);

        short[] indices = new short[samples * 2];

        float rate = FastMath.TWO_PI / (float) samples;

        float angle = 0;

        for (int i = 0; i < samples; i++) {

            float x = FastMath.cos(angle) + center.x;

            float z = FastMath.sin(angle) + center.z;

            positions.put(x * radius).put(center.y).put(z * radius);

            normals.put(new float[]{0, 1, 0});

            indices[i] = (short) i;

            if (i < samples - 1) {
                indices[i] = (short) (i + 1);
            } else {
                indices[i] = 0;
            }

            angle += rate;

        }

        setBuffer(VertexBuffer.Type.Position, 3, positions);

        setBuffer(VertexBuffer.Type.Normal, 3, normals);

        setBuffer(VertexBuffer.Type.Index, 2, indices);

        setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0, 1, 1});

        updateBound();
    }
}


}
