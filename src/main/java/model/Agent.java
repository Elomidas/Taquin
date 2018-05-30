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
    private static Messages _messages;

    private final static int _up = 1,
                            _down = 2,
                            _left = 3,
                            _right = 4,
                            _none = 0;
    private static Board _board;

    /**
     * Set the board to watch
     * @param p board to watch
     */
    static public void setPlateau(Board p) {
        _board = p;
    }

    /**
     * Get the board to watch
     * @return the board to watch
     */
    static public Board getPlateau() {
        return _board;
    }

    /**
     * Constructor
     * @param pos   Initial position
     * @param targ  Position to reach
     */
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

    /**
     * Send a message from this agent
     * @param targetId  Id of the targeted agent
     * @param perform   Perform of the message
     * @param action    Action of the message
     * @param toFree    Position affected
     */
    public void SendMessage(int targetId, Message.performs perform, Message.actions action, Position toFree) {
        Messages.add(new Message(id, targetId, perform, action, toFree));
    }

    public Message RetrieveMessage() {
        return Messages.getNext(this);
    }

    public void setPosition(Position pos) {
        position = pos;
    }

    protected void setStrategy(MoveStrategy strat) {
        strategy = strat;
    }

    /**
     * Move the agent if possible
     * @return true if agent has been moved, false else
     */
    protected boolean move() {
        return strategy != null && strategy.move(this);
    }

    protected void Compute() {
        //TODO
    }

    @Override
    public void run() {
        while(!_board.finish()) {
            //TODO
            if(goodPosition()) {
                //TODO
                //Wait for messages from others
            } else {
                //TODO
                //Try to reach its target
            }
        }
    }

    /**
     * Check if agent reach its target
     * @return true if agent is at targeted position, false else.
     */
    public boolean goodPosition() {
        return position.equals(target);
    }

    public List<Integer> FindBestPath(){
        List<Integer> res = new ArrayList<>();



        return res;
    }
}
