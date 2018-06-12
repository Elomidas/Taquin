package model;

import jdk.internal.org.objectweb.asm.Handle;
import model.path.Graph;
import model.moves.MoveStrategy;

import java.util.ArrayList;
import java.util.List;


public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id, priority;
    private MoveStrategy strategy;
    private boolean ghost;

    private static int _id = 1;
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
     */
    public Agent(Position pos, Position targ) {
        position = new Position(pos.getX(), pos.getY());
        target = new Position(targ.getX(), targ.getY());
        id = _id;
        priority = id;
        _id++;
        strategy = null;
        StringBuilder builder = new StringBuilder();
        builder.append(id).append(".jpg");
        this.img = builder.toString();
    }

    public void setAgentPriority(int prio) {
        priority = prio;
    }

    public int getAgentPriority() {
        return priority;
    }

    public int getAgentId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Position getTarget() {
        return target;
    }

    /**
     * Send a message from this agent
     * @param targetId  Id of the targeted agent
     * @param perform   Perform of the message
     * @param action    Action of the message
     * @param toFree    Position affected
     */
    public void SendMessage(int targetId, Message.performs perform, Message.actions action, Position toFree) {
        SendMessage(targetId, perform, action, toFree, priority);
    }

    public void SendMessage(int targetId, Message.performs perform, Message.actions action, Position toFree, int prio) {
        Messages.add(new Message(id, targetId, perform, action, toFree, prio));
    }

    public void SendRequest(int targetId, Position toFree) {
        SendMessage(targetId, Message.performs.request, Message.actions.move, toFree);
    }

    public void SendRequest(int targetId, Position toFree, int prio) {
        SendMessage(targetId, Message.performs.request, Message.actions.move, toFree, prio);
    }

    public void SendResponse(int targetId, Position toFree) {
        SendMessage(targetId, Message.performs.response, Message.actions.move, toFree, 26);
    }

    /**
     * Get next message for this agents
     * @return The next message, null if there isn't any
     */
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
        boolean token = _board.getToken();
        while (!token) {
            aleatempo();
            token = _board.getToken();
        }
        Graph.setFree(position, true);
        Position oldPos = new Position(position);
        setStrategy(StrategyFromDirection(dir));
        boolean result = strategy != null && strategy.move(this);
        Graph.setFree(position, false);
        if(!result) {
            System.out.println((strategy == null) ? "Strat null" : "unable to move");
            _board.giveToken();
        } else {
            _board.setChanged();
            _board.notifyObservers(new Position[]{oldPos, new Position(position)});
            tempo();
        }
        return result;
    }

    protected void Compute() {
        //TODO
    }

    @Override
    public void run() {
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
                //Try to reach its target
                List<Graph.direction> path = FindBestPath();
                followDirection(path);
            }
        }
        if(!fini && goodPosition()) {
            System.out.println("J'ai fini (" + getAgentId() + ")");
        }
        System.out.println(getAgentId() + " -> Fin du thread");
    }

    private Message waitingMessage() {
        Message message = null;
        while(test && (message == null)) {
            tempo();
            message = RetrieveMessage();
        }
        return message;
    }

    private void followDirection(List<Graph.direction> path) {
        if(path != null) {
            for (int i = 0; i < path.size(); i++) {
                boolean success = move(path.get(i));
                if (!success) {
                    System.out.println(getAgentId() + " fail");
                    if (ghost) {
                        Position next = strategy.getNewPos(this);
                        Agent a = _board.getAgent(next);
                        SendRequest(a.getAgentId(), next);
                        HandleMessage();
                    } else {
                        if (Math.random() > 0.5) {
                            path = FindBestPath();
                            i = -1;
                        }
                    }
                }
                tempo();
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

    private List<Graph.direction> FindBestPath() {
        List<Graph.direction> path = null;
        for(int i = 0; (i < 87) && (path == null); i++) {
            aleatempo();
            path = Graph.AstarSearch(position, target, getAgentId());
        }
        if(path == null) {
            System.out.println(getAgentId() + " unable to find good path");
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

    private Message WaitMessage() {
        Message message = RetrieveMessage();
        while(message == null) {
            aleatempo();
            message = RetrieveMessage();
        }
        return message;
    }

    private void HandleMessage() {
        Message message = WaitMessage();
        switch(message.getPerform()) {
            case request:
                List<Position> best = new ArrayList<>(),
                        common = new ArrayList<>(),
                        forbidden = new ArrayList<>();
                for(Position p : position.getAdjacency()) {
                    if(!_board.checkPosition(p)) {
                        forbidden.add(p);
                    } else {
                        Agent a = _board.getAgent(p);
                        if (a == null) {
                            best.add(p);
                        } else if (a.getAgentId() == message.getSender()) {
                            forbidden.add(p);
                        } else {
                            common.add(p);
                        }
                    }
                }
                boolean moved = false;
                for(int i = 0; (i < best.size()) && !moved; i++) {
                    moved = move(MoveStrategy.getDirection(position, best.get(i)));
                }
                for(int i = 0; (i < common.size()) && !moved; i++) {
                    moved = move(MoveStrategy.getDirection(position, common.get(i)));
                }
                break;
            case response:
                Graph.direction dir = MoveStrategy.getDirection(position, message.getPosition());
                if(dir != null) {
                    move(dir);
                }
                break;
            default:
                System.err.println("Unhandled perform : " + message.getPerform());
        }
    }

    private void tsttempo(long tps) {
        try {
            sleep(tps);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tempo() {
        tsttempo(200);
    }

    private void aleatempo() {
        tsttempo(50 + (long)(Math.random() * priority * 20));
    }
}
