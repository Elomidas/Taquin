package model.path;

import model.Position;

import java.util.*;

public class Graph {
    static private Node nodes[][];
    public enum direction{none, up, down, left, right}

    static public void init(int height, int width) {
        nodes = new Node[height][width];
        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes[i].length; j++) {
                nodes[i][j] = new Node(new Position(i, j));
            }
        }

        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes[i].length; j++) {
                List<Edge> edges = new ArrayList<>();
                if(i > 0) {
                    edges.add(new Edge(nodes[i-1][j]));
                }
                if(i < (nodes.length - 1)) {
                    edges.add(new Edge(nodes[i+1][j]));
                }
                if(j > 0) {
                    edges.add(new Edge(nodes[i][j-1]));
                }
                if(j < (nodes[i].length - 1)) {
                    edges.add(new Edge(nodes[i][j+1]));
                }
                //Copy
                Edge tab[] = new Edge[edges.size()];
                for(int k = 0; k < edges.size(); k++) {
                    tab[k] = edges.get(k);
                }
                nodes[i][j].adjacencies = tab;
            }
        }
    }

    public static synchronized void setFree(Position pos, boolean free) {
        System.out.println(pos);
        nodes[pos.getX()][pos.getY()].setFree(free);
    }

    private static int computeDist(Position p1, Position p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    public static synchronized List<direction> AstarSearch(Position src, Position end, int agentId) {
        Node source = nodes[src.getX()][src.getY()];
        Node goal = nodes[end.getX()][end.getY()];
        source.setHVal(computeDist(src, end), agentId);

        Set<Node> explored = new HashSet<Node>();

        PriorityQueue<Node> queue = new PriorityQueue<Node>(
                20,
                Comparator.comparingDouble(i -> i.f_scores)
        );

        //cost from start
        source.g_scores = 0;

        queue.add(source);

        boolean found = false;

        while((!queue.isEmpty())&&(!found)){

            //the node in having the lowest f_score value
            Node current = queue.poll();

            explored.add(current);

            //goal found
            if(current.toString().equals(goal.toString())) {
                found = true;
            }

            //check every child of current node
            for(Edge e : current.adjacencies) {
                Node child = e.target;
                child.setHVal(computeDist(child.pos, end), agentId);
                int cost = e.cost;
                int temp_g_scores = current.g_scores + cost;
                int temp_f_scores = temp_g_scores + child.h_scores.get(agentId);

                /* If it's the first time we explore the child
                 * or we have a better score than previously
                 */
                if((!explored.contains(child)) ||
                        (temp_f_scores < child.f_scores)) {

                    child.parent = current;
                    child.g_scores = temp_g_scores;
                    child.f_scores = temp_f_scores;

                    if(queue.contains(child)){
                        queue.remove(child);
                    }

                    queue.add(child);

                }

            }

        }

        if(found) {
            //Reconstruct path
            Stack<Node> order = new Stack<>();
            Node current = goal;
            do {
                order.push(current);
                current = current.parent;
            } while (!current.pos.equals(src));

            List<direction> dirs = new ArrayList<>();
            while (!order.empty()) {
                Node tmp = order.pop();
                dirs.add(getDir(current.pos, tmp.pos));
                current = tmp;
            }

            return dirs;
        } else {
            return null;
        }
    }

    static private direction getDir(Position src, Position dest) {
        int dx = src.getX() - dest.getX();
        if(dx == 1) {
            return direction.up;
        }
        if(dx == -1) {
            return direction.down;
        }
        int dy = src.getY() - dest.getY();
        if(dy == 1) {
            return direction.left;
        }
        if(dy == -1) {
            return direction.right;
        }
        System.err.println("Error getDir : {" + dx + "," + dy + "}");
        return direction.none;
    }

    static class Node {

        final Position pos;
        int g_scores;
        HashMap<Integer, Integer> h_scores;
        int f_scores = 0;
        Edge[] adjacencies;
        Node parent;
        boolean free;

        Node(Position p) {
            pos = p;
            free = true;
            h_scores = new HashMap<>();
        }

        void setHVal(int dist, int agentId) {
            h_scores.put(agentId, dist);
        }

        public String toString() {
            return pos.toString();
        }

        public void setFree(boolean f) {
            free = f;
        }

    }

    static class Edge {
        final int cost;
        final Node target;

        Edge(Node targetNode){
            target = targetNode;
            cost = 1;
        }
    }
}
