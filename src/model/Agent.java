package model;

public class Agent {
    private Position position;
    private Position target;
    private int id;

    private static int _id = 0;

    public Agent(Position pos, Position targ) {
        position = pos;
        target = targ;
        id = _id;
        _id++;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public void Message(int targetId, String perform, String action, Position toFree) {
        //TODO
    }
}
