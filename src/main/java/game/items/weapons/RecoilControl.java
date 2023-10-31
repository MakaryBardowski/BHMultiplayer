/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;

/**
 *
 * @author tomas
 */
public class RecoilControl extends AbstractControl {
    protected Random random = new Random();
    protected static final float RECOIL_SPEED = 0.016f;
    protected float upRecoil = -.6f;
    protected float recoilY = .3f;
    protected float recoilZ = .1f;
    int snap = 20;
    float kickback;
    Quaternion currentRotationRecoil = new Quaternion(0, 0, 0, 1);
    Quaternion targetRotationRecoil = new Quaternion(0, 0, 0, 1);
    Vector3f currentVectorRecoil = new Vector3f(0, 0, 0);
    Vector3f targetVectorRecoil = new Vector3f(0, 0, 0);

    public RecoilControl(float kickback, float recoilX, float recoilY, float recoilZ) {
        this.kickback = kickback;
        this.upRecoil = recoilX;
        this.recoilY = recoilY;
        this.recoilZ = recoilZ;
    }

    // better recoil for any type of weapon
    public void recoilUpdate() {
        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED * snap);
        currentRotationRecoil.slerp(targetRotationRecoil, RECOIL_SPEED * snap);
        targetVectorRecoil.interpolateLocal(new Vector3f(0, 0, 0), RECOIL_SPEED * snap);
        currentVectorRecoil.interpolateLocal(targetVectorRecoil, RECOIL_SPEED * snap);

        spatial.setLocalRotation(currentRotationRecoil);
        spatial.setLocalTranslation(currentVectorRecoil);

    }

    public void recoilFire() {
        targetRotationRecoil = targetRotationRecoil.add((new Quaternion()).fromAngleAxis(FastMath.PI / (6 - kickback), new Vector3f(upRecoil, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
        targetVectorRecoil = targetVectorRecoil.subtract(targetRotationRecoil.getRotationColumn(2).subtract(0, 0, 0.4f));
    }

    public float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public float getUpRecoil() {
        return upRecoil;
    }

    public void setUpRecoil(float upRecoil) {
        this.upRecoil = upRecoil;
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
        recoilUpdate();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
