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
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author tomas
 */
@Getter
@Setter
public class NewGunRecoilControl extends AbstractControl {

    protected Random random = new Random();
    protected static final float tpf = 0.016f;

    protected Quaternion currentRotationRecoil = new Quaternion(0, 0, 0, 1);
    protected Quaternion targetRotationRecoil = new Quaternion(0, 0, 0, 1);

    protected float upRecoil = 0f;
    protected float horizontalRecoil = 0f;
    protected float rollRecoil = 0f;

    protected float snappiness; // 30 feels strong
    protected float returnSpeed;

    protected Vector3f currentVectorRecoil = new Vector3f(0, 0, 0);
    protected Vector3f targetVectorRecoil = new Vector3f(0, 0, 0);

    protected float backRecoil;


    protected float rotationReturnSpeed = 6f;

    protected float translationReturnSpeed = 1f;

    protected float translationSpeed = 1f;

    public NewGunRecoilControl(float upRecoil, float horizontalRecoil, float rollRecoil, int snappiness, float backRecoil) {
        this.upRecoil = upRecoil;
        this.horizontalRecoil = horizontalRecoil;
        this.rollRecoil = rollRecoil;
        this.snappiness = snappiness;
        this.backRecoil = backRecoil;
    }

    // better recoil for any type of weapon
    public void recoilUpdate() {
        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), tpf * rotationReturnSpeed);
        currentRotationRecoil.slerp(targetRotationRecoil, tpf * snappiness );

        targetVectorRecoil.interpolateLocal(new Vector3f(0, 0, 0), tpf * snappiness * translationReturnSpeed);
        currentVectorRecoil.interpolateLocal(targetVectorRecoil, tpf * snappiness * translationSpeed);

        spatial.setLocalRotation(currentRotationRecoil);
        spatial.setLocalTranslation(currentVectorRecoil);

    }

    public void recoilFire() {
//        targetRotationRecoil = targetRotationRecoil.add((new Quaternion()).fromAngleAxis(FastMath.PI *0.01f, 
//                        new Vector3f(
//                                upRecoil, 
//                                getRandomNumber(-horizontalRecoil, horizontalRecoil), 
//                                getRandomNumber(-rollRecoil, rollRecoil)
//                        )
//                )
//        );
        targetRotationRecoil = targetRotationRecoil.add(
                new Quaternion().fromAngles(
                        -FastMath.DEG_TO_RAD * upRecoil, 
                        getRandomNumber(-FastMath.DEG_TO_RAD * horizontalRecoil, FastMath.DEG_TO_RAD * horizontalRecoil), 
                        getRandomNumber(-FastMath.DEG_TO_RAD * rollRecoil, FastMath.DEG_TO_RAD * rollRecoil))
        );
        targetVectorRecoil = targetVectorRecoil.subtract(targetRotationRecoil.getRotationColumn(2).subtract(0, 0, -backRecoil));
    }

    public float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
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
