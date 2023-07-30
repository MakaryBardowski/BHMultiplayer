/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.effects.particleStrategies;

import com.jme3.scene.Node;
import game.effects.PhysicalParticleControl;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@AllArgsConstructor
public abstract class ParticleMovementStrategy {

    @Getter
    protected final Node node;

    public abstract void updateParticle(float tpf);
    
    public abstract void move(float tpf);

    public abstract void onHitGround();

    protected abstract boolean hasNotHitGround();

    protected void removeControl() {
        node.removeControl(PhysicalParticleControl.class);
    }

}
