/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.entities;

import com.jme3.scene.Node;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
@Getter
@Setter
public abstract class Destructible extends Collidable {

    protected static final int HEALTH_ATTRIBUTE = 0;
    protected static final int MAX_HEALTH_ATTRIBUTE = 1;

    protected float health = 12;
    protected float maxHealth = 12;

    public Destructible(int id, String name, Node node) {
        super(id, name, node);
        attributes.put(HEALTH_ATTRIBUTE, new FloatAttribute(health));
        attributes.put(MAX_HEALTH_ATTRIBUTE, new FloatAttribute(maxHealth));
    }

    public void onDeathServer() {};
    public void onDeathClient() {};

    public abstract void receiveDamage(float rawDamage);
    
    public void receiveHeal(float heal) {
        health += heal;
    }

    public abstract void die();

    public abstract float getArmorValue();

    public abstract float calculateDamage(float damage);
}