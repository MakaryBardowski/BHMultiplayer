/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import game.entities.InteractiveEntity;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author tomasz_potoczko
 */
public class WeaponSwingControl extends AbstractControl{
    Quaternion currentRotation = new Quaternion(0,0,0,1);
    Quaternion targetRotation = new Quaternion(0,0,0,1);
    int snap=10;
    boolean swinging = false;
    boolean peaked = false;
    ArrayList<InteractiveEntity> mobs;

    WeaponSwingControl(Collection<InteractiveEntity> mobCollection) {
        this.mobs = new ArrayList<>(mobCollection);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        swingUpdate(tpf);
    }
    
    private void swingUpdate(float tpf) {
        targetRotation.nlerp(new Quaternion(0, 0, 0, 1), tpf*snap);
        currentRotation.slerp(targetRotation, tpf*snap);
        
        spatial.setLocalRotation(currentRotation);
        updateSwingStatus();

    }
    
    ///checks in a cone if a mob is in range and returns a list of mob names that do
    private ArrayList<InteractiveEntity> coneCollisionCheck(float coneAngle, float weaponRange){
        ArrayList<InteractiveEntity> collisions = new ArrayList<>();
        
        ///change this to loop over Mob mob in an area
        for (InteractiveEntity mob: mobs){
            Spatial m = mob.getNode();
            
            ///this will be replace by mop.getRadius() or smth like that
            BoundingBox b = (BoundingBox) m.getWorldBound();
            float furthestEdge = FastMath.sqrt((b.getXExtent()/2)*(b.getXExtent()/2) + (b.getZExtent()/2)*(b.getZExtent()/2));
            ///
            
            float range = weaponRange+(furthestEdge);

            ///caster Rot vector needs to be a rot vector of the node which is under BOTH x- and y-axis rotation
            Vector3f casterRot = spatial.getParent().getParent().getWorldRotation().getRotationColumn(2);
            ///this needs to be updated to weaponOffset+playerHeight
            Vector3f casterPos = spatial.getWorldTranslation().subtract(0, 2.12f, 0);
            Vector3f mobPos = m.getWorldTranslation();
            Vector3f vectorFromCasterToTarget = casterPos.subtract(mobPos);
            

            double dotProd = casterRot.getX() * vectorFromCasterToTarget.getX() + casterRot.getY() * vectorFromCasterToTarget.getY() + casterRot.getZ() * vectorFromCasterToTarget.getZ();
            double casterRotMag = FastMath.sqrt(casterRot.getX()*casterRot.getX() + casterRot.getY()*casterRot.getY() + casterRot.getZ()*casterRot.getZ());
            double vectorMag = FastMath.sqrt(vectorFromCasterToTarget.getX()*vectorFromCasterToTarget.getX() + vectorFromCasterToTarget.getY() * vectorFromCasterToTarget.getY() + vectorFromCasterToTarget.getZ() * vectorFromCasterToTarget.getZ());
            double angle = 180 - Math.toDegrees(FastMath.acos((float) (dotProd / (casterRotMag * vectorMag))));

            float distanceToMob = FastMath.sqrt(FastMath.pow(casterPos.getX()-mobPos.getX(), 2) + FastMath.pow(casterPos.getZ()-mobPos.getZ(), 2));
            if (angle <= coneAngle && distanceToMob <= range){
                collisions.add(mob);
            }
            //System.out.println("mob:"+mob.getName());
            //System.out.println("\tdistance and angle "+distanceToMob+" "+angle);
        }

        return collisions;
    }
    
    ///sets booleans so that we collide only on the way to the peaked rotation and not on the way back
    private void updateSwingStatus() {
        if (!peaked){
            swinging = targetRotation.getY() > 0.001;
            peaked = currentRotation.getY() > targetRotation.getY();
        } else{
            swinging=false;
        } 
        
        if (targetRotation.getY() < 0.001){
            peaked=false;
        }
    }

    ///increase rarget rotation by an angle and set booleans so when swinging mid swing it can collide
    ArrayList<InteractiveEntity> swing() {
        targetRotation = targetRotation.add((new Quaternion()).fromAngleAxis(1.5f*FastMath.PI, new Vector3f(2, 2, -1)));
        swinging=true;
        peaked=false;
        
        return coneCollisionCheck(45, 4.1f);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
