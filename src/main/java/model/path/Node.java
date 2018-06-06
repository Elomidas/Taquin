package model.path;

import model.Position;

import java.util.HashMap;

public class Node {
    Position pos;
    HashMap<Integer, Integer> heuristics;
    boolean free;

    public Node(Position p, int c, int h) {
        pos = new Position(p.getX(), p.getY());
        heuristics = new HashMap<>();
        free = true;
    }

    public int getHeuristic(int agentId) {
        return heuristics.getOrDefault(agentId, 1);
    }

    public void setFree(boolean f) {
        free = f;
    }

    public void setHeuristics(Position target, int agentId) {
        int d = Math.abs(pos.getX() - target.getX()) + Math.abs(pos.getY() - target.getY());
        heuristics.put(agentId, d);
    }
}
