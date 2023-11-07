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
import java.lang.System;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.ClipAction;
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
import com.jme3.network.AbstractMessage;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.controls.label.LabelControl;
import game.entities.Collidable;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.mobs.HumanMob;
import game.items.Holdable;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.RectangleOBB;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import messages.HitscanTrailMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.NewMeleeWeaponMessage;
import messages.items.NewRangedWeaponMessage;

/**
 *
 * @author 48793
 */
public class Knife extends MeleeWeapon {

    public Knife(int id, float damage, ItemTemplate template, String name, Node node, float roundsPerSecond) {
        super(id, damage, template, name, node, roundsPerSecond);
    }

    public Knife(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, float roundsPerSecond) {
        super(id, damage, template, name, node, droppable, roundsPerSecond);
    }

    @Override
    public void playerEquip(Player p) {
        Holdable unequippedItem = p.getEquippedRightHand();
        if (unequippedItem != null) {
            unequippedItem.playerUnequip(p);
        }
        playerHoldInRightHand(p);
    }

    @Override
    public void playerUnequip(Player p) {
        p.setEquippedRightHand(null);
        p.getGunNode().detachAllChildren();
    }

    @Override
    public void playerHoldInRightHand(Player p) {
        p.setEquippedRightHand(this);

        if (isEquippedByMe(p)) {
            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText("");

            AssetManager assetManager = Main.getInstance().getAssetManager();
            Node model = (Node) assetManager.loadModel(template.getFpPath());

            model.move(-.67f, -.75f, 2.6f);
//        model.setLocalRotation((new Quaternion()).fromAngleAxis(-FastMath.PI / 1, new Vector3f(-.0f, .0f, 0)));
            model.rotate(0, 1.5f * FastMath.DEG_TO_RAD, 0);
            Geometry ge = (Geometry) ((Node) model.getChild(0)).getChild(0);

            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);

            firerateControl = new FirerateControl(this);
            model.addControl(firerateControl);

            slashControl = new SlashControl(this, p);
            model.addControl(slashControl);

            p.getGunNode().attachChild(model);
            p.getFirstPersonCameraNode().attachChild(p.getGunNode());
            model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));

            composer = model.getChild(0).getControl(AnimComposer.class);
            composer.setCurrentAction("idle");
        }
    }

    @Override
    public void playerUseInRightHand(Player p) {
        if (currentAttackCooldown >= attackCooldown) {
            playerAttack(p);
        }
    }

    @Override
    public void playerAttack(Player p) {
        Action toIdle = composer.action("toIdle");
        Tween idle = Tweens.callMethod(composer, "setCurrentAction", "idle");
        composer.actionSequence("toIdle-idle", toIdle, idle);

        Action walk = composer.action("attack1");
        Tween doneTween = Tweens.callMethod(composer, "setCurrentAction", "toIdle-idle");
        Action walkOnce = composer.actionSequence("WalkOnce", walk, doneTween);

        ((ClipAction) composer.action("attack1")).setTransitionLength(0);
        composer.setCurrentAction("WalkOnce");
        composer.getCurrentAction().setSpeed(1.25f);

        currentAttackCooldown = 0;

        var slashDelay = 0.17f / attacksPerSecond;
        slashControl.setSlashDelay(slashDelay);
    }

    @Override
    public void slashPlayer(Player p) {
        var playerPos = p.getNode().getWorldTranslation();
        var cs = ClientGameAppState.getInstance();
        var hitboxLength = 1.3f;

        var hitboxHeight = 0.75f;
        var hitboxWidth = 1f;

        float[] playerRot = new float[3];
        cs.getCamera().getRotation().toAngles(playerRot);

        var camDir = cs.getCamera().getDirection();
        var camPos = cs.getCamera().getLocation();

        var hitboxPos = playerPos.add(0, camPos.getY() - playerPos.getY(), 0);
        hitboxPos.addLocal(camDir.normalize().multLocal(
                hitboxLength,
                hitboxLength,
                hitboxLength
        ));
        var hitbox = new RectangleOBB(hitboxPos.clone(), hitboxWidth, hitboxHeight, hitboxLength, playerRot[0]);
//        Geometry hitboxDebug = CollisionDebugUtils.createHitboxGeometry(hitbox.getWidth(), hitbox.getHeight(), hitbox.getLength(), ColorRGBA.Red);
//        hitboxDebug.rotate(0, playerRot[1], 0);
//        hitboxDebug.setName("" + id);
//        cs.getDebugNode().attachChild(hitboxDebug);
//        hitboxDebug.setLocalTranslation(hitboxPos);

        var hit = new ArrayList<Destructible>();
        for (Collidable c : cs.getGrid().getNearbyCollisionShapeAtPos(hitbox.getPosition(), hitbox)) {
            if (c instanceof Destructible de && p != c && c.getCollisionShape().wouldCollideAtPosition(hitbox, c.getCollisionShape().getPosition())) {
                hit.add(de);
            }
        }

        hit.sort((a, b) -> {
            Float aVal = a.getCollisionShape().getPosition().distanceSquared(hitbox.getPosition());
            Float bVal = b.getCollisionShape().getPosition().distanceSquared(hitbox.getPosition());
            return aVal.compareTo(bVal);
        }
        );

        if (!hit.isEmpty()) {
            composer.getCurrentAction().setSpeed(0.02f);
            slashControl.setSlowMaxTime(0.05f);
            hit.get(0).onShot(p, damage);
        }
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
        NewMeleeWeaponMessage msg = new NewMeleeWeaponMessage(this);
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
        m.setEquippedRightHand(null);
    }

    @Override
    public void slashMob(Mob wielder) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
        @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("-Damage [");
        builder.append(damage);
        builder.append("]");
        return builder.toString();
    }
}
