/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import FirstPersonHands.FirstPersonHandAnimationData;
import client.ClientGameAppState;
import client.Main;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import game.items.ItemTemplates;
import lombok.Getter;
import messages.GrenadeThrownMessage;
import messages.items.MobItemInteractionMessage;
import messages.items.NewGrenadeMessage;
import static client.ClientGameAppState.removeEntityByIdClient;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.ClipAction;
import de.lessvoid.nifty.controls.label.LabelControl;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.items.Holdable;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class Grenade extends ThrowableWeapon {

    private int thirdPersonModelParentIndex;

    @Getter
    private final float throwSpeed = 40;

    public Grenade(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, damage, template, name, node);
    }

    public Grenade(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, damage, template, name, node, droppable);
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
        NewGrenadeMessage msg = new NewGrenadeMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void attack(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerAttack(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerHoldInRightHand(Player p) {
        AssetManager assetManager = Main.getInstance().getAssetManager();

        p.setEquippedRightHand(this);

        if (playerIsMyPlayer(p)) {
            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText("");

            Node model = (Node) assetManager.loadModel(template.getFpPath());

            Geometry ge = (Geometry) model.getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);

            p.getFirstPersonHands().attachToHandR(model);
            model.scale(2.f);
            model.move(0.1f, 0.2f, -0.4f);
//            model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));

            ((ClipAction) p.getFirstPersonHands().getHandsComposer().action("ThrowGrenade")).setTransitionLength(0);

            p.getFirstPersonHands().setHandsAnim(FirstPersonHandAnimationData.HOLD_GRENADE);

            p.getFirstPersonHands().getHandsComposer().setGlobalSpeed(1f);
        }

        humanEquipInThirdPerson(p, assetManager);

    }

    @Override
    public void playerUseInRightHand(Player p) {
        var composer = p.getFirstPersonHands().getHandsComposer();

        Action toIdle = composer.action("KnifeAttackToHold");
        Tween idle = Tweens.callMethod(composer, "setCurrentAction", "HoldKnife");
        composer.actionSequence("ThrowToHold", toIdle, idle);

        Action attack = composer.action("ThrowGrenade");
        Tween attackToIdle = Tweens.callMethod(composer, "setCurrentAction", "ThrowToHold");
        composer.actionSequence("fullSwing", attack, attackToIdle);

        ((ClipAction) composer.action("ThrowGrenade")).setTransitionLength(0);
        composer.setCurrentAction("fullSwing");
        composer.getCurrentAction().setSpeed(1.5f);
        throwGrenade(p);
    }

    private void throwGrenade(Player p) {
//            ServerMain.getInstance().getLevelManagerMobs().forEach((a,b) -> {
//                System.out.println("enitty "+a+"  class "+b.getClass().getSimpleName());
//            });
        var cs = ClientGameAppState.getInstance();
        var grenadeInitialPosition = cs.getCamera().getLocation();
        var throwDirection = cs.getCamera().getDirection().normalize();
        
        var gtm = new GrenadeThrownMessage(p.getId(), id, grenadeInitialPosition, throwDirection);
        gtm.setReliable(true);

        cs.getClient().send(gtm);

        p.removeFromEquipment(this);
        p.unequip(this);
        removeEntityByIdClient(id);

    }

    @Override
    public void playerEquip(Player p) {
        Holdable unequippedItem = p.getEquippedRightHand();
        if (unequippedItem != null) {
            unequippedItem.playerUnequip(p);
        }
        p.setHoldsTrigger(false);
        playerHoldInRightHand(p);
    }

    @Override
    public void playerUnequip(Player p) {
        if (p.getEquippedRightHand() == this) {
            p.setEquippedRightHand(null);
            p.getFirstPersonHands().getRightHandEquipped().detachAllChildren();
            p.getSkinningControl().getAttachmentsNode("HandR").detachChildAt(thirdPersonModelParentIndex);
            System.out.println("unequipping GRENADE!");
        }

    }

    @Override
    public void playerServerEquip(HumanMob m) {
        m.setEquippedRightHand(this);
    }

    @Override
    public void playerServerUnequip(HumanMob m) {
        m.setEquippedRightHand(null);
    }

    private boolean playerIsMyPlayer(Player p) {
        return p == ClientGameAppState.getInstance().getPlayer();
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Throwable\n");
        builder.append("-Creates smoke screen");
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
        model.move(0, -0.33f, 0.2f);

        Geometry ge = (Geometry) (model.getChild(0));
        Material originalMaterial = ge.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        ge.setMaterial(newMaterial);
        float length = 1f;
        float width = 1f;
        float height = 1f;
        model.scale(length, width, height);
        humanMob.getSkinningControl().getAttachmentsNode("HandR").attachChild(model);
        setupModelShootability(model, humanMob.getId());
        thirdPersonModelParentIndex = humanMob.getSkinningControl().getAttachmentsNode("HandR").getChildIndex(model);
    }
}
