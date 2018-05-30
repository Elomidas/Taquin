package model;

public class Message {
    enum performs{request}
    enum actions{move}

    private int sender;
    private int reciever;
    private performs perform;
    private actions action;
    private Position toFree;

    public Message(int send, int receive, performs perf, actions act, Position pos) {
        sender = send;
        reciever = receive;
        perform = perf;
        action = act;
        toFree = pos;
    }

    public int getReciever() {
        return reciever;
    }
}
