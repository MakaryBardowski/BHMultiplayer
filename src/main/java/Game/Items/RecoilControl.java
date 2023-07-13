/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author tomas
 */
public class RecoilControl extends AbstractControl{
    
    float recoilX = -.6f;
    float recoilY = .3f;
    float recoilZ = .1f;
    int snap=20;
    float kickback;
    Quaternion currentRotationRecoil = new Quaternion(0,0,0,1);
    Quaternion targetRotationRecoil = new Quaternion(0,0,0,1);
    Vector3f currentVectorRecoil = new Vector3f(0, 0, 0);
    Vector3f targetVectorRecoil = new Vector3f(0, 0, 0);


    public RecoilControl(float kickback){
        this.kickback = kickback;
    }
    // better recoil for any type of weapon
    public void recoilUpdate(float tpf) {
        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), tpf*snap);
        currentRotationRecoil.slerp(targetRotationRecoil, tpf * snap);
        targetVectorRecoil.interpolateLocal(new Vector3f(0, 0, 0), tpf * snap);
        currentVectorRecoil.interpolateLocal(targetVectorRecoil, tpf * snap);
        
        spatial.setLocalRotation(currentRotationRecoil);
        spatial.setLocalTranslation(currentVectorRecoil);
    }
    
    public void recoilFire(){
        targetRotationRecoil = targetRotationRecoil.add((new Quaternion()).fromAngleAxis(FastMath.PI / (6-kickback), new Vector3f(recoilX, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
        targetVectorRecoil = targetVectorRecoil.subtract(0, 0, .2f);
    }
    
    public float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }
    
    public float getRecoilX() {
        return recoilX;
    }

    public void setRecoilX(float recoilX) {
        this.recoilX = recoilX;
    }

    public float getRecoilY() {
        return recoilY;
    }

    public void setRecoilY(float recoilY) {
        this.recoilY = recoilY;
    }

    public float getRecoilZ() {
        return recoilZ;
    }

    public void setRecoilZ(float recoilZ) {
        this.recoilZ = recoilZ;
    }

    public Quaternion getCurrentRotationRecoil() {
        return currentRotationRecoil;
    }

    public void setCurrentRotationRecoil(Quaternion currentRotationRecoil) {
        this.currentRotationRecoil = currentRotationRecoil;
    }

    public Quaternion getTargetRotationRecoil() {
        return targetRotationRecoil;
    }

    public void setTargetRotationRecoil(Quaternion targetRotationRecoil) {
        this.targetRotationRecoil = targetRotationRecoil;
    }

    public Vector3f getCurrentVectorRecoil() {
        return currentVectorRecoil;
    }

    public void setCurrentVectorRecoil(Vector3f currentVectorRecoil) {
        this.currentVectorRecoil = currentVectorRecoil;
    }

    public Vector3f getTargetVectorRecoil() {
        return targetVectorRecoil;
    }

    public void setTargetVectorRecoil(Vector3f targetVectorRecoil) {
        this.targetVectorRecoil = targetVectorRecoil;
    }

    @Override
    protected void controlUpdate(float tpf) {
        recoilUpdate(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }
    
}
