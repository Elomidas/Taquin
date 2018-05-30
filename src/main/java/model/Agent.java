package model;

public class Agent {
    private Position position;
    private Position target;
    private int id;

    private static int _id = 0;
    private static Plateau _plateau;
    private static Messages _messages = new Messages();

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
    }

    public int getId() {
        return id;
    }

    public Messages getMessages(){ return _messages; }

    public Position getPosition() {
        return position;
    }

    public void Message(int targetId, String perform, String action, Position toFree) {
        //TODO
    }

    public void setPosition(Position pos) {
        position = pos;
    }
}
