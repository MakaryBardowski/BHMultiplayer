/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.items.weapons;

import game.mobs.Mob;
import game.mobs.Player;

/**
 *
 * @author tomasz potoczko
 */
public interface Attacks {
    void attack(Mob m);
    void playerAttack(Player p);
}
