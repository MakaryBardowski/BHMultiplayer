/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.cameraAndInput;

import client.ClientGameAppState;
import client.Main;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 *
 * @author 48793
 */
public class PlayerCameraControlAppState extends AbstractAppState {

    public static final float CAMERA_Y_OFFSET = 2.12f; //2.12f
    private Camera handsCam;
    private ClientGameAppState clientApp;
    private float renderDistance = 700f; //70
    private Vector3f lookDirection;
    float[] cameraRotAsAngles = new float[3];
    Quaternion temporaryCameraRotQuaternion = new Quaternion();

    public PlayerCameraControlAppState(ClientGameAppState clientApp) {
        this.clientApp = clientApp;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        initCamera();

    }

    public void initCamera() {
        clientApp.getCamera().setFrustumPerspective(45, (float) clientApp.getSettings().getWidth() / clientApp.getSettings().getHeight(), 0.01f, renderDistance);
    }

    @Override
    public void update(float tpf) {
        restrictCameraRotation();
        rotatePlayerTowardsLookDirection();
    }

    private void restrictCameraRotation() {
        clientApp.getCamera().getRotation().toAngles(cameraRotAsAngles);

        if (cameraRotAsAngles[0] > FastMath.QUARTER_PI) {

            cameraRotAsAngles[0] = FastMath.QUARTER_PI;

            clientApp.getCamera().setRotation(temporaryCameraRotQuaternion.fromAngles(cameraRotAsAngles));

        } else if (cameraRotAsAngles[0] < -FastMath.QUARTER_PI) {

            cameraRotAsAngles[0] = -FastMath.QUARTER_PI;

            clientApp.getCamera().setRotation(temporaryCameraRotQuaternion.fromAngles(cameraRotAsAngles));

        }
    }

    private void rotatePlayerTowardsLookDirection() {
        if (clientApp.getPlayer() != null && clientApp.getPlayer().getHealth() > 0) {//                                                          2.12f so you are level with humanoids

//            clientApp.getCamera().setLocation(new Vector3f(clientApp.getPlayer().getNode().getWorldTranslation().x, CAMERA_Y_OFFSET + clientApp.getPlayer().getNode().getWorldTranslation().getY(), clientApp.getPlayer().getNode().getWorldTranslation().z));
//            if (lookDirection != null) {
//                clientApp.getPlayer().getGunNode().getParent().lookAt(lookDirection, Vector3f.UNIT_Y);
//            }
//
//            // first person alignment for bullets to work
//            float[] playerAngles = new float[3];
//            clientApp.getPlayer().getGunNode().getLocalRotation().toAngles(playerAngles);
//            playerAngles[0] = cameraRotAsAngles[0];
//            clientApp.getPlayer().getGunNode().getParent().setLocalRotation(new Quaternion().fromAngles(playerAngles));
//            CollisionResults results = new CollisionResults();
//            Ray ray = new Ray(clientApp.getCamera().getLocation(), clientApp.getCamera().getDirection());
//            clientApp.getMapNode().collideWith(ray, results);
//
//            if (results.size() > 0) {
//                CollisionResult closest = results.getClosestCollision();
//                lookDirection = new Vector3f(closest.getContactPoint().x, clientApp.getPlayer().getNode().getWorldTranslation().getY(), closest.getContactPoint().z);
//                clientApp.getPlayer().getNode().lookAt(lookDirection, Vector3f.UNIT_Y);
//
//            }
        }
    }

}
