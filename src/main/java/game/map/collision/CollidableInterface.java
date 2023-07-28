/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.collision;

import java.util.HashSet;

/**
 *
 * @author 48793
 */
public interface CollidableInterface {
    
    
    public void insert(WorldGrid wg); //insert object into the worldGrid
        public void removeFromGrid(WorldGrid wg);
    public HashSet<CollidableInterface> getFromCellsImIn(WorldGrid wg);
    public HashSet<CollidableInterface> getEntitiesFromTilesInRange(WorldGrid wg,float distance);

    
    public void checkCollision();
    public void onCollision();
    
}
