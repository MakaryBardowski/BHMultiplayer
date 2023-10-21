/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.scene.Node;
import game.entities.mobs.Damageable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public abstract class Destructible extends Collidable implements Damageable{

    protected float health = 12;
    protected float maxHealth = 12;


    public Destructible(int id,String name, Node node) {
        super(id,name, node);
    }
    
    protected abstract void createHitbox();
    public void onDeathServer(){};

}
