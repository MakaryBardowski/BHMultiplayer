/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.CameraAndInput;

import Game.Effects.DecalProjector;
import Game.Items.Item;
import Game.Mobs.Player;
import com.Networking.Client.ClientGameAppState;
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
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import java.util.Random;

/**
 *
 * @author 48793
 *
 * klasa odpowiadaj¹ca za obs³ugê inputu gracza (klawiatura, przyciski myszy)
 */
public class InputController {

    private NiftyImage guiElement;
    private InputManager m;
    private HeadBobControl headBob;

    public void createInputListeners(ClientGameAppState gs) {
        m = gs.getInputManager();
        initKeys(m, initActionListener(gs), initAnalogListener(gs));
        
        headBob = new HeadBobControl(gs.getPlayer());
        gs.getPlayer().getMainCameraNode().addControl(headBob);
    }

    private ActionListener initActionListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();
        System.out.println("player "+player);
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
                if (!player.isDead() && name.equals("Attack") && !keyPressed) {
//                    player.setShooting(false);
                    player.getEquippedRightHand().playerUseRight(player);

//                    projectBlood(gs);
                }
//                else if (!player.isDead() && name.equals("Attack")) {
//                    player.setShooting(true);
//                }

                if (name.equals("I") && !gs.getPlayer().isDead() && !keyPressed) {
                    gs.getFlyCam().setDragToRotate(!gs.getFlyCam().isDragToRotate());
                    for (int eqSlot = 0; eqSlot < gs.getPlayer().getEquipment().length; eqSlot++) {
//                        if (gs.getPlayer().getEquipment()[eqSlot] != null) {
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
                    float centredX = m.getCursorPosition().x - 0.5f * gs.getSettings().getWidth();
                    Quaternion quat = new Quaternion();
                    quat.fromAngles(0, -0.005f * centredX, 0);

                    gs.getPlayer().getNode().setLocalRotation(quat);

                }
                if (name.equals("MouseMovedY")) {
                    float centredY = m.getCursorPosition().y - 0.5f * gs.getSettings().getWidth();
                    Quaternion quat = new Quaternion();
                    quat.fromAngles(-0.005f * centredY, 0, 0);

                   
                    gs.getPlayer().getRotationNode().setLocalRotation(quat);
                    gs.getPlayer().getFirstPersonCameraNode().setLocalRotation(quat);
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

    private void projectBlood(ClientGameAppState gs) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {

            Camera cam = gs.getCamera();
            var contactFaceNormal = new Vector3f(); // Get the normal vector of the contact face
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(gs.getCamera().getLocation(), gs.getCamera().getDirection());
            gs.getMapNode().collideWith(ray, results);
            var contactPoint = new Vector3f();
            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();
                contactFaceNormal = closest.getContactNormal();
                contactPoint = closest.getContactPoint();
            }
// Calculate the position aligned with the contact face normal
//var pos = cam.getLocation().add(contactFaceNormal.mult(dist / 2));
            var pos = contactPoint.add(contactFaceNormal.mult(0.1f));
// Calculate the rotation aligned with the contact face normal
//var rot = new Quaternion().lookAt(contactFaceNormal.negate(), Vector3f.UNIT_Y);
            var rot = cam.getRotation().clone();
            var dist = contactPoint.distance(pos) + 35f;
//pos.addLocal(rot.getRotationColumn(2).mult(dist/2));
            var projectionBox = new Vector3f(3f, 3f, dist);

            var projector = new DecalProjector(gs.getMapNode(), pos, rot, projectionBox);
            projector.setSeparation(0.002f);
            var geometry = projector.project();

            Texture texture = gs.getAssetManager().loadTexture("Textures/Gameplay/Decals/testBlood" + new Random().nextInt(2) + ".png");
            Material material = new Material(gs.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            material.setTexture("ColorMap", texture);
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            material.getAdditionalRenderState().setDepthWrite(false);
            material.getAdditionalRenderState().setPolyOffset(-4, -1f);
            // Apply the material to the geometry
            geometry.setMaterial(material);
            geometry.setQueueBucket(Bucket.Transparent);

            geometry.setMaterial(material);

            gs.getRootNode().attachChild(geometry);
        }

        System.out.println(System.currentTimeMillis() - time + "ms");
//                    var box = new Geometry("box", new Box(projectionBox.x / 2f, projectionBox.y / 2f, projectionBox.z / 2f));
//                    var boxmat = new Material(gs.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//                    boxmat.getAdditionalRenderState().setWireframe(true);
//                    boxmat.setColor("Color", ColorRGBA.White);
//                    box.setMaterial(boxmat);
//                    box.setLocalTranslation(pos);
//                    box.setLocalRotation(rot);
//                    gs.getRootNode().attachChild(box);

    }
}
