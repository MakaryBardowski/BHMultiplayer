package debugging;

import client.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

/**
 *
 * @author 48793
 */
public class Circle extends Mesh {

    /**
     * The center.
     *
     */
    private Vector3f center;

    /**
     * The radius.
     *
     */
    private float radius;

    /**
     * The samples.
     *
     */
    private int samples;

    /**
     * Constructs a new instance of this class.
     *
     *
     * @param radius
     *
     */
    public Circle(float radius) {

        this(Vector3f.ZERO, radius, 16);

    }

    /**
     * Constructs a new instance of this class.
     *
     *
     * @param radius
     * @param samples
     *
     */
    public Circle(float radius, int samples) {

        this(Vector3f.ZERO, radius, samples);

    }

    /**
     * Constructs a new instance of this class.
     *
     *
     * @param center
     * @param radius
     * @param samples
     *
     */
    public Circle(Vector3f center, float radius, int samples) {

        super();

        this.center = center;

        this.radius = radius;

        this.samples = samples;

        setMode(Mesh.Mode.Lines);

        updateGeometry();

    }

    protected void updateGeometry() {

        FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);

        FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);

        short[] indices = new short[samples * 2];

        float rate = FastMath.TWO_PI / (float) samples;

        float angle = 0;

        for (int i = 0; i < samples; i++) {

            float x = FastMath.cos(angle) + center.x;

            float z = FastMath.sin(angle) + center.z;

            positions.put(x * radius).put(center.y).put(z * radius);

            normals.put(new float[]{0, 1, 0});

            indices[i] = (short) i;

            if (i < samples - 1) {
                indices[i] = (short) (i + 1);
            } else {
                indices[i] = 0;
            }

            angle += rate;

        }

        setBuffer(VertexBuffer.Type.Position, 3, positions);

        setBuffer(VertexBuffer.Type.Normal, 3, normals);

        setBuffer(VertexBuffer.Type.Index, 2, indices);

        setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0, 1, 1});

        updateBound();
    }
    
        public static Geometry createCircle(float radius, ColorRGBA color) {
        Circle b = new Circle(radius, 60);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);

        return geom;
    }
}
