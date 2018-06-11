package model;

import model.path.Graph;

import java.util.ArrayList;
import java.util.Observable;

public class Board extends Observable implements Runnable {
    private ArrayList<Agent> agents;
    private int height, length;
    private boolean token;

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

    public synchronized boolean getToken() {
        if(token) {
            token = false;
            return true;
        }
        return false;
    }

    public synchronized void giveToken() {
        token = true;
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
        System.out.println("Added");
        agents.add(new Agent(new Position(px, py), new Position(tx, ty), img));
    }

    public boolean checkPosition(Position pos) {
        return ((pos.getX() >= 0)
                && (pos.getX() < length)
                && (pos.getY() >= 0)
                && (pos.getY() < height));
    }

    public boolean isFree(Position pos) {
        return isFree(pos.getX(), pos.getY());
    }

    public boolean isFree(int x, int y) {
        return (getAgent(x, y) == null);
    }

    public int getId(Position pos) {
        return getId(pos.getX(), pos.getY());
    }

    public int getId(int x, int y) {
        Agent a = getAgent(x, y);
        return (a == null) ? -1 : a.getAgentId();
    }

    public Agent getAgent(Position pos) {
        return getAgent(pos.getX(), pos.getY());
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

    @Override
    public void setChanged(){
        super.setChanged();
    }

    //TODO
    public void stop(){
        Agent.setRunnable(false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Agent.setRunnable(true);
    }

    @Override
    public void run() {
        token = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Agent.setRunnable(true);
        for(Agent agent : agents) {
            agent.start();
        }
    }


}
