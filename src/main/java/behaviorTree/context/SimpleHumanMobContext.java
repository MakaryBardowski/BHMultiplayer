/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package behaviorTree.context;

import client.ClientGameAppState;
import client.Main;
import com.jme3.math.Vector3f;
import data.DamageReceiveData;
import events.DamageReceivedEvent;
import events.GameEvent;
import game.entities.Destructible;
import game.entities.mobs.HumanMob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;
import pathfinding.Node;
import server.ServerMain;

/**
 *
 * @author 48793
 */
public class SimpleHumanMobContext extends Context {

    public static int STEERED_HUMAN = 0;
    public static int INTERRUPT_WALKING = 1;
    public static int TARGET_DESTRUCTIBLE = 2;
    public static int CURRENT_PATH = 3;
    public static int LAST_KNOWN_TARGET_POSITION = 4;

    private Future pathfindingFuture;
    private final float findRandomPathCooldown = 6;
    private float findRandomPathTimer = new Random().nextFloat() * findRandomPathCooldown;
    private int currentNodeIndex = 0;
    private boolean shouldPathfindToLastTarget = false;

    @Getter
    @Setter
    private Vector3f desiredLookDirection = null;
    
    
    @Getter
    @Setter
    private long currentUpdateTimestamp = System.currentTimeMillis();

    private final int changeTargetCooldownMillis = 5000;

    @Getter
    private long lastTargetChangeTimestampMillis = currentUpdateTimestamp - changeTargetCooldownMillis;

    private long lastLoweredAggroTimestamp = 0;
    private final float lowerAggroEveryMillis = 1000;
    private final float aggroPointsFallofPerSecond = 1;

    public SimpleHumanMobContext(HumanMob mob) {
        blackboard.put(STEERED_HUMAN, mob);
        blackboard.put(INTERRUPT_WALKING, false);
        blackboard.put(TARGET_DESTRUCTIBLE, null);
        blackboard.put(CURRENT_PATH, null);

    }

    // makes the mob forget what it was doing.
    public void resetVariables() {
        blackboard.put(INTERRUPT_WALKING, false);
        blackboard.put(TARGET_DESTRUCTIBLE, null);
        blackboard.put(CURRENT_PATH, null);
        setFindRandomPathTimer(findRandomPathCooldown);
        resetPath();
    }

    public Destructible getCurrentTarget() {
        return (Destructible) blackboard.get(TARGET_DESTRUCTIBLE);
    }

    public HumanMob getHumanMob() {
        return (HumanMob) blackboard.get(STEERED_HUMAN);
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
        setLastKnownTargetPosition(null);
    }

    public boolean isShouldPathfindToLastTarget() {
        return shouldPathfindToLastTarget;
    }

    public void setShouldPathfindToLastTarget(boolean shouldPathfindToLastTarget) {
        this.shouldPathfindToLastTarget = shouldPathfindToLastTarget;
    }

    public void setTargetDestructible(Destructible d) {
        if (d == null) {
            blackboard.put(SimpleHumanMobContext.TARGET_DESTRUCTIBLE, null);
            return;
        }
        if (d == getCurrentTarget()) {
            return;
        }
        lastTargetChangeTimestampMillis = currentUpdateTimestamp;
        blackboard.put(SimpleHumanMobContext.TARGET_DESTRUCTIBLE, d);
    }

    public Destructible getTargetDestructible() {
        return (Destructible) blackboard.get(SimpleHumanMobContext.TARGET_DESTRUCTIBLE);
    }

    public boolean canAcquireNewTarget() {
        return currentUpdateTimestamp - lastTargetChangeTimestampMillis > changeTargetCooldownMillis;
    }

    public Float calculateThreatLevel(DamageReceiveData dre) {
        return dre.getRawDamage();
    }

    @Override
    public void receiveEventNotification(GameEvent gameEvent) {
        if (gameEvent instanceof DamageReceivedEvent dre) {
            handleDamageReceivedEvent(dre);
        }
    }

    @Override
    public void shutdown() {
        if (getPathfindingFuture() != null) {
            getPathfindingFuture().cancel(true);
        }
    }

    public void handleDamageReceivedEvent(DamageReceivedEvent dre) {
        var attacker = ServerMain.getInstance().getLevelManagerMobs().get(dre.getDamageData().getAttackerId());
        if (attacker == null || !(attacker instanceof Destructible destructibleTarget)) {
            return;
        }

        if (!canAcquireNewTarget()) {
            System.out.println(getHumanMob() + " cannot yet switch target. Current delta "
                    + (currentUpdateTimestamp - lastTargetChangeTimestampMillis) + "ms/" + changeTargetCooldownMillis + "ms"
            );
            return;
        }
        lastTargetChangeTimestampMillis = currentUpdateTimestamp;
        
        var attackerPos = attacker.getNode().getWorldTranslation();
        resetVariables();
        setFindRandomPathTimer(findRandomPathCooldown);
        setTargetDestructible(destructibleTarget);
        getHumanMob().getNode().lookAt(attackerPos, Vector3f.UNIT_Y);
        System.out.println(getHumanMob() + " switches target to (because received damage from): " + attacker);
    }
}
