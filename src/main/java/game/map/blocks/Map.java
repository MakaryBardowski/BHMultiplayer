package game.map.blocks;

import com.jme3.math.Vector3f;

public class Map {
    private final int blockSize;
    private int mapSizeX;
    private int mapSizeY;
    private int mapSizeZ;

    private byte[] logicMap;

    public Map(byte[] logicMap,int mapSizeX, int mapSizeY, int mapSizeZ, int blockSize){
        this.logicMap = logicMap;
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.mapSizeZ = mapSizeZ;
        this.blockSize = blockSize;
    }

    public byte getBlockIdAtIndex(int index){
        return logicMap[index];
    }
    public void setBlockIdAtIndex(int index,byte value){
        logicMap[index] = value;
    }

    public byte getBlockIdAtPosition(int x, int y,int z){
        System.out.println("pos xyz "+x+" "+y+" "+z);
        return logicMap[positionToIndex(x,y,z)];
    }

    public byte getBlockIdAtPosition(Vector3f position) {
        return logicMap[positionToIndex(position)];
    }

    public void setBlockIdAtPosition(int x, int y,int z, byte blockId){
        logicMap[positionToIndex(x,y,z)] = blockId;
    }

    public void setBlockIdAtPosition(Vector3f position, byte blockId) {
        logicMap[positionToIndex(position)] = blockId;
    }

    public boolean isPositionEmpty(int x, int y, int z){
        return getBlockIdAtPosition(x,y,z) == 0;
    }

    public boolean isPositionEmpty(Vector3f position){
        return getBlockIdAtPosition(position) == 0;
    }

    public boolean isPositionNotEmpty(int x, int y, int z){
        return !isPositionEmpty(x,y,z);
    }

    public boolean isPositionNotEmpty(Vector3f position){
        return !isPositionEmpty(position);
    }

    public boolean isWithinMapBounds(int x,int y, int z){
        int index = positionToIndex(x,y,z);
        return index < logicMap.length && index >= 0;
    }

    public boolean isWithinMapBounds(Vector3f position){
        return positionToIndex(position) < logicMap.length;
    }

    private int positionToIndex(int x, int y, int z){
        return x * (mapSizeY * mapSizeZ) + y * mapSizeZ + z;
    }

    private int positionToIndex(Vector3f position){
        return positionToIndex(
                (int) (Math.floor(position.x / blockSize)),
                (int) (Math.floor(position.y / blockSize)),
                (int) (Math.floor(position.z / blockSize))
        );
    }

    public int getXFromIndex(int index) {
        int x = index / (mapSizeY * mapSizeZ);
        return x;
    }

    public int getYFromIndex(int index) {
        int y = (index % (mapSizeY * mapSizeZ)) / mapSizeZ;
        return y;
    }

    public int getZFromIndex(int index) {
        int z = index % mapSizeZ;
        return z;
    }

    public int getLength(){
        return logicMap.length;
    }

    public static int positionToIndex(int x, int y, int z,int mapSizeY,int mapSizeZ){
        return x * (mapSizeY * mapSizeZ) + y * mapSizeZ + z;
    }

    public static int positionToIndex(Vector3f position,int mapSizeY,int mapSizeZ, int blockSize){
        return Map.positionToIndex(
                (int) (Math.floor(position.x / blockSize)),
                (int) (Math.floor(position.y / blockSize)),
                (int) (Math.floor(position.z / blockSize)),
                mapSizeY,
                mapSizeZ
        );
    }

}
