/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.entities.mobs;

import game.items.Equippable;
import game.items.Item;
import client.ClientGameAppState;
import com.jme3.math.Vector3f;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import game.entities.Movable;

/**
 *
 * @author 48793
 */
public interface MobInterface extends AiSteerable{

    
    public void attack();

    public void equip(Item e);

    public void unequip(Item e);

    public void equipServer(Item e);
    
    public void unequipServer(Item e);
    
    
}
