/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.anim.tween.AbstractTween;
import com.jme3.anim.tween.action.BaseAction;

/**
 *
 * @author 48793
 */
public class InvokeMethodTween extends AbstractTween {
    private final Runnable runnable;
    public InvokeMethodTween(Runnable runnable) {
        super(0);
        this.runnable = runnable;
    }

    @Override
    protected void doInterpolate(double d) {
        runnable.run();
    }
}
