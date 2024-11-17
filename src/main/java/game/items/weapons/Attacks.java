/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package game.items.weapons;

import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;

/**
 *
 * @author tomasz potoczko
 */
public interface Attacks {
    void attack(Mob m);
    void playerAttack(Player p);
}
