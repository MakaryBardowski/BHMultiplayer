/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game.Mobs;

import Game.Items.Holdable;
import Game.Map.Collision.CollidableInterface;
import Game.Map.Collision.WorldGrid;
import com.Networking.Client.ClientMain;
import com.jme3.scene.Node;
import java.util.HashSet;

/**
 *
 * @author 48793
 */
public class HumanMob extends Mob{
    protected Holdable equippedRightHand;
    protected Holdable equippedLeftHand;
    
    
    public HumanMob(int id,Node node,String name){
    super(id,node,name);
    }
    
    
    
    @Override
    public void move(float tpf, ClientMain cm) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void die() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void receiveDamage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void dealDamage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    

    @Override
    public void insert(WorldGrid wg) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void removeFromGrid(WorldGrid wg) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HashSet<CollidableInterface> getFromCellsImIn(WorldGrid wg) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public HashSet<CollidableInterface> getEntitiesFromTilesInRange(WorldGrid wg, float distance) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void checkCollision() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onCollision() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Holdable getEquippedRightHand() {
        return equippedRightHand;
    }

    public void setEquippedRightHand(Holdable equippedRightHand) {
        this.equippedRightHand = equippedRightHand;
    }

    public Holdable getEquippedLeftHand() {
        return equippedLeftHand;
    }

    public void setEquippedLeftHand(Holdable equippedLeftHand) {
        this.equippedLeftHand = equippedLeftHand;
    }
    
    

}
