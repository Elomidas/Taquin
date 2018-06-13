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
    private Set<contact> contacted;

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
        id = -1;
        priority = -1;
        strategy = null;
        setPosition(pos);
        setTarget(targ);
    }

    public synchronized void setAgentId(int aid) {
        id = aid;
    }

    public synchronized void setAgentPriority(int prio) {
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

    public synchronized void setPosition(Position pos) {
        if(position != null) {
            Graph.setFree(position, true);
        }
        position = new Position(pos);
        Graph.setFree(position, false);
        if(goodPosition()) {
            Graph.block(position);
        }
        contacted = new HashSet<>();
    }

    public synchronized void setTarget(Position pos) {
        target = new Position(pos);
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
    }

    private void followDirection(List<Graph.direction> path) {
        for (int i = 0; (path != null) && (i < path.size()) && test; i++) {
            setStrategy(StrategyFromDirection(path.get(i)));
            boolean free = _board.isFree(strategy.getNewPos(this));
            if(free) {
                move(path.get(i));
            }
            if (!free) {
                Position next = strategy.getNewPos(this);
                if(pushAgent(next, null)) {
                    move(path.get(i));
                }
            }
            tempo();
        }
    }

    private boolean pushAgent(Position newPos, List<Integer> previous) {
        Agent a = _board.getAgent(newPos);
        if(a != null) {
            //Check if there is a message
            Message message = RetrieveRequest();
            if(message != null) {
                SendResponse(message.getSender(), message.getPosition(), true);
            }
            contact c = new contact(a);
            if(!contacted.contains(c)) {
                contacted.add(c);
                SendRequest(a.getAgentId(), newPos, previous);
                return HandleResponse(WaitResponse());
            }
            return false;
        }
        return true;
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
            tempo();
            message = RetrieveResponse();
        }
        System.out.println(getAgentId() + " get response " + ((message == null) ? "*" : message.getAction()));
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
        }
        return false;
    }

    private void tempo() {
        long tps = 20 + (long)(Math.random() * 80);
        try {
            sleep(tps);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class contact {
        Position position;
        int agentId;

        contact(Agent a) {
            this(a.getAgentId(), a.getPosition());
        }

        contact(int id, Position p) {
            position = new Position(p);
            agentId = id;
        }

        @Override
        public boolean equals(Object obj){
            if(this == obj)
                return true;
            if(obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final contact c = (contact) obj;
            if(id != c.agentId)
                return false;
            return c.position.equals(position);
        }

        @Override
        public int hashCode(){
            return Objects.hash(agentId, position);
        }
    }
}
