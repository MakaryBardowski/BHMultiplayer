/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import game.effects.GradientParticleEmitter;
import game.effects.GradientParticleMesh;
import game.items.ItemTemplates.ItemTemplate;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import projectiles.controls.BulletTracerControl;
import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.controls.label.LabelControl;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.HumanMob;
import game.items.AmmoPack;
import game.items.Holdable;
import game.items.Item;
import game.items.ItemTemplates.ItemType;
import messages.EntitySetFloatAttributeMessage;
import messages.EntitySetIntegerAttributeMessage;
import messages.HitscanTrailMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.NewRangedWeaponMessage;

/**
 *
 * @author 48793
 */
public class Pistol extends RangedWeapon {

    private static final float BULLET_SPEED = 1200f;

    private Node muzzleNode;
    private CameraRecoilControl camRecoil;
    private RecoilControl gunRecoil;
    private ParticleEmitter muzzleEmitter;

    public Pistol(int id, float damage, ItemTemplate template, String name, Node node, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, maxAmmo, roundsPerSecond);
    }

    public Pistol(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, droppable, maxAmmo, roundsPerSecond);
    }

    @Override
    public void playerEquip(Player p) {
        var packMsg = new EntitySetFloatAttributeMessage(p, Mob.SPEED_ATTRIBUTE, 169);
        packMsg.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(packMsg);

        Holdable unequippedItem = p.getEquippedRightHand();
        if (unequippedItem != null) {
            unequippedItem.playerUnequip(p);
        }
        playerHoldInRightHand(p);
    }

    @Override
    public void playerUnequip(Player p) {
        p.setEquippedRightHand(null);
        p.getGunNode().removeControl(gunRecoil);
        p.getMainCameraNode().removeControl(camRecoil);
        p.getGunNode().detachAllChildren();
    }

    @Override
    public void attack(Mob m) {

    }

    @Override
    public void playerHoldInRightHand(Player p) {
        p.setEquippedRightHand(this);

        if (isEquippedByMe(p)) {
            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText((int) getAmmo() + "/" + (int) getMaxAmmo());

            AssetManager assetManager = Main.getInstance().getAssetManager();
            Node model = (Node) assetManager.loadModel(template.getFpPath());

            model.move(-.67f, -.75f, 2.3f);
//        model.setLocalRotation((new Quaternion()).fromAngleAxis(-FastMath.PI / 1, new Vector3f(-.0f, .0f, 0)));
            model.rotate(0, 1.5f * FastMath.DEG_TO_RAD, 0);
            Geometry ge = (Geometry) ((Node) model.getChild(0)).getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);

            SkinningControl skinningControl = model.getChild(0).getControl(SkinningControl.class);
            muzzleNode = skinningControl.getAttachmentsNode("muzzleAttachmentBone");

            firerateControl = new FirerateControl(this);
            gunRecoil = new RecoilControl(0.0f, -.0f, .0f, .00f);
            camRecoil = new CameraRecoilControl(0.3f, -.1f, .1f, .05f);
            model.addControl(firerateControl);
            p.getGunNode().addControl(gunRecoil);
            p.getMainCameraNode().addControl(camRecoil);

            p.getGunNode().attachChild(model);
//            model.scale(2f);
            p.getFirstPersonCameraNode().attachChild(p.getGunNode());
            model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));

//            AnimComposer composer = model.getChild(0).getControl(AnimComposer.class);
//            composer.setCurrentAction("idle");
            muzzleEmitter = setupMuzzleFlashEmitter();
            muzzleNode.attachChild(muzzleEmitter);
        }
    }

    @Override
    public void playerUseInRightHand(Player p) {

        if (getAmmo() > 0 && currentAttackCooldown >= attackCooldown) {
            playerAttack(p);
        }
    }

    @Override
    public void playerAttack(Player p) {
        muzzleEmitter.emitParticles(1);
        currentAttackCooldown = 0;
        int newAmmo = getAmmo() - 1;

        var msg = new EntitySetIntegerAttributeMessage(this, AMMO_ATTRIBUTE, newAmmo );
        ClientGameAppState.getInstance().getClient().send(msg);

        ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText((int) newAmmo + "/" + (int) getMaxAmmo());
        hitscan(p);
    }

    private void hitscan(Player p) {
        var cs = ClientGameAppState.getInstance();
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        float distanceToFirstWall = Float.MAX_VALUE;

        Vector3f cp = null;

        if (cs.getMapNode().collideWith(ray, results) > 0) {
            distanceToFirstWall = results.getClosestCollision().getDistance();
            cp = results.getClosestCollision().getContactPoint();

        }

        results = new CollisionResults();
        cs.getDestructibleNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            cp = closest.getContactPoint();

            float distanceToFirstTarget = closest.getDistance();
            if (distanceToFirstTarget < distanceToFirstWall) {

                Integer hitId = Integer.valueOf(closest.getGeometry().getName());
                InteractiveEntity mobHit = cs.getMobs().get(hitId);
                mobHit.onShot(p, damage);
            }
        }
        createBullet(muzzleNode.getWorldTranslation(), cp);

        int hostedConnectionId = cs.getClient().getId();
        HitscanTrailMessage trailMessage = new HitscanTrailMessage(p.getId(), cp, hostedConnectionId);
        cs.getClient().send(trailMessage);

        recoilFire();
    }

    private void recoilFire() {
        camRecoil.recoilFire();
        gunRecoil.recoilFire();
    }

    public static void createBullet(Vector3f spawnpoint, Vector3f destination) {
        Node bullet = new Node("boolet");
        ClientGameAppState.getInstance().getDebugNode().attachChild(bullet);
        bullet.move(spawnpoint);
        GradientParticleEmitter trail = createTrail();
        bullet.attachChild(trail);
        bullet.addControl(new BulletTracerControl(bullet, destination, BULLET_SPEED, trail));

    }

    private static GradientParticleEmitter createTrail() {
        Material trailMat = createTrailMaterial();
        GradientParticleEmitter fire = new GradientParticleEmitter("Debris", GradientParticleMesh.Type.Triangle, 400);
        fire.setMaterial(trailMat);
        fire.setLowLife(0.7f);  // 0.7f
        fire.setHighLife(0.7f); // 0.7f
        fire.setStartSize(0.02f); // 0.02
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
        fire.setQueueBucket(RenderQueue.Bucket.Transparent);

        return fire;
    }

    private static Material createTrailMaterial() {
        Material mat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
        Texture tex1 = Main.getInstance().getAssetManager().loadTexture("Effects/Particles/part_beam.png");
        mat.setTexture("Texture", tex1);
        return mat;
    }

    @Override
    public void onShot(Mob shooter, float damage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewRangedWeaponMessage msg = new NewRangedWeaponMessage(this);
        msg.setReliable(true);
        return msg;
    }

    private boolean isEquippedByMe(Player p) {
        return p == ClientGameAppState.getInstance().getPlayer();
    }

    @Override
    public void playerServerEquip(HumanMob m) {
        m.setEquippedRightHand(this);
    }

    @Override
    public void playerServerUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static ParticleEmitter setupMuzzleFlashEmitter() {
        ParticleEmitter blood
                = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 10);
        Material mat_red = new Material(Main.getInstance().getAssetManager(),
                "Common/MatDefs/Misc/Particle.j3md");
        Texture t = Main.getInstance().getAssetManager().loadTexture(
                "Textures/Gameplay/Effects/muzzleFlash.png");
        t.setMagFilter(Texture.MagFilter.Nearest);
        mat_red.setTexture("Texture", t);

        mat_red.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        blood.setQueueBucket(RenderQueue.Bucket.Transparent);
        blood.setMaterial(mat_red);
        blood.setImagesX(1);
        blood.setImagesY(1); // 2x2 texture animation
        blood.setInWorldSpace(false);
        blood.setSelectRandomImage(true);
        blood.setRandomAngle(true);

//        blood.setEndColor(ColorRGBA.White);
//        blood.setStartColor(ColorRGBA.White);
        blood.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.5f));   // red
        blood.setStartColor(new ColorRGBA(1f, 1f, 1f, 0.5f)); // yellow
        blood.setStartSize(0.315f); //1f
        blood.setEndSize(0.2f); //1.1f
        blood.setGravity(0, 0, 0);
        blood.setLowLife(0.03f);
        blood.setHighLife(0.05f);
//        blood.getParticleInfluencer().setVelocityVariation(0f);
        blood.setParticlesPerSec(0);
        return blood;
    }

    @Override
    public void reload(Mob wielder) {
        int ammoToFullClip = getMaxAmmo() - getAmmo();
        int ammoFromPack = 0;
        int localAmmo = getAmmo();
        int maxAmmo = getMaxAmmo();

        for (int i = 0; i < wielder.getEquipment().length; i++) {
            Item item = wielder.getEquipment()[i];
            if (item instanceof AmmoPack pack && pack.getTemplate().getType().equals(ItemType.PISTOL_AMMO)) {
                int initialPackAmmo = pack.getAmmo();
                ammoFromPack = Math.min(ammoToFullClip, initialPackAmmo);

                if (ammoFromPack > 0) {
                    var packMsg = new EntitySetIntegerAttributeMessage(pack, AmmoPack.AMMO_ATTRIBUTE, initialPackAmmo - ammoFromPack);
                    packMsg.setReliable(true);
                    ClientGameAppState.getInstance().getClient().send(packMsg);

                    localAmmo += ammoFromPack;
                    ammoToFullClip = maxAmmo - localAmmo;
                }
            }
        }

        var msg = new EntitySetIntegerAttributeMessage(this, AMMO_ATTRIBUTE, localAmmo);
        msg.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(msg);

    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("-Damage [");
        builder.append(damage);
        builder.append("]\n");
        builder.append("-Fire rate [");
        builder.append(attacksPerSecond);
        builder.append("]\n");
        builder.append("-Ammo [");
        builder.append(getAmmo());
        builder.append("/");
        builder.append(getMaxAmmo());
        builder.append("]");
        return builder.toString();
    }
}
