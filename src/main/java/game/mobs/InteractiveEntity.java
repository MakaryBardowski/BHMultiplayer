/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.mobs;

import com.jme3.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@AllArgsConstructor
@Getter
public abstract class InteractiveEntity {
    protected int id;
    @Setter
    protected String name;
    protected Node node;
    
    public abstract void onShot(Mob shooter,float damage);
    public abstract void onInteract();
    
}
