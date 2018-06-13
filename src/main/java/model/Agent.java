package model;

import model.communication.Message;
import model.communication.Messages;
import model.path.Graph;
import model.moves.MoveStrategy;

import java.util.*;


public class Agent extends Thread {
    private Position position;
    private Position target;
    private int id, priority, tmpPriority;
    private MoveStrategy strategy;

    private static int _id = 1;
    private static boolean test = false;

    private static Board _board = null;

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
        position = null;
        target = new Position(targ.getX(), targ.getY());
        id = _id;
        priority = id;
        _id++;
        strategy = null;
        setPosition(new Position(pos.getX(), pos.getY()));
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
    private void SendMessage(int targetId, Message.performs perform, Message.actions action, Position toFree, List<Integer> prev) {
        SendMessage(targetId, perform, action, toFree, tmpPriority, prev);
    }

    private void SendMessage(int targetId, Message.performs perform, Message.actions action, Position toFree, int prio, List<Integer> prev) {
        Message msg = new Message(id, targetId, perform, action, toFree, prio);
        if(prev != null) {
            for(Integer id : prev) {
                msg.addPrev(id);
            }
        }
        Messages.add(msg);
    }

    private void SendRequest(int targetId, Position toFree, List<Integer> previous) {
        SendMessage(targetId, Message.performs.request, Message.actions.move, toFree, previous);
    }

    private void SendRequest(int targetId, Message.actions action , Position toFree) {
        SendMessage(targetId, Message.performs.request, action, toFree, null);
    }

    private void SendResponse(int targetId, Position toFree, boolean success) {
        SendMessage(targetId, Message.performs.response, success ? Message.actions.success : Message.actions.failure, toFree, tmpPriority, null);
    }

    /**
     * Get next message for this agents
     * @return The next message, null if there isn't any
     */
    private Message RetrieveResponse() {
        return Messages.getNextResponse(this);
    }
    private Message RetrieveRequest() {
        return Messages.getNextRequest(this);
    }

    public void setPosition(Position pos) {
        if(position != null) {
            Graph.setFree(position, true);
        }
        position = pos;
        if(position != null) {
            Graph.setFree(position, false);
        }
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
        boolean result = false;
        setStrategy(StrategyFromDirection(dir));
        if(_board.isFree(strategy.getNewPos(this))) {
            System.out.println(String.format("%d move ", getAgentId()) + dir);
            Position oldPos = new Position(position);
            result = strategy != null && strategy.move(this);
            if (result) {
                _board.setChanged();
                _board.notifyObservers(new Position[]{oldPos, new Position(position)});
            }
        }
        tempo();
        return result;
    }

    private boolean checkPriority() {
        return (_board.getCurrentPriority() == getAgentPriority());
    }

    @Override
    public void run() {
        tmpPriority = priority;
        while(!_board.finish() && test) {
            if(checkPriority()) {
                if(goodPosition()) {
                    _board.updateCurrentPriority();
                } else {
                    //Try to reach its target
                    List<Graph.direction> path = FindBestPath();
                    followDirection(path);
                }
            } else {
                HandleRequest(WaitRequest());
            }
        }
        System.out.println(getAgentId() + " -> Fin du thread");
    }

    private void followDirection(List<Graph.direction> path) {
        for (int i = 0; (path != null) && (i < path.size()) && test; i++) {
            //TODO corner at begin -> path < 3
            if((path.size() - i) == 3 && false) {
                //Corner check
                Board.corner corner = _board.getCorner(target);
                if(corner != Board.corner.none) {
                    System.out.println("Corner");
                    path = FindBestPath();
                    //TODO
                }
            }
            setStrategy(StrategyFromDirection(path.get(i)));
            boolean free = _board.isFree(strategy.getNewPos(this));
            if(free) {
                move(path.get(i));
            }
            if (!free) {
                Position next = strategy.getNewPos(this);
                pushAgent(next, null);
            }
            tempo();
        }
    }

    private boolean pushAgent(Position newPos, List<Integer> previous) {
        Agent a = _board.getAgent(newPos);
        if(a != null) {
            if(Messages.blocked(a.getAgentId())) {
                System.out.println(">> FAMINE (" + getAgentId() + " => " + a.getAgentId() + ") <<");
                Graph.ghostUp(a.getPosition());
                _board.updateCurrentPriority();
            } else {
                /**
                 * TODO
                 * Check if there is a message waiting
                 */
                //Check if there is a message
                Message message = RetrieveRequest();
                if(message != null) {
                    if(message.getPosition().equals(position)) {
                        System.out.println("FUCK");
                    }
                    SendResponse(message.getSender(), message.getPosition(), true);
                }
                SendRequest(a.getAgentId(), newPos, previous);
                return HandleResponse(WaitResponse());
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * Check if agent reach its target
     * @return true if agent is at targeted position, false else.
     */
    boolean goodPosition() {
        return position.equals(target);
    }

    private List<Graph.direction> FindBestPath() {
        List<Graph.direction> path = Graph.AstarSearch(position,target, getAgentId());
        return path;
    }

    private Message WaitResponse() {
        Message message = RetrieveResponse();
        while((message == null) && test) {
            if(checkPriority()) {
                System.out.println(String.format("Ag%02d waiting for response", getAgentId()));
            }
            tempo();
            message = RetrieveResponse();
        }
        return message;
    }

    private Message WaitRequest() {
        Message message = RetrieveRequest();
        System.out.println(String.format("Ag%02d wait message", getAgentId()));
        while((message == null) && test && !checkPriority()) {
            tempo();
            message = RetrieveRequest();
        }
        System.out.println(getAgentId() + " free");
        return message;
    }

    private void HandleRequest(Message message) {
        if(message != null) {
            int prevPrio = tmpPriority;
            tmpPriority = message.getPriority();
            Queue<Position> queue = new PriorityQueue<>(
                    3,
                    Comparator.comparingInt(p -> ((_board.isFree(p) ? 25 : 50) - _board.getPriority(p)))
            );

            for (Position p : position.getAdjacency()) {
                if (_board.checkPosition(p)) {
                    Agent a = _board.getAgent(p);
                    if ((a == null) || !message.contains(a.getAgentId())) {
                        queue.add(p);
                    }
                }
            }

            System.out.println(String.format("%d : %d poss", getAgentId(), queue.size()));
            if(queue.size() == 0) {
                System.out.println("Here");
            }
            boolean moved = false;
            while (!queue.isEmpty() && !moved) {
                Position current = queue.poll();
                boolean free = true;
                if (!_board.isFree(current)) {
                    free = pushAgent(current, message.getPrevious());
                }
                moved = free && move(MoveStrategy.getDirection(position, current));
            }

            if (message.getSender() >= 0) {
                SendResponse(message.getSender(), message.getPosition(), moved);
            } else {
                _board.updateCurrentPriority();
            }

            tmpPriority = prevPrio;
        }
    }

    private boolean HandleResponse(Message message) {
        if(message != null) {
            if(message.getAction() == Message.actions.success) {
                return true;
            }
        } else {
            System.out.println("Response missed " + getAgentId() + " (" + getAgentPriority() + ")");
        }
        return false;
    }

    private void tsttempo(long tps) {
        try {
            sleep(tps);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tempo() {
        //tsttempo(100);
        aleatempo();
    }

    private void aleatempo() {
        tsttempo(20 + (long)(Math.random() * 20));
    }
}
