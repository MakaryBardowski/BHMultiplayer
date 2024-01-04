/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import client.Main;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author 48793
 */
public class Chunk {

    private List<Vector3f> positions = new ArrayList<>();
    private List<Vector2f> uvs = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();
    private List<Vector3f> normals = new ArrayList<>();
    private List<Vector4f> colors = new ArrayList<>();
    private Geometry geometry;
    private final BlockWorld bw;

    private Vector2f chunkPos;
    private int blocksCount = 0;
    private int vertexCount = 0;

    private ArrayList<Block> blocks = new ArrayList<>();

    public Chunk(BlockWorld bw, int x, int z) {
        this.bw = bw;
        chunkPos = new Vector2f(x, z);
    }

    public final Geometry generateGeometry(Mesh m) {

        geometry = new Geometry("Chunk", m);
        bw.getWorldNode().attachChild(geometry);

        geometry.move(chunkPos.getX() * bw.getBLOCK_SIZE(), 0, chunkPos.getY() * bw.getBLOCK_SIZE());

//        Material matVC = new Material(bw.getAsm(), "Common/MatDefs/Misc/Unshaded.j3md");

//        Texture t = bw.getAsm().loadTexture(BlockType.STONE.textureName);
//        t.setMagFilter(Texture.MagFilter.Nearest);
//        matVC.setTexture("ColorMap", t);
//        matVC.setBoolean("VertexColor", true);
//        geometry.setMaterial(matVC);

        Material mat = new Material(bw.getAsm(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", bw.getTextureAtlas().getAtlasTexture("DiffuseMap"));
        mat.getTextureParam("ColorMap").getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        mat.setBoolean("VertexColor", true);
        mat.getAdditionalRenderState().setWireframe(Main.WIREFRAME);
//        mat.getAdditionalRenderState().setLineWidth(7);

        geometry.setMaterial(mat);
        return geometry;
    }

    public Block attachBlock(Block b, Texture t) {
        b.setVertexOffsetInParentChunk(positions.size());

//        System.out.println("starting indices index " + indices.size());
//        System.out.println("block indices size " + b.getIndices().size());
//
//        System.out.println("block vertex offset " + b.getVertexOffsetInParentChunk());
//        System.out.println("\n\n\n");
        addBlockData(b, t);

        Mesh m = generateMesh();
//        updateMesh(b.getPositions().size(),b.getIndices().size(),b.getUvs().size(),b.getColors().size());

//        if(geometry != null)
        geometry.setMesh(m);
//        else
//            generateGeometry(m);

//          m.getFloatBuffer(VertexBuffer.Type.TexCoord).position(0);
//        for(int i = 0 ; i< m.getFloatBuffer(VertexBuffer.Type.TexCoord).limit();i++)
//        System.out.print(m.getFloatBuffer(VertexBuffer.Type.TexCoord).get()+", ");
        return b;
    }

    public Block addBlockData(Block b, Texture t) {
        blocks.add(b);
        /*veretxOffsetInParentChunk is the number of the first vertex in the chunk which belongs to this block
           so the block vertices in a chunk start at vertexOffsetInParentChunk and end at vertexOffsetInparentChunk+vertexCount
         */
        b.setVertexOffsetInParentChunk(positions.size());
        positions.addAll(b.getPositions());
        indices.addAll(b.getIndices());
        normals.addAll(b.getNormals());

        uvs.addAll(b.getUvs());
        colors.addAll(b.getColors());
        blocksCount += 1;
        vertexCount += b.getVertexCount();
        return b;
    }

    public Block detachBlock(Block b) {
        if (b == null) {
            return b;
        }

        int vertexOffset = b.getVertexOffsetInParentChunk();
        int vertexCount = b.getVertexCount();
        int triangleCount = vertexCount / 3;

        // Remove the vertices and adjust the indices
        for (int i = vertexOffset + vertexCount - 1; i >= vertexOffset; i--) {
            positions.remove(i);
            uvs.remove(i);
        }

        // Adjust the indices for the removed block
        for (int i = 0; i < indices.size(); i++) {
            int index = indices.get(i);

            if (index >= vertexOffset) {
                index -= vertexCount;
            }

            indices.set(i, index);
        }

        // Adjust the vertex offsets and indices for subsequent blocks
        for (Block block : blocks) {
            if (block.getVertexOffsetInParentChunk() > vertexOffset) {
                int blockVertexOffset = block.getVertexOffsetInParentChunk();
                int blockVertexCount = block.getVertexCount();

                // Adjust the vertex offset
                block.setVertexOffsetInParentChunk(blockVertexOffset - vertexCount);

                // Adjust the indices for the block
                for (int i = 0; i < block.getIndices().size(); i++) {
                    int index = block.getIndices().get(i);

                    if (index >= blockVertexOffset) {
                        index -= blockVertexCount;
                    }

                    block.getIndices().set(i, index);
                }
            }
        }

        // Update counts
        blocksCount--;
        vertexCount -= b.getVertexCount();

        Mesh m = generateMesh();
        geometry.setMesh(m);

        return b;
    }

    public void clear() {
        positions.clear();
        indices.clear();
        uvs.clear();

    }
    
///---------------------------------------MESH GENERATION AND BUFFER METHODS----------------------------------------------------------------------------

    public Mesh generateMesh() {
        Mesh mesh = new Mesh();

        mesh.setBuffer(VertexBuffer.Type.Position, 3, vector3fToBuffer(positions));
        mesh.setBuffer(VertexBuffer.Type.Index, 1, intToBuffer(indices));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, vector3fToBuffer(normals));

        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, vector2fToBuffer(uvs));
        mesh.setBuffer(VertexBuffer.Type.Color, 4, vector4fToBuffer(colors));
        mesh.updateBound();

        return mesh;
    }

//      public Mesh updateMesh(int newPositions,int newIndices, int newTexCoords,int newColors) {
//
//        Mesh mesh = geometry.getMesh();
//
//        mesh.setBuffer(VertexBuffer.Type.Position, 3, vector3fToBuffer(positions));
//        mesh.setBuffer(VertexBuffer.Type.Index, 1, intToBuffer(indices));
//
//        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, vector2fToBuffer(uvs));
//        mesh.setBuffer(VertexBuffer.Type.Color, 4, vector4fToBuffer(colors));
//        mesh.updateBound();
//
//        
//        return mesh;
//    }
    
    private static FloatBuffer vector3fToBuffer(List<Vector3f> list) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(list.size() * 3);
        for (Vector3f vec : list) {
            buf.put(vec.getX()).put(vec.getY()).put(vec.getZ());
        }
        flipBuffer(buf);
        return buf;
    }

    private static FloatBuffer vector2fToBuffer(List<Vector2f> list) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(list.size() * 2);
        for (Vector2f vec : list) {
            buf.put(vec.getX()).put(vec.getY());
        }
        flipBuffer(buf);
        return buf;
    }

    private static IntBuffer intToBuffer(List<Integer> list) {
        IntBuffer buf = BufferUtils.createIntBuffer(list.size());
        for (int i : list) {
            buf.put(i);
        }
        flipBuffer(buf);
        return buf;
    }

    private static FloatBuffer vector4fToBuffer(List<Vector4f> list) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(list.size() * 4);
        for (Vector4f vec : list) {
            buf.put(vec.getX()).put(vec.getY()).put(vec.getZ()).put(vec.getW());
        }
        flipBuffer(buf);
        return buf;
    }

    private static void flipBuffer(Buffer buffer) {
        // Since JDK 9, ByteBuffer class overrides some methods and their return type in the Buffer class. To
        // ensure compatibility with JDK 8, calling the 'flipBuffer' method forces using the
        // JDK 8 Buffer's methods signature, and avoids explicit casts.
//        buffer.flip();
    }

///----------------------------------------GETTERS AND SETTERS--------------------------------------------------------------------------    
    public List<Vector3f> getPositions() {
        return positions;
    }

    public void setPositions(List<Vector3f> positions) {
        this.positions = positions;
    }

    public List<Vector2f> getUvs() {
        return uvs;
    }

    public void setUvs(List<Vector2f> uvs) {
        this.uvs = uvs;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    public List<Vector4f> getColors() {
        return colors;
    }

    public void setColors(List<Vector4f> colors) {
        this.colors = colors;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Vector2f getChunkPos() {
        return chunkPos;
    }

    public void setChunkPos(Vector2f chunkPos) {
        this.chunkPos = chunkPos;
    }

    public int getBlocksCount() {
        return blocksCount;
    }

    public void setBlocksCount(int blocksCount) {
        this.blocksCount = blocksCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public void setNormals(List<Vector3f> normals) {
        this.normals = normals;
    }
    
    
    

}
