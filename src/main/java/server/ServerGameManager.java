/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import game.entities.InteractiveEntity;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public abstract class ServerGameManager implements GameManagerInterface{
    protected int gamemodeId;
    
    @Getter
    protected ServerLevelManager levelManager;

}
