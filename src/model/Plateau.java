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
}
