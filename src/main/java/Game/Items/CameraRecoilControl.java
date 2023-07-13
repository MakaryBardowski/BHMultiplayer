/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Items.RecoilControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author tomas
 */
public class CameraRecoilControl extends RecoilControl{

    public CameraRecoilControl(float kickback) {
        super(kickback);  
    }

    
    @Override
    protected void controlUpdate(float tpf) {
        this.recoilUpdate(tpf);
    }
    
    public void recoilUpdate(float tpf) {
        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), tpf*snap*.4f);
        currentRotationRecoil.slerp(targetRotationRecoil, tpf * snap);
        
        spatial.setLocalRotation(currentRotationRecoil);

    }
    
    @Override
    public void recoilFire(){
        targetRotationRecoil = targetRotationRecoil.add((new Quaternion()).fromAngleAxis(FastMath.PI / (20-kickback), new Vector3f(recoilX*4, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
    }
}
