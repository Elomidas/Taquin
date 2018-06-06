package model;

import model.moves.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id;
    private MoveStrategy strategy;

    private static int _id = 0;
    private static Messages _messages;

    public String getImg() {
        return img;
    }

    private enum direction{none, up, down, left, right}

    private static Board _board;

    private final String img;

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
    public Agent(Position pos, Position targ, String img) {
        position = pos;
        target = targ;
        id = _id;
        _id++;
        strategy = null;
        this.img = img;
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

    /**
     * Set move strategy
     * @param strat Strategy to set
     */
    private void setStrategy(MoveStrategy strat) {
        strategy = strat;
    }

    /**
     * Adapt the move strategy to the direction
     * @param dir Move's direction
     * @return MoveStrategy matching the direction
     */
    private MoveStrategy StrategyFromDirection(direction dir) {
        switch(dir) {
            case up:
                return MoveStrategy._up;
            case down:
                return MoveStrategy._down;
            case left:
                return MoveStrategy._left;
            case right:
                return MoveStrategy._right;
            default:
                return null;
        }
    }

    /**
     * Move the agent if possible
     * @param dir Direction of the move
     * @return true if agent has been moved, false else
     */
    protected boolean move(direction dir) {
        setStrategy(StrategyFromDirection(dir));
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

        //Heuristique utilisé dans notre A*
        ArrayList<Integer> h = new ArrayList<>(getPlateau().size());

        //
        ArrayList<Integer> g = new ArrayList<>(getPlateau().size());

        for(int i=0;i<getPlateau().size();i++){
            h.set(i, Math.abs(position.getX() - target.getX() + Math.abs(position.getY() - target.getY()) ));
        }

        for (int i=0;i<g.size();i++)
            g.set(i, 0);



        return res;
    }
}
