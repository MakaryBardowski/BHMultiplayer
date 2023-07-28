/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import com.jme3.math.Vector2f;

/**
 *
 * @author 48793
 */
public class BlockType {

    public static final BlockType STONE = new BlockType("Textures/Blocks/testRoomTile.png", new Vector2f(0, 0.0f), new Vector2f(0.5f, 0.5f));
    public static final BlockType DIRT = new BlockType("Textures/Blocks/mcdirt.png", new Vector2f(0.5f, 0.0f), new Vector2f(1f, 0.5f));

    public String textureName;
    public Vector2f minTexCoord;
    public Vector2f maxTexCoord;

    public BlockType(String s, Vector2f min, Vector2f max) {
        textureName = s;
        minTexCoord = min;
        maxTexCoord = max;
    }

}
