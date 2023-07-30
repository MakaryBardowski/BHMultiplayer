package game.effects;

import client.ClientGameAppState;
import client.Main;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.effects.particleStrategies.DroppedItemParticleMovementStrategy;
import game.effects.particleStrategies.GoreParticleMovementStrategy;
import game.effects.particleStrategies.ParticleMovementStrategy;
import game.items.Item;
import java.util.List;
import java.util.Random;

public class ParticleUtils {

    private static final Random RANDOM = new Random();

    public static void spawnGorePhysicalParticleShaded(Node particleNode, Vector3f initialPos) {
        setMaterialForShadedParticles(particleNode);
        attachParticleNode(particleNode);
        moveParticleToSpawnpoint(particleNode, initialPos);
        float finalY = 4f;
        GoreParticleMovementStrategy strategy = createGoreParticleStrategy(particleNode, generateRandomVelocity(), generateRandomRotationalVelocity(), finalY);
        createParticle(particleNode, strategy);
    }

    public static void spawnItemPhysicalParticleShaded(Node particleNode, Vector3f initialPos,Item i) {
        setMaterialForShadedParticles(particleNode);
        attachParticleNode(particleNode);
        moveParticleToSpawnpoint(particleNode, initialPos);
        float finalY = 4f;
        DroppedItemParticleMovementStrategy strategy = createDroppedItemParticleStrategy(particleNode, generateRandomVelocity(), generateRandomRotationalVelocity(), finalY,i);
        createParticle(particleNode, strategy);
    }

    private static void setMaterialForShadedParticles(Node particleNode) {
        for (Spatial c : particleNode.getChildren()) {
            if(c != null){
            if (c instanceof Geometry g) {
                Material originalMaterial = g.getMaterial();
                Material newMaterial = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
                newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                // newMaterial.setBoolean("UseInstancing", true);
                g.setMaterial(newMaterial);
            }else if(c instanceof Node n){
            setMaterialForShadedParticles(n);
            }
            
            }
        }
    }

    private static Vector3f generateRandomVelocity() {
        float minVelocityX = -2.0f;
        float maxVelocityX = 2.0f;
        float minVelocityY = 3.0f;
        float maxVelocityY = 6.0f;
        float minVelocityZ = -2.0f;
        float maxVelocityZ = 2.0f;

        float initialVelocityX = minVelocityX + (maxVelocityX - minVelocityX) * RANDOM.nextFloat();
        float initialVelocityY = minVelocityY + (maxVelocityY - minVelocityY) * RANDOM.nextFloat();
        float initialVelocityZ = minVelocityZ + (maxVelocityZ - minVelocityZ) * RANDOM.nextFloat();

        return new Vector3f(initialVelocityX, initialVelocityY, initialVelocityZ);
    }

    private static Vector3f generateRandomRotationalVelocity() {
        float rotx = RANDOM.nextFloat() * RANDOM.nextInt(9) * (RANDOM.nextInt(3) - 1);
        float roty = RANDOM.nextFloat() * RANDOM.nextInt(9) * (RANDOM.nextInt(3) - 1);
        float rotz = RANDOM.nextFloat() * RANDOM.nextInt(9) * (RANDOM.nextInt(3) - 1);

        Vector3f rotVelocity = new Vector3f(rotx * 3, roty * 3, rotz * 3);

        float rotMult = 1;
        rotVelocity.multLocal(rotMult);

        return rotVelocity;
    }

    private static GoreParticleMovementStrategy createGoreParticleStrategy(Node particleNode, Vector3f velocity, Vector3f rotVelocity, float finalY) {
        return new GoreParticleMovementStrategy(particleNode, velocity, rotVelocity, finalY);
    }

    private static DroppedItemParticleMovementStrategy createDroppedItemParticleStrategy(Node particleNode, Vector3f velocity, Vector3f rotVelocity, float finalY,Item i) {
        return new DroppedItemParticleMovementStrategy(particleNode, velocity, rotVelocity, finalY,i);
    }

    private static void attachParticleNode(Node particleNode) {
        ClientGameAppState.getInstance().getDebugNode().attachChild(particleNode);
    }

    private static void moveParticleToSpawnpoint(Node particleNode, Vector3f initialPos) {
        particleNode.setLocalTranslation(initialPos);
    }

    private static void createParticle(Node particleNode, ParticleMovementStrategy strategy) {
        PhysicalParticle particle = new PhysicalParticle(strategy);
        addControlToParticle(particleNode, particle);
    }

    private static void addControlToParticle(Node particleNode, PhysicalParticle particle) {
        particleNode.addControl(new PhysicalParticleControl(particle));
    }
}
