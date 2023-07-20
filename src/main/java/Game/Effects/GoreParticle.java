/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Effects;

import static Game.CameraAndInput.InputController.projectBlood;
import com.Networking.Client.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author 48793
 */
public class GoreParticle {

    private final Node node;
    private final Vector3f velocity;
    private final Vector3f rotationVelocity;
    private final float finalY;
    private static final float GRAVITY = 13.81f;//9.81f;
    private boolean fell;

    public GoreParticle(Node node, Vector3f velocity, Vector3f rotationVelocity, float finalY) {
        this.node = node;
        this.velocity = velocity;
        this.rotationVelocity = rotationVelocity;
        this.finalY = finalY;
    }

    void updateParticle(float tpf) {
        if (node.getLocalTranslation().getY() > finalY) {
            node.rotate(rotationVelocity.x * tpf, rotationVelocity.y * tpf, rotationVelocity.z * tpf);
            node.move(velocity.mult(tpf));
            velocity.subtractLocal(0, GRAVITY * tpf, 0);
        } else if (!fell) {
            float[] startQ = new float[3];
            node.getWorldRotation().toAngles(startQ);
            startQ[0] = 0;
            startQ[2] = 0;
            node.setLocalRotation(new Quaternion().fromAngles(startQ));
            fell = true;
            node.getWorldTranslation().setY(finalY);
                    projectBlood(ClientGameAppState.getInstance(),node.getWorldTranslation().clone().add(0,1,0),new Vector3f(0,-1,0));

        }
    }

}
