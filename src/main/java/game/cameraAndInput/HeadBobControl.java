/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.cameraAndInput;

import game.entities.mobs.Player;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author tomasz_potoczko
 * @author 48793
 */
public class HeadBobControl extends AbstractControl{
    private boolean headBobActive = false;
    private float headBobTimer=0;
    private final float frequency = 15;
    private final float amplitude = 0.018f;
    private final float wavelength = 2*FastMath.PI;
    private final Player player;
    private final Vector3f initialLocalTranslation;
    private float onStopInterpolationValue = 0;

    public HeadBobControl(Player player,Vector3f initialLocalTranslation) {
        this.player=player;
        this.initialLocalTranslation = initialLocalTranslation.clone();
    }

    @Override
    protected void controlUpdate(float tpf) {
        setHeadBobActive();
        headBob(tpf);   
    }
    
    private void headBob(float tpf) {
        if (headBobActive == true){
            onStopInterpolationValue = 0;
            int nextStep = (int) (1 + Math.floor((headBobTimer)*frequency/wavelength));
            float nextStepTime = (nextStep*wavelength)/frequency;
            headBobTimer = Math.min(headBobTimer+tpf, nextStepTime);
            
            if (headBobTimer == nextStepTime){
                headBobActive = false;
                headBobTimer=0;
            }
//            player.getGunNode().move(0, (float) (Math.sin(headBobTimer*20)*amplitude)*tpf, 0);
            
            spatial.move(0, (float) (Math.sin(headBobTimer*frequency)*amplitude), 0);
            
//            //bob stabilisation <- doesnt work
//            //it's supposed to make the point we look at stay stationary and not bob with the camera
//            
//            CollisionResults results = new CollisionResults();
//            Ray ray = new Ray(cam.getLocation().add(0, 0, .5f), cam.getDirection());
//            obs.collideWith(ray, results);
//
//            if (results.size() > 0) {
//                spatial.lookAt(results.getClosestCollision().getContactPoint().clone(), new Vector3f(0, 1, 0));
//            }
        } else {
            onStopInterpolationValue += tpf*frequency;
            if(onStopInterpolationValue > 1)
                onStopInterpolationValue = 1;
            var v = spatial.getLocalTranslation().clone().interpolateLocal(initialLocalTranslation, onStopInterpolationValue);
            spatial.setLocalTranslation(v);
        }
    }  
    
    void setHeadBobActive() {
        headBobActive = player.isForward() || player.isBackward() || player.isLeft() || player.isRight();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
