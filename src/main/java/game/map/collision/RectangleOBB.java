package game.map.collision;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;


/**
 *
 * @author 48793
 */
public class RectangleOBB extends CollisionShape {

    private float rotation;
    private Vector2f axis1;
    private Vector2f axis2;
    private final Vector2f[] vertices = new Vector2f[4];

    public RectangleOBB(Vector3f position, float width, float height, float length, float rotation) {
        super(position);
        this.width = width;
        this.height = height;
        this.length = length;
        this.rotation = rotation;
        calculateVertices();
        calculateAxes();
    }

    @Override
    public boolean wouldCollideAtPosition(CollisionShape other, Vector3f newPos) {
        if (other == this) {
            return false;
        }

        if (other instanceof RectangleOBB) {
            RectangleOBB r = (RectangleOBB) other;
            return wouldCollideAtPositionOBB(r, newPos);
        }

        RectangleOBB otherCastToOBB = new RectangleOBB(other.getPosition(), other.width, other.height, other.length, 0);
        return wouldCollideAtPositionOBB(otherCastToOBB, newPos);
    }

    private boolean wouldCollideAtPositionOBB(RectangleOBB obb, Vector3f newPos) {
        Vector3f actualPos = position.clone();

        position.set(newPos);
        calculateVertices();

        Vector2f[] axesToTest = {
            axis1, axis2,
            obb.axis1, obb.axis2};

        for (Vector2f axis : axesToTest) {
            if (!overlapOnAxis(this, obb, axis)) {
                position.set(actualPos);
                calculateVertices();
                return false;
            }
        }
        position.set(actualPos);
        calculateVertices();
        return true;
    }

    private boolean overlapOnAxis(RectangleOBB b1, RectangleOBB b2, Vector2f axis) {
        Vector2f interval1 = getInterval(b1, axis);
        Vector2f interval2 = getInterval(b2, axis);
        return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
    }

    private Vector2f getInterval(RectangleOBB rect, Vector2f axis) {
        Vector2f result = new Vector2f(0, 0);

        Vector2f[] vertices = rect.getVertices();

        result.x = axis.dot(vertices[0]);
        result.y = result.x;

        for (int i = 1; i < 4; i++) {
            float projection = axis.dot(vertices[i]);
            if (projection < result.x) {
                result.x = projection;
            }
            if (projection > result.y) {
                result.y = projection;
            }
        }

        return result;
    }

    public final void calculateAxes() { // to be recalculated on rotation
        axis1 = new Vector2f(0, 1);
        rotate(axis1, rotation, new Vector2f());
        axis2 = new Vector2f(1, 0);
        rotate(axis2, rotation, new Vector2f());
    }

    public final Vector2f[] calculateVertices() { // recalculated on movement or rotation
        Vector2f min = getMin();
        Vector2f max = getMax();

        vertices[0] = new Vector2f(min.x, min.y);
        vertices[1] = new Vector2f(min.x, max.y);
        vertices[2] = new Vector2f(max.x, min.y);
        vertices[3] = new Vector2f(max.x, max.y);

        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                rotate(vert, rotation, getPos2D());
            }
        }

        return vertices;
    }

    private Vector2f getPos2D() {
        return new Vector2f(position.getX(), position.getZ());
    }

    private Vector2f getMin() {
        Vector2f pos = getPos2D();
        return pos.subtract(new Vector2f(width, length));
    }

    private Vector2f getMax() {
        Vector2f pos = getPos2D();
        return pos.add(new Vector2f(width, length));
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
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

    public Vector2f[] getVertices() {
        return vertices;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

}
