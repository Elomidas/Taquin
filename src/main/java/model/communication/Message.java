package model.communication;

import model.Position;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public enum performs{request, response}
    public enum actions{move, moveU, moveR, moveD, moveL, success, failure}

    private int sender;
    private int reciever;
    private performs perform;
    private actions action;
    private Position toFree;
    private int priority;
    private List<Integer> previous;

    public Message(int send, int receive, performs perf, actions act, Position pos, int prio) {
        sender = send;
        reciever = receive;
        perform = perf;
        action = act;
        toFree = pos;
        priority = prio;
        previous = new ArrayList<>();
        previous.add(sender);
    }

    public void addPrev(int id) {
        previous.add(id);
    }

    public boolean contains(int id) {
        return previous.contains(id);
    }

    public List<Integer> getPrevious() {
        return previous;
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
