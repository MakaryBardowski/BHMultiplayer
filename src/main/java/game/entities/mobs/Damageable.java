/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities.mobs;

/**
 *
 * @author 48793
 */
public interface Damageable {

    public void receiveDamage(float rawDamage);
    public void die();
    public float getArmorValue();
    public float calculateDamage(float damage );
}
