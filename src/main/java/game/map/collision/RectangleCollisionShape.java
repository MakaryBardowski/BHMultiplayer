package game.map.collision;

import com.jme3.math.Vector3f;

/**
 *
 * @author 48793
 */
public class RectangleCollisionShape {
    
    protected Vector3f position;
    private float width;
    private float height;
    private float length;

    public RectangleCollisionShape(Vector3f position, float width, float height, float length) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public boolean collidesWith(RectangleCollisionShape other) {
        if (other == this) {
            return false;
        }

        Vector3f thisPosition = this.position;
        Vector3f otherPosition = other.position;

        float tMinX = thisPosition.getX() - width;
        float tMaxX = thisPosition.getX() + width;
        float tMinY = thisPosition.getY() - height;
        float tMaxY = thisPosition.getY() + height;
        float tMinZ = thisPosition.getZ() - length;
        float tMaxZ = thisPosition.getZ() + length;

        float oMinX = otherPosition.getX() - other.width;
        float oMaxX = otherPosition.getX() + other.width;
        float oMinY = otherPosition.getY() - other.height;
        float oMaxY = otherPosition.getY() + other.height;
        float oMinZ = otherPosition.getZ() - other.length;
        float oMaxZ = otherPosition.getZ() + other.length;

        if (tMaxX < oMinX || tMinX > oMaxX
                || tMaxY < oMinY || tMinY > oMaxY
                || tMaxZ < oMinZ || tMinZ > oMaxZ) {
            return false;
        }

        return true;
    }

    
    public boolean wouldCollideAfterMovement(RectangleCollisionShape other, Vector3f movementVector) {
        if (other == this) {
            return false;
        }

        Vector3f thisPosition = this.position.add(movementVector);
        Vector3f otherPosition = other.position;

        float tMinX = thisPosition.getX() - width;
        float tMaxX = thisPosition.getX() + width;
        float tMinY = thisPosition.getY() - height;
        float tMaxY = thisPosition.getY() + height;
        float tMinZ = thisPosition.getZ() - length;
        float tMaxZ = thisPosition.getZ() + length;

        float oMinX = otherPosition.getX() - other.width;
        float oMaxX = otherPosition.getX() + other.width;
        float oMinY = otherPosition.getY() - other.height;
        float oMaxY = otherPosition.getY() + other.height;
        float oMinZ = otherPosition.getZ() - other.length;
        float oMaxZ = otherPosition.getZ() + other.length;

        if (tMaxX < oMinX || tMinX > oMaxX
                || tMaxY < oMinY || tMinY > oMaxY
                || tMaxZ < oMinZ || tMinZ > oMaxZ) {
            return false;
        }

        return true;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
    
    public float getLength() {
        return length;
    }
        public Vector3f getPosition() {
        return position;
    }
    @Override
    public String toString() {
        return "Rectangle{" + "width=" + width + ", height=" + height + ", length=" + length + '}';
    }

}
