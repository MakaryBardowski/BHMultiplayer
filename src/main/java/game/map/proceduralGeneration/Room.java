/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.map.proceduralGeneration;

import com.jme3.math.Vector3f;
import java.util.List;
import java.util.Random;
import jme3utilities.math.Vector3i;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author tomasz_potoczko
 */

@Getter
@AllArgsConstructor
public class Room{
    private int startX,startY,startZ;
    private int endX,endY,endZ;  
}
