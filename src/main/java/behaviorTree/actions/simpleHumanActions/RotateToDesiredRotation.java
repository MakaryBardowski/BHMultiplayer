package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class RotateToDesiredRotation extends NodeAction{ 
    
        private float dotProduct;
        private Vector3f currentDir;
        private Vector3f desiredDir;
        private Vector3f rotationAxis;
        private Quaternion stepRotation;
        private Quaternion newRotation;
        private final float rotationSpeedPerSecond = FastMath.TWO_PI;

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = (SimpleHumanMobContext) context;

            if (hc.getDesiredLookDirection() != null) {
                currentDir = hc.getHumanMob().getNode().getLocalRotation().getRotationColumn(2).normalize();
                desiredDir = hc.getDesiredLookDirection().normalizeLocal();

                dotProduct = currentDir.dot(desiredDir);

                if (dotProduct < 0.999f) { // 2.5 degrees difference

                    float angleDiff = FastMath.acos(dotProduct);
                    float rotationStep = Math.min(rotationSpeedPerSecond * ServerMain.getTimePerFrame(), angleDiff);
                    rotationAxis = currentDir.cross(desiredDir).normalize();
                    stepRotation = new Quaternion().fromAngleAxis(rotationStep, rotationAxis);

                    newRotation = stepRotation.multLocal(hc.getHumanMob().getNode().getLocalRotation());
                    hc.getHumanMob().getNode().setLocalRotation(newRotation);
                    hc.getHumanMob().getRotationChangedOnServer().set(true);
                }
            }

            return SUCCESS;
        }
}
