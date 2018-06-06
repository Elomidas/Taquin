package model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Board extends Observable {
    private ArrayList<Agent> agents;
    private int high, length;

    public Board() {
        this(5, 5);
    }

    public Board(int sizeX, int sizeY) {
        agents = new ArrayList<>();
        high = sizeY;
        length = sizeX;
        this.add(new Position(0,0), new Position(1,1), "etoile.jpg");
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

    public void add(Position initial, Position target, String img) {
        agents.add(new Agent(initial, target, img));
    }

    public boolean checkPosition(Position pos) {
        return pos.getX() >= 0
                && pos.getX() < length
                && pos.getY() >= 0
                && pos.getY() < high;
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
}