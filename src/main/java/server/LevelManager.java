/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package server;

import game.map.MapType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author 48793
 */
public abstract class LevelManager {

    @Getter
    @Setter
    protected long[] levelSeeds;

    @Getter
    @Setter
    protected MapType[] levelTypes;
    
    @Getter
    protected int currentLevelIndex;

    public abstract void jumpToLevel(int levelIndex);
    }
