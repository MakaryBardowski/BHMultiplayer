/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.cameraAndInput;

import game.entities.mobs.Player;
import client.ClientGameAppState;
import client.Main;
import static client.Main.CAM_ROT_SPEED;
import static client.Main.CAM__MOVE_SPEED;
import client.PlayerHUDController;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import static debugging.DebugUtils.DEBUG_GEO;
import game.entities.InteractiveEntity;
import game.items.Item;
import game.items.weapons.Grenade;
import game.items.weapons.MeleeWeapon;
import game.items.weapons.RangedWeapon;
import java.util.Arrays;
import messages.MobRotUpdateMessage;

/**
 *
 * @author 48793
 *
 */
public class InputController {

    private NiftyImage guiElement;
    private InputManager m;
    private HeadBobControl headBob;
    private static final float ONE_DEGREE = 0.0174f;
    private static final float NOTIFY_SERVER_THRESHOLD = ONE_DEGREE * 2;
    /*
    JME cursor position cannot be altered externally
     */
    private final Vector2f prevJMEcursorPos = new Vector2f();
    private final Vector2f cursorPositionForPlayerRotation = new Vector2f();
    private float deltaX;
    private float deltaY;
    private float centeredX;
    private float centeredY;
    private Quaternion newRotationQuat;
    private float currentMaxDeviationX = 0;
    private float currentMaxDeviationY = 0;

    private boolean updatePlayersThirdPersonHandsRot = false;

    public void createInputListeners(ClientGameAppState gs) {
        m = gs.getInputManager();
        initKeys(m, initActionListener(gs), initAnalogListener(gs));

        headBob = new HeadBobControl(gs.getPlayer());
        gs.getPlayer().getRotationNode().addControl(headBob);
    }

    private ActionListener initActionListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();
        ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                if (!player.isDead() && name.equals("W") && !keyPressed) {
                    player.setForward(false);
                    setMovingAnimationidle(player);

                } else if (!player.isDead() && name.equals("W")) {
                    player.setForward(true);
                    setMovingAnimationPlayer(player, 2.5f);

                }

                if (!player.isDead() && name.equals("S") && !keyPressed) {
                    player.setBackward(false);
                    setMovingAnimationidle(player);

                } else if (!player.isDead() && name.equals("S")) {
                    player.setBackward(true);
                    setMovingAnimationPlayer(player, 1.25f);
                }

                if (!player.isDead() && name.equals("A") && !keyPressed) {
                    player.setLeft(false);
                    setMovingAnimationidle(player);

                } else if (!player.isDead() && name.equals("A")) {
                    player.setLeft(true);
                    setMovingAnimationPlayer(player, 1.25f);

                }

                if (!player.isDead() && name.equals("D") && !keyPressed) {
                    player.setRight(false);
                    setMovingAnimationidle(player);
                } else if (!player.isDead() && name.equals("D")) {
                    player.setRight(true);
                    setMovingAnimationPlayer(player, 1.25f);

                }

                if (!player.isDead() && name.equals("1") && !keyPressed) {
                    Item equippedItem = player.getEquipment()[Integer.parseInt(name) - 1];
                    player.equip(equippedItem);
                    PlayerHUDController.sendEquipMessageToServer(equippedItem);
                }

                // attack test
                if (!player.isViewingEquipment() && player.getEquippedRightHand() != null && name.equals("Attack") && !keyPressed) {
                    if (player.getEquippedRightHand() instanceof Grenade) { // if its a grenade, dont throw when released
                        player.getEquippedRightHand().playerUseInRightHand(player);
                    } else {
                        player.setHoldsTrigger(false);
                    }
                } else if (!player.isDead() && !player.isViewingEquipment() && player.getEquippedRightHand() != null && name.equals("Attack") && keyPressed) {
                    if (player.getEquippedRightHand() instanceof RangedWeapon || player.getEquippedRightHand() instanceof MeleeWeapon) { // if its a ranged weapon, set holds trigger which makes the auto shoot
                        player.setHoldsTrigger(true);
                    }
                }

                if (name.equals("I") && !gs.getPlayer().isDead() && !keyPressed) {
                    gs.getFlyCam().setDragToRotate(!gs.getFlyCam().isDragToRotate());
                    Player p = gs.getPlayer();
                    p.setViewingEquipment(!p.isViewingEquipment());
                    p.setHoldsTrigger(false);

                    for (int eqSlot = 0; eqSlot < p.getEquipment().length; eqSlot++) {
                        String iconString = "Textures/GUI/EquipmentIcons/equipmentSlotEmpty.png";
                        if (p.getEquipment()[eqSlot] != null && p.getEquipment()[eqSlot].getTemplate().getIconPath() != null) {
                            iconString = gs.getPlayer().getEquipment()[eqSlot].getTemplate().getIconPath();
                        }

                        guiElement = gs.getNifty().getRenderEngine().createImage(gs.getNifty().getCurrentScreen(), iconString, false);
                        gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).getRenderer(ImageRenderer.class).setImage(guiElement);

                        gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).setVisible(!gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).isVisible());
                    }
                }

                if (name.equals(
                        "E") && !gs.getPlayer().isDead() && !keyPressed) {
                    interact();
                }
                if (name.equals("R") && !gs.getPlayer().isDead() && !keyPressed) {
                    var equipped = gs.getPlayer().getEquippedRightHand();
                    if (equipped != null && equipped instanceof RangedWeapon ranged) {
                        ranged.reload(player);
                    }
                }

//                if (name.equals("2") && !keyPressed) {
//                    DEBUG_GEO.rotate(10 * FastMath.DEG_TO_RAD, 0, 0);
//                }
//
//                if (name.equals("3") && !keyPressed) {
//                    DEBUG_GEO.rotate(0, 0, 10 * FastMath.DEG_TO_RAD);
//                }
//                if (name.equals("4") && !keyPressed) {
//                    DEBUG_GEO.rotate(0, 10 * FastMath.DEG_TO_RAD, 0);
//                }

                if (name.equals(
                        "K") && !keyPressed) {
//                    float[] rot = new float[3];
//                    DEBUG_GEO.getLocalRotation().toAngles(rot);
//                    System.out.println("DEBUG GEO ROT "+Arrays.toString(rot));
                    player.setRight(false);
                    player.setLeft(false);
                    player.setBackward(false);
                    player.setForward(false);

                    System.out.println("cheat!");
                    Main.getInstance().getFlyCam().setMoveSpeed(CAM__MOVE_SPEED);
                    Main.getInstance().getFlyCam().setRotationSpeed(CAM_ROT_SPEED);

                    player.getMainCameraNode().removeFromParent();

                    player.getFirstPersonHands().getHandsNode().setCullHint(Spatial.CullHint.Always);
                    player.getNode().setCullHint(Spatial.CullHint.Inherit);

                    player.setMovementControlLocked(true);
                    updatePlayersThirdPersonHandsRot = true;

                }
            }
        };

        gs.setActionListener(actionListener);

        return actionListener;
    }

    private AnalogListener initAnalogListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();

//        prevJMEcursorPos.set(gs.getInputManager().getCursorPosition());
        AnalogListener analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                if (name.equals("MouseMovedX")) {
                    deltaX = gs.getInputManager().getCursorPosition().x - prevJMEcursorPos.x;
                    if (!gs.getPlayer().isViewingEquipment()) {
                        cursorPositionForPlayerRotation.setX(cursorPositionForPlayerRotation.x + deltaX);
                        centeredX = cursorPositionForPlayerRotation.x - 0.5f * gs.getSettings().getWidth();
                        newRotationQuat = new Quaternion();

                        centeredX = -0.005f * centeredX;

                        newRotationQuat.fromAngles(0, centeredX, 0);

                        currentMaxDeviationX += centeredX;

                        player.getNode().setLocalRotation(newRotationQuat);
                    }
                    prevJMEcursorPos.setX(gs.getInputManager().getCursorPosition().x);

                }
                if (name.equals("MouseMovedY")) {
                    deltaY = gs.getInputManager().getCursorPosition().y - prevJMEcursorPos.y;
                    if (!gs.getPlayer().isViewingEquipment()) {

                        cursorPositionForPlayerRotation.setY(cursorPositionForPlayerRotation.y + deltaY);
                        centeredY = cursorPositionForPlayerRotation.y - 0.5f * gs.getSettings().getWidth();
                        newRotationQuat = new Quaternion();

                        centeredY = -0.005f * centeredY; //reusingVariable
                        newRotationQuat.fromAngles(centeredY, 0, 0);

                        currentMaxDeviationY += centeredY;

                        player.getRotationNode().setLocalRotation(newRotationQuat);

                    }
                    prevJMEcursorPos.setY(gs.getInputManager().getCursorPosition().y);

                }
                if (currentMaxDeviationX >= NOTIFY_SERVER_THRESHOLD || currentMaxDeviationX <= -NOTIFY_SERVER_THRESHOLD || currentMaxDeviationY >= NOTIFY_SERVER_THRESHOLD || currentMaxDeviationY <= -NOTIFY_SERVER_THRESHOLD) {
                    currentMaxDeviationX = 0;
                    currentMaxDeviationY = 0;
                    MobRotUpdateMessage rotu = new MobRotUpdateMessage(player.getId(), player.getNode().getLocalRotation().mult(player.getRotationNode().getLocalRotation()));
                    ClientGameAppState.getInstance().getClient().send(rotu);

                    if (updatePlayersThirdPersonHandsRot) {
                        var handsRot = player.getRotationNode().getLocalRotation();

                        var skinningControl = player.getSkinningControl();
                        skinningControl.getArmature().getJoint("HandR").setLocalRotation(handsRot);

                        var rot = skinningControl.getArmature().getJoint("HandR").getLocalRotation();
                        skinningControl.getArmature().getJoint("HandL").setLocalRotation(rot);
                        skinningControl.getArmature().getJoint("Head").getLocalTransform().setRotation(rot);
                    }
                }

            }

        };

        gs.setAnalogListener(analogListener);

        return analogListener;
    }

    private void initKeys(InputManager inputManager, ActionListener actionListener, AnalogListener analogListener) {

        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W)); // forward
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S)); // backward
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A)); // strafe left
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D)); // strafe right

        inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // shoot
        inputManager.addMapping("AttackR", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // shoot

        inputManager.addMapping("Grenade", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("B", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E)); // activate item
        inputManager.addMapping("I", new KeyTrigger(KeyInput.KEY_I)); // open EQ
        inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R)); // strafe right

        inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1)); // hotbar 1
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2)); // hotbar 2
        inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3)); // hotbar 3
        inputManager.addMapping("4", new KeyTrigger(KeyInput.KEY_4)); // hotbar 4

        inputManager.addMapping("MouseMovedX",
                new MouseAxisTrigger(MouseInput.AXIS_X, false),
                new MouseAxisTrigger(MouseInput.AXIS_X, true)
        );
        inputManager.addMapping("MouseMovedY",
                new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                new MouseAxisTrigger(MouseInput.AXIS_Y, true)
        );

        inputManager.addListener(analogListener, "MouseMovedX");
        inputManager.addListener(analogListener, "MouseMovedY");
        inputManager.addListener(actionListener, "W");
        inputManager.addListener(actionListener, "S");
        inputManager.addListener(actionListener, "A");
        inputManager.addListener(actionListener, "D");
        inputManager.addListener(actionListener, "R");

        inputManager.addListener(actionListener, "Attack");
        inputManager.addListener(actionListener, "AttackR");

        inputManager.addListener(actionListener, "Grenade");
        inputManager.addListener(actionListener, "K");
        inputManager.addListener(actionListener, "B");
        inputManager.addListener(actionListener, "E");
        inputManager.addListener(actionListener, "I");

        inputManager.addListener(actionListener, "1");

        inputManager.addListener(actionListener, "2");
        inputManager.addListener(actionListener, "3");
        inputManager.addListener(actionListener, "4");

    }

    private void setMovingAnimationPlayer(Player p, float animSpeed) {
//        if (p.getHandsAnimChannel() != null && !p.getHandsAnimChannel().getAnimationName().equals("Run") || p.getHandsAnimChannel().getSpeed() < animSpeed) {
//            p.getHandsAnimChannel().setAnim("Run");
//            p.getHandsAnimChannel().setSpeed(animSpeed);
//        }
    }

    private void setMovingAnimationidle(Player p) {
//        if (p.getHandsAnimChannel() != null && !p.isForward() && !p.isBackward() && !p.isLeft() && !p.isRight()) {
//            p.getHandsAnimChannel().setAnim("Idle");
//            p.getHandsAnimChannel().setLoopMode(LoopMode.DontLoop);
//
//        }
    }

    private void interact() {
        ClientGameAppState cs = ClientGameAppState.getInstance();
        Player p = cs.getPlayer();
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        cs.getPickableNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            String hitName = closest.getGeometry().getName();
            if (hitName.matches("-?\\d+")) {
                Integer hitId = Integer.valueOf(hitName);
                InteractiveEntity mobHit = (InteractiveEntity) ClientGameAppState.getInstance().getMobs().get(hitId);
                if (mobHit.getNode().getWorldTranslation().distance(p.getNode().getWorldTranslation()) <= Player.PICKUP_RANGE) {
                    mobHit.onInteract();
                }
            }
        }
    }

}
