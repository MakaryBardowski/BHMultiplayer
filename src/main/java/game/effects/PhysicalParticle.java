/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.effects;

import game.effects.particleStrategies.ParticleMovementStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@AllArgsConstructor
public class PhysicalParticle {

    @Getter
    private final ParticleMovementStrategy strategy;
    
}
