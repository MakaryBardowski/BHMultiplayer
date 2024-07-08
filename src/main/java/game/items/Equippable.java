/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.items;

import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.entities.mobs.Player;

/**
 *
 * @author 48793
 */
public interface Equippable {

    public void playerEquip(Player m);

    public void playerUnequip(Player m);
    
    public void playerServerEquip(HumanMob m);
    
    public void playerServerUnequip(HumanMob m);
}
