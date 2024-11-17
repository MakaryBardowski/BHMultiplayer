/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.map.blocks;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author 48793
 */
public class Block {
    @Getter
    private final BlockType blockType;
    private List<Vector3f> positions = new ArrayList<>();
    private List<Vector2f> uvs = new ArrayList<>();
    private List<Vector3f> normals = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();
    private List<Vector4f> colors = new ArrayList<>();

    private int index;
    private int vertexOffsetInParentChunk; // first vertex in parentChunk

    private float lightLevel;
    private final float[] faceOffsetIndexes;

    public Block(BlockType blockType) {
        this.blockType = blockType;
        faceOffsetIndexes = new float[6];

        for (int i = 0; i < faceOffsetIndexes.length; i++) {
            faceOffsetIndexes[i] = -1;
        }

    }

    public float[] getFaceOffsetIndexes() {
        return faceOffsetIndexes;
    }

    public List<Vector3f> getPositions() {
        return positions;
    }

    public List<Vector2f> getUvs() {
        return uvs;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public void setPositions(List<Vector3f> positions) {
        this.positions = positions;
    }

    public void setUvs(List<Vector2f> uvs) {
        this.uvs = uvs;
    }

    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    public void setColors(List<Vector4f> colors) {
        this.colors = colors;
    }
    
    public void setNormals(List<Vector3f> normals){
    this.normals = normals;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }
    
    
    public List<Vector4f> getColors() {
        return colors;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(float lightLevel) {
        this.lightLevel = lightLevel;
    }

    public int getVertexOffsetInParentChunk() {
        return vertexOffsetInParentChunk;
    }

    public void setVertexOffsetInParentChunk(int vertexOffsetInParentChunk) {
        this.vertexOffsetInParentChunk = vertexOffsetInParentChunk;
    }

    public int getVertexCount() {
        return positions.size();
    }

}
