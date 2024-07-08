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
 * @author tomas
 */
public class CameraRecoilControl extends RecoilControl {
    private float xperlin = 6;
    private float yperlin = 6;
    private float shakeStrength;
    public CameraRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ) {
        super(kickback, recoilX, recoilY, recoilZ, 20);
    }

    public CameraRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, int snap) {
        super(kickback, recoilX, recoilY, recoilZ, snap);
    }

    public CameraRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, int snap,float shakeStrength) {
        super(kickback, recoilX, recoilY, recoilZ, snap);
        this.shakeStrength = shakeStrength;
    }

    @Override
    protected void controlUpdate(float tpf) {
        this.recoilUpdate();
    }

    @Override
    public void recoilUpdate() {

        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED * snap * .4f);
        currentRotationRecoil.nlerp(targetRotationRecoil, RECOIL_SPEED * snap);
        spatial.setLocalRotation(currentRotationRecoil);
    }

    @Override
    public void recoilFire() {

        recoilX = FastMath.PI / (40 + random.nextFloat(10));

        recoilY = (float) ImprovedNoise.noise(xperlin, yperlin);
        recoilZ = (float) ImprovedNoise.noise(xperlin, yperlin);

        xperlin = random.nextFloat(-1, 1) * 30;
        yperlin = random.nextFloat(-1, 1) * 30;

        float scale = 0.05f;
        recoilY = recoilY * scale * shakeStrength; // left right
        recoilX = recoilX * shakeStrength;
//        recoilZ = recoilZ * 0.005f; // rotates head 

//        targetRotationRecoil.addLocal((new Quaternion()).fromAngleAxis(FastMath.PI / (20-kickback), new Vector3f(recoilX*4, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
        targetRotationRecoil.addLocal(
                (new Quaternion()).fromAngleAxis(
                        -recoilX,
                        new Vector3f(recoilX,
                                recoilY,
                                0
                        )));

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
