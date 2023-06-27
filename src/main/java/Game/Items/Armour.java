/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Items;

import Game.Mobs.Mob;
import java.util.HashMap;

/**
 * 
 * @author tomasz potoczko
 */
public abstract class Armour extends Item{
    float damageReduction;
    
    public void setDamageReduction(float damageReduction){
        this.damageReduction = damageReduction;
    }
}
