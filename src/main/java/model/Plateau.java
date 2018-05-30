package model;

import java.util.ArrayList;

public class Plateau {
    private ArrayList<Agent> agents;
    private int high, length;

    public Plateau() {
        this(5, 5);
    }

    public Plateau(int sizeX, int sizeY) {
        agents = new ArrayList<>();
        high = sizeY;
        length = sizeX;
    }

    public Agent getAgent(int index) {
        if(index < agents.size()) {
            return agents.get(index);
        }
        return null;
    }

    public int size() {
        return agents.size();
    }

    public void add(Position initial, Position target) {
        agents.add(new Agent(initial, target));
    }

    public boolean checkPosition(Position pos) {
        return pos.getX() >= 0
                && pos.getX() < length
                && pos.getY() >= 0
                && pos.getY() < high;
    }

    public boolean isFree(Position position) {
        for(Agent a : agents) {
            if(a.getPosition() == position) {
                return false;
            }
        }
        return true;
    }
}
