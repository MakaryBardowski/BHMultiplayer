/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Game.Mobs;

import com.Networking.Client.ClientGamAppState;

/**
 *
 * @author 48793
 */
public interface MobInterface {
    public void move(float tpf, ClientGamAppState cm);
    public void die();
    public void attack();
    public void receiveDamage();
    public void dealDamage();
}