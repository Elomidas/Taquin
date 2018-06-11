package model;

import model.path.Graph;
import model.moves.MoveStrategy;

import java.util.List;


public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id;
    private MoveStrategy strategy;
    private boolean ghost;

    private static int _id = 0;
    private static Messages _messages;
    private static boolean test = false;

    public String getImg() {
        return img;
    }

    private static Board _board = null;

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

    static public void setRunnable(boolean runnable) {
        test = runnable;
    }

    /**
     * Constructor
     * @param pos   Initial position
     * @param targ  Position to reach
     * @param img   Path to image
     */
    public Agent(Position pos, Position targ, String img) {
        position = new Position(pos.getX(), pos.getY());
        target = new Position(targ.getX(), targ.getY());
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

    public void SendRequest(int targetId, Position toFree) {
        SendMessage(targetId, Message.performs.request, Message.actions.move, toFree);
    }

    public void SendResponse(int targetId, Position toFree) {
        SendMessage(targetId, Message.performs.response, Message.actions.move, toFree);
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
    private MoveStrategy StrategyFromDirection(Graph.direction dir) {
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
    private boolean move(Graph.direction dir) {
        Graph.setFree(position, true);
        setStrategy(StrategyFromDirection(dir));
        boolean result = strategy != null && strategy.move(this);
        Graph.setFree(position, true);
        _board.setChanged();
        return result;
    }

    protected void Compute() {
        //TODO
    }

    @Override
    public void run() {
        test = true;
        boolean fini = false;
        while(!_board.finish() && test) {
            //TODO
            if(goodPosition()) {
                //TODO
                if(!fini) {
                    System.out.println("J'ai fini (" + getAgentId() + ")");
                    fini = true;
                }
                tempo();
            } else {
                fini = false;
                //TODO
                //Try to reach its target
                List<Graph.direction> path = FindBestPath();
                for(int i = 0; (i < path.size()) && test; i++) {
                    System.out.println("Move : " + getAgentId() + " (" + path.get(i) + ")");
                    Position oldPos = getPosition();
                    boolean success = move(path.get(i));
                    Position newPos = getPosition();
                    if(!success) {
                        //System.out.println(getAgentId() + " : blocked");
                        if(!ghost) {
                            path = FindBestPath();
                            i = 0;
                        } else {
                            Position target = strategy.getNewPos(this);
                            int agent = _board.getId(target);
                            if(agent != -1) {
                                SendRequest(agent, target);
                            }
                            while(!_board.isFree(target) && test) {
                                tempo();
                                System.out.println("Waiting (" + getAgentId() + ")");
                            }
                        }
                    } else {
                        tempo();
                    }
                    _board.notifyObservers(new Position[]{oldPos, newPos});
                }
            }
        }
        if(!fini && goodPosition()) {
            System.out.println("J'ai fini (" + getAgentId() + ")");
        }
        System.out.println(getAgentId() + " -> Fin du thread");
    }

    /**
     * Check if agent reach its target
     * @return true if agent is at targeted position, false else.
     */
    public boolean goodPosition() {
        return position.equals(target);
    }

    private List<Graph.direction> FindBestPath() {

        List<Graph.direction> path = Graph.AstarSearch(position, target, getAgentId());
        if(path != null) {
            path = FindGhostBestPath();
            ghost = true;
        } else {
            ghost = false;
        }
        return path;
    }

    private List<Graph.direction> FindGhostBestPath() {
        return Graph.AstarSearchGhost(position, target, getAgentId());
    }

    private void tempo() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
