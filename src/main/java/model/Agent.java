package model;

import model.moves.*;

import java.util.ArrayList;
import java.util.List;

public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id;
    private MoveStrategy strategy;

    private static int _id = 0;
    private static Plateau _plateau;
    private static Messages _messages;

    public final static int _up = 1,
                            _down = 2,
                            _left = 3,
                            _right = 4,
                            _none = 0;

    static public void setPlateau(Plateau p) {
        _plateau = p;
    }

    static public Plateau getPlateau() {
        return _plateau;
    }

    public Agent(Position pos, Position targ) {
        position = pos;
        target = targ;
        id = _id;
        _id++;
        strategy = null;
    }

    public int getAgentId() {
        return id;
    }

    public Messages getMessages(){ return _messages; }

    public Position getPosition() {
        return position;
    }

    public void Message(int targetId, Message.performs perform, Message.actions action, Position toFree) {
        //TODO
    }

    public void setPosition(Position pos) {
        position = pos;
    }

    protected void setStrategy(MoveStrategy strat) {
        strategy = strat;
    }

    protected boolean move() {
        return strategy != null && strategy.move(this);
    }

    protected void Compute() {
        //TODO
    }

    @Override
    public void run() {
        //TODO
    }

    public List<Integer> FindBestPath(){
        List<Integer> res = new ArrayList<>();



        return res;
    }
}
