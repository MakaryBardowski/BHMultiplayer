/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pathfinding;

import client.ClientGameAppState;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import server.ServerMain;

public class AStar {

    private static final int BLOCK_SIZE = ServerMain.getInstance().getBLOCK_SIZE();
    private static Node neighbor;
    private static final byte[][][] grid = ServerMain.getInstance().getMap();
    ;
    private static final boolean[] visited = new boolean[grid.length * grid[0].length * grid[1].length];
    private static final PriorityQueue<Node> openSet = new PriorityQueue<>(new NodeComparator());

    static class NodeComparator implements Comparator<Node> {

        public int compare(Node node1, Node node2) {
            return Integer.compare(node1.getF(), node2.getF());
        }
    }

    private static final int[][][] DIRS = {
        {{-1, 0, 0}, {1, 0, 0}},
        //            {{0, -1, 0}, {0, 1, 0}}, // vertical pathfinding
        {{0, 0, -1}, {0, 0, 1}},};
//        {{-1, 0, -1}, {1, 0, 1}},
//        {{-1, 0, 1}, {1, 0, -1}},};

    public static List<Vector3f> findPath(Vector3f from, Vector3f to) {
        int startX = (int) (Math.floor(from.x / BLOCK_SIZE));
        int startY = (int) (Math.floor(from.y / BLOCK_SIZE));
        int startZ = (int) (Math.floor(from.z / BLOCK_SIZE));

        int endX = (int) Math.floor(to.x / BLOCK_SIZE);
        int endY = (int) Math.floor(to.y / BLOCK_SIZE);
        int endZ = (int) Math.floor(to.z / BLOCK_SIZE);

        if ((startX >= grid.length || startX < 0)
                || (startY >= grid[0].length || startY < 0)
                || (startZ >= grid[0][0].length || startZ < 0)
                || (endX >= grid.length || endX < 0)
                || (endY >= grid[0].length || endY < 0)
                || (endZ >= grid[0][0].length || endZ < 0)) {
//            System.out.println("tried to pathfind outside the map. Returning null path.");
            return null;
        }

        return smoothPath(findPath(startX, startY, startZ, endX, endY, endZ), 0.2f, 0.2f, 0.000101f);
    }

    public static List<Node> findPath(int startX, int startY, int startZ, int goalX, int goalY, int goalZ) {
        if (grid[goalX][goalY][goalZ] != 0) {
            return null;
        }

        int width = grid.length;
        int height = grid[0].length;
        int depth = grid[0][0].length;

//        long time = System.nanoTime();
        Arrays.fill(visited, false);
//        long time1 = System.nanoTime();
//        float tf = ((float)(time1-time))/1_000_000;
//        System.out.println("tf "+tf);
        
        openSet.clear();
        
        openSet.add(new Node(startX, startY, startZ));
        visited[index(startX, startY, startZ, height, depth)] = true;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == goalX && current.y == goalY && current.z == goalZ) {
                // Path found, reconstruct and return it
                return reconstructPath(current);
            }

            for (int[][] dir : DIRS) {
                for (int[] d : dir) {
                    int newX = current.x + d[0];
                    int newY = current.y + d[1];
                    int newZ = current.z + d[2];

                    if (isValid(newX, newY, newZ, width, height, depth) && grid[newX][newY][newZ] == 0
                            && !visited[index(newX, newY, newZ, height, depth)]) {
                        neighbor = new Node(newX, newY, newZ);
                        neighbor.g = current.g + 1;
                        neighbor.h = heuristic(newX, newY, newZ, goalX, goalY, goalZ);
                        neighbor.parent = current;

                        openSet.add(neighbor);
                        visited[index(newX, newY, newZ, height, depth)] = true;
                    }
                }
            }
        }
        // No path found
//        System.out.println("null path. " + new Random().nextDouble());
//        System.out.printf("grid Main.grid[%d][%d][%d]", goalX, goalY, goalZ);
//        System.out.println("destination " + grid[goalX][goalY][goalZ]);
        return null;
    }

    private static boolean isValid(int x, int y, int z, int width, int height, int depth) {
        return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth;
    }

    private static int heuristic(int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
    }

    private static int index(int x, int y, int z, int height, int depth) {
        return x * height * depth + y * depth + z;
    }

    private static List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();

        while (node != null) {
            if (node.parent != null || path.isEmpty()) {
                /* add all but first node (because first node in the path
               is the tile we are already on)
                 */
                path.add(node);
            }
            node = node.parent;
        }

        Collections.reverse(path);
        return path;
    }

    // Rest of the code remains unchanged
    public static ArrayList<Vector3f> smoothPath(List<Node> pathNode, float weightData, float weightSmooth, float tolerance) {
        if (pathNode == null) {
            return null;
        }
        ArrayList<Vector3f> path = new ArrayList<>();

        for (Node n : pathNode) {
            path.add(n.getVector3f()); // make them center of tiles
        }

        ArrayList<Vector3f> newPath = new ArrayList<>();

        for (int i = 0; i < path.size(); i++) {
            var path_i = path.get(i);
            Vector3f wp = new Vector3f(path_i.x, path_i.y, path_i.z);
            newPath.add(wp);
        }

        float change = tolerance;
        while (change >= tolerance) {

            change = 0;
            for (int i = 1; i < path.size() - 1; i++) {
                for (int j = 0; j < 2; j++) {
                    float aux = 0;
                    if (j == 0) {
                        aux = newPath.get(i).getX();
                        newPath.get(i).setX(newPath.get(i).getX() + weightData * (path.get(i).getX() - newPath.get(i).getX()));
                        newPath.get(i).setX((float) (newPath.get(i).getX() + weightSmooth * (newPath.get(i - 1).getX() + newPath.get(i + 1).getX() - (2.0 * newPath.get(i).getX()))));
                        change += Math.abs(aux - newPath.get(i).getX());
                    } else {
                        aux = newPath.get(i).getZ();
                        newPath.get(i).setZ(newPath.get(i).getZ() + weightData * (path.get(i).getZ() - newPath.get(i).getZ()));
                        newPath.get(i).setZ((float) (newPath.get(i).getZ() + weightSmooth * (newPath.get(i - 1).getZ() + newPath.get(i + 1).getZ() - (2.0 * newPath.get(i).getZ()))));
                        change += Math.abs(aux - newPath.get(i).getZ());

                    }

                }
            }

        }

        return newPath;

    }

}
