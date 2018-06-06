package model;

import model.path.Graph;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Board extends Observable {
    private ArrayList<Agent> agents;
    private int height, length;

    public Board() {
        this(5, 5);
    }

    public Board(int sizeX, int sizeY) {
        agents = new ArrayList<>();
        height = sizeY;
        length = sizeX;
        Graph.init(height, length);
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void add(int px, int py, int tx, int ty, String img) {
        agents.add(new Agent(new Position(px, py), new Position(tx, ty), img));
    }

    public boolean checkPosition(Position pos) {
        return pos.getX() >= 0
                && pos.getX() < length
                && pos.getY() >= 0
                && pos.getY() < height;
    }

    public boolean isFree(int x, int y) {
        Position position = new Position(x, y);
        for(Agent a : agents) {
            if(a.getPosition().equals(position)) {
                return false;
            }
        }
        return true;
    }

    public Agent getAgent(int x, int y){
        Position position = new Position(x, y);
        for(Agent a : agents) {
            if(a.getPosition().equals(position)) {
                return a;
            }
        }
        return null;
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
