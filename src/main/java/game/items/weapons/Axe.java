/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import FirstPersonHands.FirstPersonHandAnimationData;
import game.items.ItemTemplates.ItemTemplate;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.ClipAction;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import de.lessvoid.nifty.controls.label.LabelControl;
import static game.entities.Animation.HUMAN_ATTACK_MELEE;
import game.entities.Collidable;
import game.entities.Destructible;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.items.Holdable;
import game.items.ItemTemplates;
import game.map.collision.CollisionDebugUtils;
import game.map.collision.RectangleOBB;
import java.util.ArrayList;
import messages.AnimationPlayedMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.NewMeleeWeaponMessage;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class Axe extends MeleeWeapon {

    private int thirdPersonModelParentIndex;

    public Axe(int id, float damage, ItemTemplate template, String name, Node node, float roundsPerSecond) {
        super(id, damage, template, name, node, roundsPerSecond);
    }

    public Axe(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, float roundsPerSecond) {
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
        p.getFirstPersonHands().getRightHandEquipped().detachAllChildren();
        p.getSkinningControl().getAttachmentsNode("HandR").detachChildAt(thirdPersonModelParentIndex);
        System.out.println("unequipping KNIFE!");

    }

    @Override
    public void playerHoldInRightHand(Player p) {
        AssetManager assetManager = Main.getInstance().getAssetManager();

        p.setEquippedRightHand(this);

        if (playerEqualsMyPlayer(p)) {

            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText("");

            Node model = (Node) assetManager.loadModel(template.getFpPath());
            model.move(0.2f, 0.8f, 0.01f);
//            Geometry ge = (Geometry) ((Node) model.getChild(0)).getChild(0);
            Geometry ge = (Geometry) model.getChild(0);

            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);

            firerateControl = new FirerateControl(this);
            model.addControl(firerateControl);

            slashControl = new SlashControl(this, p);
            model.addControl(slashControl);

            p.getFirstPersonHands().attachToHandR(model);
            model.scale(1);
            model.rotate(0, 180 * FastMath.DEG_TO_RAD, 0);

            composer = p.getFirstPersonHands().getHandsComposer();

            p.getFirstPersonHands().setHandsAnim(FirstPersonHandAnimationData.HOLD_AXE);

        }

        humanEquipInThirdPerson(p, assetManager);

    }

    @Override
    public void playerUseInRightHand(Player p) {
        if (currentAttackCooldown >= attackCooldown) {
            playerAttack(p);
        }
    }

    @Override
    public void playerAttack(Player p) {
        Action toIdle = composer.action("AxeAttackToHold");
        Tween idle = Tweens.callMethod(composer, "setCurrentAction", "HoldAxe");
        composer.actionSequence("SwingToHold", toIdle, idle);

        Action attack = composer.action("AxeAttack");
        Tween attackToIdle = Tweens.callMethod(composer, "setCurrentAction", "SwingToHold");
        composer.actionSequence("fullSwing", attack, attackToIdle);

        ((ClipAction) composer.action("AxeAttack")).setTransitionLength(0);
        composer.setCurrentAction("fullSwing");
        composer.getCurrentAction().setSpeed(0.9f);

        currentAttackCooldown = 0;

        var slashDelay = 0.23f / getAttacksPerSecond();
        slashControl.setSlashDelay(slashDelay);
        
        var animPlayed = HUMAN_ATTACK_MELEE;
        p.playAnimation(animPlayed);
        var apm = new AnimationPlayedMessage(p.getId(), animPlayed);
        ClientGameAppState.getInstance().getClient().send(apm);
    }

    @Override
    public void slashPlayer(Player p) {
        var playerPos = p.getNode().getWorldTranslation();
        var cs = ClientGameAppState.getInstance();
        var hitboxLength = 2.5f;

        var hitboxHeight = 1f;
        var hitboxWidth = 1.35f;

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
        // below loop throws null when on map edge
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
            hit.get(0).onShot(p, getDamage());
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

    private boolean playerEqualsMyPlayer(Player p) {
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
    public void slashMob(Mob p) {
        var thisTemplate = (ItemTemplates.MeleeWeaponTemplate) template;

        var playerPos = p.getNode().getWorldTranslation();
        var cs = ClientGameAppState.getInstance();
        var hitboxLength = thisTemplate.getMobUsageData().getWeaponRange();

        var hitboxHeight = 1f;
        var hitboxWidth = 1f;

        float[] mobRot = new float[3];
        p.getNode().getLocalRotation().toAngles(mobRot);

        var mobHandsHeight = p.getNode().getWorldTranslation().add(0, 1f, 0);
        var mobDir = p.getNode().getLocalRotation().getRotationColumn(2);

        var hitboxPos = playerPos.add(0, mobHandsHeight.getY() - playerPos.getY(), 0);
        hitboxPos.addLocal(mobDir.normalize().multLocal(
                hitboxLength,
                hitboxLength,
                hitboxLength
        ));

        var hitbox = new RectangleOBB(hitboxPos.clone(), hitboxWidth, hitboxHeight, hitboxLength, mobRot[0]);
//        Geometry hitboxDebug = CollisionDebugUtils.createHitboxGeometry(hitbox.getWidth(), hitbox.getHeight(), hitbox.getLength(), ColorRGBA.Red);
//        hitboxDebug.rotate(0, mobRot[1], 0);
//        hitboxDebug.setName("" + id);
//        cs.getDebugNode().attachChild(hitboxDebug);
//        hitboxDebug.setLocalTranslation(hitboxPos);

        var hit = new ArrayList<Destructible>();
        for (Collidable c : cs.getGrid().getNearbyCollisionShapeAtPos(hitbox.getPosition(), hitbox)) {
            if (c instanceof Destructible de && p != c && c.getClass() != p.getClass() && c.getCollisionShape().wouldCollideAtPosition(hitbox, c.getCollisionShape().getPosition())) {
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
            hit.get(0).onShot(p, getDamage());
        }
    }

    @Override
    public void attack(Mob m) {
        var apm = new AnimationPlayedMessage(m.getId(), HUMAN_ATTACK_MELEE);
        ServerMain.getInstance().getServer().broadcast(apm);
        slashMob(m);
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("-Damage [");
        builder.append(getDamage());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void humanMobUnequip(HumanMob m) {
        if (m.getEquippedRightHand() == this) {
            m.setEquippedRightHand(null);
        }
    }

    @Override
    public void humanMobEquip(HumanMob m) {
        Holdable unequippedItem = m.getEquippedRightHand();
        if (unequippedItem != null) {
            unequippedItem.humanMobUnequip(m);
        }
        m.setEquippedRightHand(this);
        humanEquipInThirdPerson(m, Main.getInstance().getAssetManager());
    }

    private void humanEquipInThirdPerson(HumanMob humanMob, AssetManager assetManager) {
        Node model = (Node) assetManager.loadModel(template.getDropPath());

        model.setLocalTranslation(template.getThirdPersonOffsetData().getOffset());
        model.rotate(
                template.getThirdPersonOffsetData().getRotation().x,
                template.getThirdPersonOffsetData().getRotation().y,
                template.getThirdPersonOffsetData().getRotation().z
        );

        Geometry ge = (Geometry) (model.getChild(0));
        Material originalMaterial = ge.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        ge.setMaterial(newMaterial);
        model.scale(template.getThirdPersonOffsetData().getScale());
        humanMob.getSkinningControl().getAttachmentsNode("HandR").attachChild(model);
        setupModelShootability(model, humanMob.getId());
        thirdPersonModelParentIndex = humanMob.getSkinningControl().getAttachmentsNode("HandR").getChildIndex(model);
    }

}
