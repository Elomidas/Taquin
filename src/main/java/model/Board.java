package model;

import model.path.Graph;

import java.util.ArrayList;

public class Board {
    private ArrayList<Agent> agents;
    private int high, length;

    public Board() {
        this(5, 5);
    }

    public Board(int sizeX, int sizeY) {
        agents = new ArrayList<>();
        high = sizeY;
        length = sizeX;
        Graph.init(sizeX, sizeY);
        Agent.setPlateau(this);
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

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

    public boolean finish() {
        for(Agent agent : agents) {
            if(!agent.goodPosition()) {
                return false;
            }
        }
        return true;
    }

    public void start() {
        for(Agent agent : agents) {
            agent.start();
        }
    }
}
