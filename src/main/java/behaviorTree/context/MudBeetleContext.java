/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.context;

import com.jme3.math.Vector3f;
import events.GameEvent;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import pathfinding.Node;

/**
 *
 * @author 48793
 */
public class MudBeetleContext extends Context {

    public static int STEERED_MUD_BEETLE = 0;
    public static int INTERRUPT_WALKING = 1;
    public static int TARGET_DESTRUCTIBLE = 2;
    public static int CURRENT_PATH = 3;
    public static int LAST_KNOWN_TARGET_POSITION = 4;

    private Future pathfindingFuture;
    private float findRandomPathCooldown = 6;
    private float findRandomPathTimer = new Random().nextFloat() * findRandomPathCooldown;
    private int currentNodeIndex = 0;
    private boolean shouldPathfindToLastTarget = false;
    private boolean hasLookedAtNextPathNode = false;

    public MudBeetleContext(MudBeetle mob) {
        blackboard.put(STEERED_MUD_BEETLE, mob);
        blackboard.put(INTERRUPT_WALKING, false);
        blackboard.put(TARGET_DESTRUCTIBLE, null);
        blackboard.put(CURRENT_PATH, null);

    }

    public Destructible getCurrentTarget() {
        return (Destructible) blackboard.get(TARGET_DESTRUCTIBLE);
    }

    public MudBeetle getMudBeetle() {
        return (MudBeetle) blackboard.get(STEERED_MUD_BEETLE);
    }

    public List<Vector3f> getCurrentPath() {
        return (List<Vector3f>) blackboard.get(CURRENT_PATH);
    }

    public void setCurrentPath(List<Vector3f> path) {
        blackboard.put(CURRENT_PATH, path);
    }

    public Vector3f getLastKnownTargetPosition() {
        return (Vector3f) blackboard.get(LAST_KNOWN_TARGET_POSITION);
    }

    public void setLastKnownTargetPosition(Vector3f pos) {
        blackboard.put(LAST_KNOWN_TARGET_POSITION, pos);
    }

    public Future getPathfindingFuture() {
        return pathfindingFuture;
    }

    public void setPathfindingFuture(Future pathfindingFuture) {
        this.pathfindingFuture = pathfindingFuture;
    }

    public float getFindRandomPathTimer() {
        return findRandomPathTimer;
    }

    public void setFindRandomPathTimer(float findRandomPathTimer) {
        this.findRandomPathTimer = findRandomPathTimer;
    }

    public float getFindRandomPathCooldown() {
        return findRandomPathCooldown;
    }

    public int getCurrentNodeIndex() {
        return currentNodeIndex;
    }

    public void setCurrentNodeIndex(int currentNodeIndex) {
        this.currentNodeIndex = currentNodeIndex;
    }

    public void resetPath() {

        setCurrentNodeIndex(0);
        setCurrentPath(null);
        setPathfindingFuture(null);
    }

    public boolean isShouldPathfindToLastTarget() {
        return shouldPathfindToLastTarget;
    }

    public void setShouldPathfindToLastTarget(boolean shouldPathfindToLastTarget) {
        this.shouldPathfindToLastTarget = shouldPathfindToLastTarget;
    }

    public boolean hasLookedAtCurrentNode() {
        return hasLookedAtNextPathNode;
    }

    public void setLookedAtCurrentNode(boolean b) {
        hasLookedAtNextPathNode = b;
    }

    public void setTargetDestructible(Destructible d) {
        blackboard.put(MudBeetleContext.TARGET_DESTRUCTIBLE, d);
    }

    public Destructible getTargetDestructible(){
    return (Destructible) blackboard.get(MudBeetleContext.TARGET_DESTRUCTIBLE);
    }

    @Override
    public void receiveEventNotification(GameEvent gameEvent) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void shutdown() {
        if(getPathfindingFuture() != null){
            getPathfindingFuture().cancel(true);
        }
    }
    
}
