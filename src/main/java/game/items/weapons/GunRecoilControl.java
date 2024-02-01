/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.items.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import game.cameraAndInput.ImprovedNoise;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class GunRecoilControl extends RecoilControl {

    private static Random random = new Random();
    private float xperlin = 0;
    private float yperlin = 0;
    private float shakeStrength = 1;

    public GunRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ) {
        super(kickback, recoilX, recoilY, recoilZ, 20);
    }

    public GunRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, int snap) {
        super(kickback, recoilX, recoilY, recoilZ, snap);
    }

    public GunRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, int snap, float shakeStrength) {
        super(kickback, recoilX, recoilY, recoilZ, snap);
        this.shakeStrength = shakeStrength;
    }

    @Override
    protected void controlUpdate(float tpf) {
        this.recoilUpdate();
    }

    @Override
    public void recoilUpdate() {

        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED * snap);
        currentRotationRecoil.slerp(targetRotationRecoil, RECOIL_SPEED * snap);
        targetVectorRecoil.interpolateLocal(new Vector3f(0, 0, 0), RECOIL_SPEED * snap);
        currentVectorRecoil.interpolateLocal(targetVectorRecoil, RECOIL_SPEED * snap);

        spatial.setLocalRotation(currentRotationRecoil);
        spatial.setLocalTranslation(currentVectorRecoil);
    }

    @Override
    public void recoilFire() {

        xperlin += random.nextFloat(0.05f, 0.11f);
        yperlin += random.nextFloat(0.05f, 0.8f);
        var xOffset = (float) ImprovedNoise.noise(xperlin, yperlin);

        xperlin += random.nextFloat(0.12f, 0.14f);
        yperlin += random.nextFloat(0.02f, 0.4f);
        var yOffset = (float) ImprovedNoise.noise(xperlin, yperlin);
        var yScale = 0.9f;
        var xScale = 0.9f;
        
        xOffset = ((xOffset - 0.5f) * 2) * xScale*shakeStrength;
        yOffset = ((yOffset - 0.5f) * 2) * yScale*shakeStrength;

        targetRotationRecoil = targetRotationRecoil.add((new Quaternion()).fromAngleAxis(FastMath.PI / (6 - kickback), new Vector3f(recoilX, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
        targetVectorRecoil = targetVectorRecoil.subtract(targetRotationRecoil.getRotationColumn(2).subtract(xOffset, yOffset, -backRecoil));

    }

    //    @Override
//    public void recoilUpdate() {
//        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED*snap*.4f);
//        currentRotationRecoil.slerp(targetRotationRecoil, RECOIL_SPEED * snap);
//        spatial.setLocalRotation(currentRotationRecoil);
//    }
///    @Override
////    public void recoilFire(){
////        targetRotationRecoil.addLocal((new Quaternion()).fromAngleAxis(FastMath.PI / (20-kickback), new Vector3f(recoilX*4, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
////    }
}
