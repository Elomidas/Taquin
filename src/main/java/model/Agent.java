package model;

import jdk.internal.org.objectweb.asm.Handle;
import model.path.Graph;
import model.moves.MoveStrategy;

import java.util.ArrayList;
import java.util.List;


public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id, priority, tmpPriority;
    private MoveStrategy strategy;
    private int ghost;

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
        SendMessage(targetId, perform, action, toFree, tmpPriority);
    }

    public void SendMessage(int targetId, Message.performs perform, Message.actions action, Position toFree, int prio) {
        Messages.add(new Message(id, targetId, perform, action, toFree, prio));
    }

    public void SendRequest(int targetId, Position toFree) {
        SendMessage(targetId, Message.performs.request, Message.actions.move, toFree);
    }

    public void SendResponse(int targetId, Position toFree, boolean success) {
        SendMessage(targetId, Message.performs.response, success ? Message.actions.success : Message.actions.failure, toFree, tmpPriority);
    }

    /**
     * Get next message for this agents
     * @return The next message, null if there isn't any
     */
    public Message RetrieveResponse() {
        return Messages.getNextResponse(this);
    }
    public Message RetrieveRequest() {
        return Messages.getNextRequest(this);
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
        if(dir == null) {
            return null;
        }
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
            _board.giveToken();
        } else {
            _board.setChanged();
            _board.notifyObservers(new Position[]{oldPos, new Position(position)});
            tempo();
        }
        return result;
    }

    private boolean checkPriority() {
        return (_board.getCurrentPriority() >= getAgentPriority());
    }

    @Override
    public void run() {
        boolean fini = false;
        tmpPriority = priority;
        while(!_board.finish() && test) {
            if(goodPosition() || !checkPriority()) {
                if(!fini && goodPosition()) {
                    System.out.println("J'ai fini (" + getAgentId() + ")");
                    _board.updateCurrentPriority();
                    fini = true;
                } else if(fini && !goodPosition()) {
                    fini = false;
                    _board.updateCurrentPriority();
                }
                HandleMessage(WaitRequest());
            } else {
                System.out.println("Moving (" + getAgentId() + ")");
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

    private void followDirection(List<Graph.direction> path) {
        for (int i = 0; (path != null) && (i < path.size()) && test; i++) {
            //boolean success = move(path.get(i));
            if (!move(path.get(i))) {
                if (ghost > 0) {
                    Position next = strategy.getNewPos(this);
                    if(!pushAgent(next)) {
                        ghost--;
                    } else {
                        ghost = 3;
                    }
                } else {
                    path = FindBestPath();
                    i = -1;
                }
            }
            tempo();
        }
    }

    private boolean pushAgent(Position newPos) {
        Agent a = _board.getAgent(newPos);
        if(a != null) {
            if(Messages.blocked(a.getAgentId())) {
                System.out.println(">> FAMINE (" + getAgentId() + " => " + a.getAgentId() + ") <<");
                Graph.ghostUp(a.getPosition());
                _board.updateCurrentPriority();
            } else {
                SendRequest(a.getAgentId(), newPos);
                HandleMessage(WaitResponse());
                return true;
            }
        }
        return false;
    }

    /**
     * Check if agent reach its target
     * @return true if agent is at targeted position, false else.
     */
    public boolean goodPosition() {
        return position.equals(target);
    }

    private List<Graph.direction> FindBestPath() {
        List<Graph.direction> path = Graph.AstarSearch(position,target, getAgentId());
        if(path == null) {
            path = FindGhostBestPath();
            ghost = 3;
        } else {
            ghost = 0;
        }
        return path;
    }

    private List<Graph.direction> FindGhostBestPath() {
        return Graph.AstarSearchGhost(position, target, getAgentId());
    }

    private Message WaitResponse() {
        Message message = RetrieveResponse();
        while((message == null) && test) {
            tempo();
            message = RetrieveResponse();
        }
        return message;
    }

    private Message WaitRequest() {
        Message message = RetrieveRequest();
        while((message == null) && test && !checkPriority()) {
            tempo();
            message = RetrieveRequest();
        }
        return message;
    }

    private void HandleMessage(Message message) {
        if(message != null) {
            switch (message.getPerform()) {
                case request:
                    int prevPrio = tmpPriority;
                    tmpPriority = message.getPriority();
                    List<Position> best = new ArrayList<>(),
                            common = new ArrayList<>();
                    for (Position p : position.getAdjacency()) {
                        if (_board.checkPosition(p)) {
                            Agent a = _board.getAgent(p);
                            if (a == null) {
                                best.add(p);
                            } else if (a.getAgentId() != message.getSender()) {
                                common.add(p);
                            }
                        }
                    }
                    boolean moved = false;
                    for (int i = 0; (i < best.size()) && !moved; i++) {
                        moved = move(MoveStrategy.getDirection(position, best.get(i)));
                    }
                    List<Graph.direction> list = FindGhostBestPath();
                    if(list != null) {
                        Graph.direction dir = list.get(0);
                        setStrategy(StrategyFromDirection(dir));
                        Position tmp = strategy.getNewPos(this);
                        Agent a = _board.getAgent(tmp);
                        if(a == null) {
                            moved = move(dir);
                        } else if(a.getAgentId() != message.getSender()) {
                            if(pushAgent(tmp)) {
                                moved = move(MoveStrategy.getDirection(position, tmp));
                            }
                        }
                    }
                    SendResponse(message.getSender(), message.getPosition(), moved);
                    tmpPriority = prevPrio;
                    tempo();
                    break;
                case response:
                    if (message.getAction() == Message.actions.success) {
                        Graph.direction dir = MoveStrategy.getDirection(position, message.getPosition());
                        if (dir != null) {
                            move(dir);
                        }
                    }
                    break;
                default:
                    System.err.println("Unhandled perform : " + message.getPerform());
            }
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
        tsttempo(100);
    }

    private void aleatempo() {
        tsttempo(100 + (long)(Math.random() * priority * 20));
    }
}
