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
import game.entities.mobs.Mob;
import game.entities.mobs.Player;
import java.io.IOException;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
public class SlashControl extends AbstractControl {

    private MeleeWeapon weapon;
    private Mob wielder;

    private float time;
    private float slashDelay;
    private boolean waitingToSlash;

    private boolean slashedAndHit;
    private float slowTime;
    private float slowMaxTime;

    public SlashControl(MeleeWeapon weapon, Mob wielder) {
        this.weapon = weapon;
        this.wielder = wielder;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (slashedAndHit) {
            slowTime+=tpf;
            if (slowTime >= slowMaxTime) {
                weapon.composer.getCurrentAction().setSpeed(1.25f);
                slowTime = 0;
                slashedAndHit = false;
            }
        } else if (waitingToSlash) {

            time += tpf;
            if (time >= slashDelay) {
                if (wielder instanceof Player p) {
                    weapon.slashPlayer(p);
                } else {
                    weapon.slashMob(wielder);
                }
                time = 0;
                waitingToSlash = false;
            }
        }
    }

    public void setSlashDelay(float slashDelay) {
        this.slashDelay = slashDelay;
        waitingToSlash = true;
    }

    public float getSlowMaxTime() {
        return slowMaxTime;
    }

    public void setSlowMaxTime(float slowMaxTime) {
        this.slowMaxTime = slowMaxTime;
        slashedAndHit = true;
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
        SlashControl control = new SlashControl(weapon, wielder);
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
