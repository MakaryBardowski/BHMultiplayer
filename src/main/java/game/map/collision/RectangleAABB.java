package game.map.collision;

import com.jme3.math.Vector3f;

/**
 *
 * @author 48793
 */
public class RectangleAABB extends CollisionShape {



    public RectangleAABB(Vector3f position, float width, float height, float length) {
        super(position);
        this.width = width;
        this.height = height;
        this.length = length;
    }

    @Override
    public boolean wouldCollideAtPosition(CollisionShape other,Vector3f newPos) {
        if (other == this) {
            return false;
        }
        if (other instanceof RectangleAABB) {
            return wouldCollideAtPositionAABB((RectangleAABB) other,newPos);
        }

        return wouldCollideAtPositionOBB((RectangleOBB) other,newPos);

    }

  public boolean wouldCollideAtPositionAABB(RectangleAABB other, Vector3f newPos) {
        if (other == this) {
            return false;
        }

        Vector3f otherPosition = other.position;

        float tMinX = newPos.getX() - width;
        float tMaxX = newPos.getX() + width;
        float tMinY = newPos.getY() - height;
        float tMaxY = newPos.getY() + height;
        float tMinZ = newPos.getZ() - length;
        float tMaxZ = newPos.getZ() + length;

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
    

    private boolean wouldCollideAtPositionOBB(RectangleOBB r,Vector3f newPos) {
        var thisOBB = new RectangleOBB(newPos, width, height, length, 0);
        return r.wouldCollideAtPosition(thisOBB,r.getPosition());
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Rectangle{" + "width=" + width + ", height=" + height + ", length=" + length + '}';
    }

}
