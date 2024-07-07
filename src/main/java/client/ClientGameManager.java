/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;
import lombok.Getter;
import server.GameManagerInterface;
/**
 *
 * @author 48793
 */
public abstract class ClientGameManager implements GameManagerInterface {
    @Getter
    protected ClientLevelManager levelManager;
}
