package model;

import model.moves.MoveStrategy;

public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id;
    private MoveStrategy strategy;

    private static int _id = 0;
    private static Plateau _plateau;

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

    public Position getPosition() {
        return position;
    }

    public void Message(int targetId, String perform, String action, Position toFree) {
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
        //
    }

    @Override
    public void run() {
        //
    }
}
