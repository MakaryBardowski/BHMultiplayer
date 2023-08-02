/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.mobs;

import com.jme3.scene.Node;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public abstract class Destructible extends InteractiveEntity {
    protected float health = 10;
    protected float maxHealth = 10;
    
    public Destructible(int id,String name, Node node){
    super(id,name,node);
    }

}
