/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.cameraAndInput;

import game.mobs.Player;
import client.ClientGameAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import de.lessvoid.nifty.render.NiftyImage;

/**
 *
 * @author 48793
 *
 */
public class InputController {

    private NiftyImage guiElement;
    private InputManager m;
    private HeadBobControl headBob;

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

    public void createInputListeners(ClientGameAppState gs) {
        m = gs.getInputManager();
        initKeys(m, initActionListener(gs), initAnalogListener(gs));

        headBob = new HeadBobControl(gs.getPlayer());
        gs.getPlayer().getRotationNode().addControl(headBob);
    }

    private ActionListener initActionListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();
        System.out.println("player " + player);
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
                    player.equip(player.getEquipment()[Integer.parseInt(name) - 1]);

                }

                // attack test
                if (!player.isDead() && !player.isViewingEquipment() && name.equals("Attack") && !keyPressed) {
//                    player.setShooting(false);
                    player.getEquippedRightHand().playerUseRight(player);

//                    projectBlood(gs);
                }
                if (!player.isDead() && !player.isViewingEquipment() && name.equals("AttackR") && !keyPressed) {

                }
//                else if (!player.isDead() && name.equals("Attack")) {
//                    player.setShooting(true);
//                }

                if (name.equals("I") && !gs.getPlayer().isDead() && !keyPressed) {
                    gs.getFlyCam().setDragToRotate(!gs.getFlyCam().isDragToRotate());
                    Player p = gs.getPlayer();
                    p.setViewingEquipment(!p.isViewingEquipment());

                    for (int eqSlot = 0; eqSlot < p.getEquipment().length; eqSlot++) {
//                        if (p.getEquipment()[eqSlot] != null) {
//
//                            guiElement = gs.getNifty().getRenderEngine().createImage(gs.getNifty().getCurrentScreen(), gs.getPlayer().getEquipment()[eqSlot].getIconPath(), false);
//                            gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).getRenderer(ImageRenderer.class).setImage(guiElement);
//                        }

                        gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).setVisible(!gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).isVisible());
                    }
                }

//                if (name.equals("E") && !gs.getPlayer().isDead() && !keyPressed) {
//                    gs.getPlayer().checkIfPlayerPicked(gs.getPickableNode(), gs);
//                }
//                if (name.equals("R") && !gs.getPlayer().isDead() && !keyPressed) {
//                    gs.getPlayer().getEquippedRightHand().reload(gs.getPlayer(), gs);
//                }
                if (name.equals("K") && !keyPressed) {
//                    gs.chatPutMessage("Cheat!");
                }

            }
        };

        gs.setActionListener(actionListener);

        return actionListener;
    }

    private AnalogListener initAnalogListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();

        AnalogListener analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                if (name.equals("MouseMovedX")) {
                    deltaX = gs.getInputManager().getCursorPosition().x - prevJMEcursorPos.x;
                    if (!gs.getPlayer().isViewingEquipment()) {
                        cursorPositionForPlayerRotation.setX(cursorPositionForPlayerRotation.x + deltaX);
                        centeredX = cursorPositionForPlayerRotation.x - 0.5f * gs.getSettings().getWidth();
                        newRotationQuat = new Quaternion();
                        newRotationQuat.fromAngles(0, -0.005f * centeredX, 0);
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
                        newRotationQuat.fromAngles(-0.005f * centeredY, 0, 0);
                        player.getRotationNode().setLocalRotation(newRotationQuat);
                    }
                    prevJMEcursorPos.setY(gs.getInputManager().getCursorPosition().y);

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

}
