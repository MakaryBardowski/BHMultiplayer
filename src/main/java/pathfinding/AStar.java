package pathfinding;

import Utils.GridUtils;
import com.jme3.math.Vector3f;

import java.util.*;

public class AStar {

    private static Node currentNode;
    private static Node neighbor;
    private static byte[][][] grid;
    private static boolean[][][] visited;  // 3D visited array
    private static final PriorityQueue<Node> openSet = new PriorityQueue<>(new NodeComparator());
    private static final Queue<Node> nodePool = new LinkedList<>();  // Node pool to reuse nodes
    private static boolean stopCurrentPathfinding = false;

    static class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node node1, Node node2) {
            return Integer.compare(node1.getF(), node2.getF());
        }
    }

    // Movement directions (x, y, z)
    private static final int[][][] DIRS = {
        {{-1, 0, 0}, {1, 0, 0}},
        {{0, 0, -1}, {0, 0, 1}},};

    public static void setPathfindingMap(byte[][][] map) {
        grid = map;
        visited = new boolean[map.length][map[0].length][map[0][0].length];  // 3D visited array

        // Prepopulate the node pool for better performance
        for (int i = 0; i < map.length * map[0].length * map[0][0].length; i++) {
            nodePool.add(new Node(0, 0, 0));
        }

        stopCurrentPathfinding = true;
    }

    public static List<Vector3f> findPath(Vector3f from, Vector3f to) {
        int startX = GridUtils.worldToGridCoordinate(from.x);
        int startY = GridUtils.worldToGridCoordinate(from.y);
        int startZ = GridUtils.worldToGridCoordinate(from.z);

        int endX = GridUtils.worldToGridCoordinate(to.x);
        int endY = GridUtils.worldToGridCoordinate(to.y);
        int endZ = GridUtils.worldToGridCoordinate(to.z);

        if (!GridUtils.isSpotEmpty(startX, startY, startZ, grid) || !GridUtils.isSpotEmpty(endX, endY, endZ, grid)) {
            return null;
        }

        List<Node> path = findPath(startX, startY, startZ, endX, endY, endZ);
        return path == null ? null : path.stream().map(Node::getVector3f).toList();
    }

    public static List<Node> findPath(int startX, int startY, int startZ, int goalX, int goalY, int goalZ) {
        if (grid[startX][startY][startZ] != 0 || grid[goalX][goalY][goalZ] != 0) {
            return null;
        }

        // Reset the visited array
        for (boolean[][] layer : visited) {
            for (boolean[] row : layer) {
                Arrays.fill(row, false);
            }
        }

        openSet.clear();

        Node startNode = getNewNode(startX, startY, startZ, 0, heuristic(startX, startY, startZ, goalX, goalY, goalZ));
        openSet.add(startNode);
        visited[startX][startY][startZ] = true;

        while (!openSet.isEmpty()) {
            currentNode = openSet.poll();

            if (stopCurrentPathfinding) {
                stopCurrentPathfinding = false;
                var unfinishedPath = getPathFromNode(currentNode);
                returnNodesToPool(unfinishedPath);
                return findPath(startX, startY, startZ, goalX, goalY, goalZ);
            }

            if (currentNode.x == goalX && currentNode.y == goalY && currentNode.z == goalZ) {
                return reconstructPath(currentNode);
            }

            for (int[][] dir : DIRS) {
                for (int[] d : dir) {
                    int newX = currentNode.x + d[0];
                    int newY = currentNode.y + d[1];
                    int newZ = currentNode.z + d[2];

                    if (isValid(newX, newY, newZ) && grid[newX][newY][newZ] == 0 && !visited[newX][newY][newZ]) {
                        neighbor = getNewNode(newX, newY, newZ, currentNode.g + 1, heuristic(newX, newY, newZ, goalX, goalY, goalZ));
                        neighbor.parent = currentNode;
                        openSet.add(neighbor);
                        visited[newX][newY][newZ] = true;  // Mark as visited
                    }
                }
            }
        }

        return null;
    }

    // Get a new node from the pool or create a new one if needed
    private static Node getNewNode(int x, int y, int z, int g, int h) {
        Node temp = nodePool.poll();
        if (temp == null) {
            temp = new Node(x, y, z);  // If pool is empty, create a new Node
        }
        temp.setPosition(x, y, z);
        temp.g = g;
        temp.h = h;
        temp.parent = null;
        return temp;
    }

    private static boolean isValid(int x, int y, int z) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && z >= 0 && z < grid[0][0].length;
    }

    private static int heuristic(int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
    }

    private static List<Node> reconstructPath(Node node) {
        var path = getPathFromNode(node);
        Collections.reverse(path);

        returnNodesToPool(path);
        if (path.isEmpty()) { // if path is empty then return null so no exception is thrown when reading path
            path = null;
        }
        return path;
    }

    private static void returnNodesToPool(List<Node> nodes) {
        for (Node n : nodes) {
            nodePool.offer(n);
        }
    }

    private static List<Node> getPathFromNode(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            if (node.parent != null) {
                path.add(node);
            }
            node = node.parent;
        }
        return path;
    }

    public static ArrayList<Vector3f> smoothPath(List<Node> pathNode, float weightData, float weightSmooth, float tolerance) {
        if (pathNode == null) {
            return null;
        }

        ArrayList<Vector3f> path = new ArrayList<>();
        for (Node n : pathNode) {
            path.add(n.getVector3f());
        }

        ArrayList<Vector3f> newPath = new ArrayList<>(path);

        float change = tolerance;
        while (change >= tolerance) {
            change = 0;
            for (int i = 1; i < path.size() - 1; i++) {
                for (int j = 0; j < 2; j++) {
                    float aux;
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
