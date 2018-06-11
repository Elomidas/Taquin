package model;

public class Message {
    enum performs{request, response}
    enum actions{move}

    private int sender;
    private int reciever;
    private performs perform;
    private actions action;
    private Position toFree;
    private int priority;

    public Message(int send, int receive, performs perf, actions act, Position pos, int prio) {
        sender = send;
        reciever = receive;
        perform = perf;
        action = act;
        toFree = pos;
        priority = prio;
    }

    public int getPriority() {
        return priority;
    }

    public int getSender() {
        return sender;
    }

    public performs getPerform() {
        return perform;
    }

    public actions getAction() {
        return action;
    }

    public Position getPosition() {
        return toFree;
    }

    public int getReceiver() {
        return reciever;
    }
}
