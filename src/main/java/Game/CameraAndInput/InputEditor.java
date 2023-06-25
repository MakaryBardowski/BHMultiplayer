/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.CameraAndInput;

import Game.Items.Item;
import Game.Mobs.Player;
import com.Networking.Client.ClientMain;
import com.jme3.animation.LoopMode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;

/**
 *
 * @author 48793
 * 
 * klasa odpowiadaj¹ca za obs³ugê inputu gracza (klawiatura, przyciski myszy)
 */
public class InputEditor {
    
    private NiftyImage guiElement;

    public void setupInput(ClientMain gs) {
        InputManager m = gs.getInputManager();
        initKeys(m, initActionManager(gs));
    }

    private ActionListener initActionManager(final ClientMain gs) {
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

                // eq test
//                if (!player.isDead() && name.equals("1") && !keyPressed) {
//                    player.equipItem((ItemInterface)player.getEquipment()[0], true, gs);
//
//                }

//                if (!player.isDead() && name.equals("2") && !keyPressed) {
//                    player.equipItem((ItemInterface)player.getEquipment()[1], true, gs);
//                }

                // attack test
//                if (!player.isDead() && name.equals("Attack") && !keyPressed) {
//                    player.setShooting(false);
//
//                } else if (!player.isDead() && name.equals("Attack")) {
//                    player.setShooting(true);
//                }




                if (name.equals("I") && !gs.getPlayer().isDead() && !keyPressed) {
                    gs.getFlyCam().setDragToRotate(!gs.getFlyCam().isDragToRotate());
                    for (int eqSlot = 0; eqSlot < gs.getPlayer().getEquipment().length; eqSlot++) {
                        if (gs.getPlayer().getEquipment()[eqSlot] != null) {

                            guiElement = gs.getNifty().getRenderEngine().createImage(gs.getNifty().getCurrentScreen(), gs.getPlayer().getEquipment()[eqSlot].getIconPath(), false);
                            gs.getNifty().getCurrentScreen().findElementById("slot" + eqSlot).getRenderer(ImageRenderer.class).setImage(guiElement);
                        }

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

    private void initKeys(InputManager inputManager, ActionListener actionListener) {

        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W)); // forward
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S)); // backward
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A)); // strafe left
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D)); // strafe right

        inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // shoot
        inputManager.addMapping("Grenade", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("B", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E)); // activate item
        inputManager.addMapping("I", new KeyTrigger(KeyInput.KEY_I)); // open EQ
        inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R)); // strafe right

        inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1)); // hotbar 1
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2)); // hotbar 2

        inputManager.addListener(actionListener, "W");
        inputManager.addListener(actionListener, "S");
        inputManager.addListener(actionListener, "A");
        inputManager.addListener(actionListener, "D");
        inputManager.addListener(actionListener, "R");

        inputManager.addListener(actionListener, "Attack");
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
