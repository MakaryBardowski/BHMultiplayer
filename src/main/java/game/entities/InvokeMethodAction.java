/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.action.BaseAction;

/**
 *
 * @author 48793
 */
public class InvokeMethodAction extends BaseAction {

    private final double normalizedTimeOfAnimationToInvokeRunnable;
    private final Runnable runnable;
    private boolean ran;

    public InvokeMethodAction(Tween tween, Runnable runnable, double normalizedTimeOfAnimationToInvokeRunnable) {
        super(tween);
        this.runnable = runnable;
        this.normalizedTimeOfAnimationToInvokeRunnable = normalizedTimeOfAnimationToInvokeRunnable;
    }

    @Override
    public boolean interpolate(double t) {
        boolean running = super.interpolate(t);
        if (t >= normalizedTimeOfAnimationToInvokeRunnable && !ran) {
            ran = true;
            runnable.run();
        }
        return running;
    }
}
