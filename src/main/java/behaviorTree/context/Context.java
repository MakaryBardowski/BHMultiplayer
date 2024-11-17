/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.context;

import events.EventSubscriber;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 48793
 */
public abstract class Context extends EventSubscriber{
    protected final Map<Integer,Object> blackboard = new HashMap<>();

    public Map<Integer, Object> getBlackboard() {
        return blackboard;
    }
    
    public abstract void shutdown();
    
}
