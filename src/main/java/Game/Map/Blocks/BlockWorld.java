/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Map.Blocks;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.HashMap;
import java.util.HashSet;
import jme3tools.optimize.TextureAtlas;

/**
 *
 * @author 48793
 */
public class BlockWorld {

    private final int BLOCK_SIZE;
    private final int CHUNK_SIZE;
    private final int MAP_SIZE;
    private Block[][][] map;  // contains block data - generated based on logic map
    private final byte[][][] logicMap; // contains info if a block is present (!=0) needed for culling

    private TextureAtlas textureAtlas;
    private final AssetManager asm;

    private final HashMap<String, Chunk> chunks;
    private Node worldNode;

    public BlockWorld(int VOXEL_SIZE, int CHUNK_SIZE, int MAP_SIZE, byte[][][] logicMap, AssetManager asm, Node rootNode) {
        this.BLOCK_SIZE = VOXEL_SIZE;
        this.CHUNK_SIZE = CHUNK_SIZE;
        this.MAP_SIZE = MAP_SIZE;
        this.asm = asm;
        map = new Block[MAP_SIZE][MAP_SIZE][MAP_SIZE];
        this.logicMap = logicMap;

        chunks = createChunks();

        worldNode = new Node("Block World Node");
        textureAtlas = new TextureAtlas(64, 64);
        textureAtlas.addTexture(asm.loadTexture(BlockType.STONE.textureName), "DiffuseMap");
        textureAtlas.addTexture(asm.loadTexture(BlockType.DIRT.textureName), "DiffuseMap");

        initializeBlocks();
        rootNode.attachChild(worldNode);

    }

    private HashMap<String, Chunk> createChunks() {
        HashMap<String, Chunk> cs = new HashMap<>();
        for (int x = 0; x < map.length; x += CHUNK_SIZE) { // 4 == chunkSize
            for (int z = 0; z < map[x][0].length; z += CHUNK_SIZE) {

                String s = "" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE;

                Chunk chunk = new Chunk(this, (x / CHUNK_SIZE) * CHUNK_SIZE, (z / CHUNK_SIZE) * CHUNK_SIZE);

                cs.put(s, chunk);

            }
        }
        return cs;
    }

    public Block placeBlock(int x, int y, int z, BlockType bt) {

        if (logicMap[x][y][z] != 0) {
            throw new IllegalArgumentException("Block [" + x + "][" + y + "][" + z + "] already exists!");
        }

        logicMap[x][y][z] = 1;
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
//       Block   b = VoxelAdding.addBox((x * BLOCK_SIZE)-c.getChunkPos().getX()*BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE)-c.getChunkPos().getY()*BLOCK_SIZE, BLOCK_SIZE,  c.getBlocksCount(),  asm,  bt);
        Block b = VoxelAdding.AddOptimizedBox(c, map, logicMap, x, y, z, BLOCK_SIZE, c.getBlocksCount(), asm, bt, (x * BLOCK_SIZE) - c.getChunkPos().getX() * BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE) - c.getChunkPos().getY() * BLOCK_SIZE);
        map[x][y][z] = b;
        c.attachBlock(b, asm.loadTexture(bt.textureName));
        return b;
    }

    public Block removeBlock(int x, int y, int z) {
        logicMap[x][y][z] = 0;
        Block b = map[x][y][z];
        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
        c.detachBlock(b);
        return b;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public void setTextureAtlas(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public AssetManager getAsm() {
        return asm;
    }

    public Node getWorldNode() {
        return worldNode;
    }

    public void setWorldNode(Node worldNode) {
        this.worldNode = worldNode;
    }

    public int getBLOCK_SIZE() {
        return BLOCK_SIZE;
    }

    public Block addBlockDataToChunk(int x, int y, int z, BlockType bt) { // o

        Chunk c = chunks.get("" + (x / CHUNK_SIZE) * CHUNK_SIZE + (z / CHUNK_SIZE) * CHUNK_SIZE);
//       Block   b = VoxelAdding.addBox((x * BLOCK_SIZE)-c.getChunkPos().getX()*BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE)-c.getChunkPos().getY()*BLOCK_SIZE, BLOCK_SIZE,  c.getBlocksCount(),  asm,  bt);
        Block b = VoxelAdding.AddOptimizedBox(c, map, logicMap, x, y, z, BLOCK_SIZE, c.getBlocksCount(), asm, bt, (x * BLOCK_SIZE) - c.getChunkPos().getX() * BLOCK_SIZE, y * BLOCK_SIZE, (z * BLOCK_SIZE) - c.getChunkPos().getY() * BLOCK_SIZE);
        map[x][y][z] = b;
        c.addBlockData(b, asm.loadTexture(bt.textureName));

        return b;
    }

    public Block[][][] getMap() {
        return map;
    }

    public void setMap(Block[][][] map) {
        this.map = map;
    }

    public HashMap<String, Chunk> getChunks() {
        return chunks;
    }

    public byte[][][] getLogicMap() {
        return logicMap;
    }

    private void initializeBlocks() {
        // do not change below code - it performantly creates blockData in places where logicMap != 0
        // because it writes to chunk and batches it only once

        for (int x = 0; x < logicMap.length; x++) {
            for (int y = 0; y < logicMap[x].length; y++) {
                for (int z = 0; z < logicMap[x][y].length; z++) {

                    if (logicMap[x][y][z] == 1 || logicMap[x][y][z] == 9) {
                        addBlockDataToChunk(x, y, z, BlockType.STONE);
                    }

                }

            }

        }

        for (Chunk c : chunks.values()) {
            c.generateGeometry(c.generateMesh());
        }


    }

}
