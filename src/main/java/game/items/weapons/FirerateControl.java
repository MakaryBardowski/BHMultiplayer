/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package game.items.weapons;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class FirerateControl extends AbstractControl {

    private RangedWeapon weapon;

    public FirerateControl(RangedWeapon weapon) {
        this.weapon = weapon;
    }

    @Override
    protected void controlUpdate(float tpf) {
        weapon.setCurrentAttackCooldown(weapon.getCurrentAttackCooldown() + tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial == null) {
            weapon = null;
        }
    }

    public Control cloneForSpatial(Spatial spatial) {
        FirerateControl control = new FirerateControl(weapon);
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);

    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);

    }

}
