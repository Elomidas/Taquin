package model;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Pair;
import model.path.Graph;

import java.util.*;

public class Board extends Observable implements Runnable {
    private ArrayList<Agent> agents;
    private int height, length;
    private boolean token;
    private List<Position> posStart, posEnd;
    private Position bestBlank;
    private int currentPriority, counter;
    private HashMap<Position, Integer> priorityPos, positionWanted;
    private HashMap<Integer, Position> priorityValue;

    public Board() {
        this(5, 5);
    }

    public Board(int sizeX, int sizeY) {
        agents = new ArrayList<>();
        height = sizeY;
        length = sizeX;
        Graph.init(height, length);
        Agent.setPlateau(this);
        posStart = new ArrayList<>();
        posEnd = new ArrayList<>();
        currentPriority = 0;
        priorityPos = new HashMap<>();
        positionWanted = new HashMap<>();
        priorityValue = new HashMap<>();
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                posStart.add(new Position(i, j));
                posEnd.add(new Position(i, j));
            }
        }
    }

    public synchronized boolean getToken() {
        if(token) {
            token = false;
            return true;
        }
        return false;
    }

    public synchronized void giveToken() {
        token = true;
    }

    public Agent getAgent(int index) {
        if(index < agents.size()) {
            return agents.get(index);
        }
        return null;
    }

    public int size() {
        return agents.size();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void add() {
        System.out.println("Added");
        int start = (int)Math.floor(Math.random() * posStart.size()),
                end = (int)Math.floor(Math.random() * posEnd.size());
        Position pStart = posStart.get(start),
                pEnd = posEnd.get(end);
        posStart.remove(start);
        posEnd.remove(end);
        positionWanted.put(pEnd, agents.size());
        agents.add(new Agent(pStart, pEnd));
    }

    public boolean checkPosition(Position pos) {
        return ((pos.getX() >= 0)
                && (pos.getX() < length)
                && (pos.getY() >= 0)
                && (pos.getY() < height));
    }

    public boolean isFree(Position pos) {
        return isFree(pos.getX(), pos.getY());
    }

    public boolean isFree(int x, int y) {
        return (getAgent(x, y) == null);
    }

    public int getId(Position pos) {
        return getId(pos.getX(), pos.getY());
    }

    public int getId(int x, int y) {
        Agent a = getAgent(x, y);
        return (a == null) ? -1 : a.getAgentId();
    }

    public Agent getAgent(Position pos) {
        return getAgent(pos.getX(), pos.getY());
    }

    public Agent getAgent(int x, int y) {
        Position position = new Position(x, y);
        for(Agent a : agents) {
            if(a.getPosition().equals(position)) {
                return a;
            }
        }
        return null;
    }

    private Agent getGoal(Position pos) {
        Position position = new Position(pos.getX(), pos.getY());
        for(Agent a : agents) {
            if(a.getTarget().equals(position)) {
                return a;
            }
        }
        return null;
    }

    public boolean finish() {
        for(Agent agent : agents) {
            if(!agent.goodPosition()) {
                return false;
            }
        }
        System.out.println("#####\n###\n# End\n###\n#####");
        return true;
    }

    public synchronized  int getCurrentPriority() {
        if(counter <= 0) {
            updateCurrentPriority();
            counter = 100;
        }
        return currentPriority;
    }

    public synchronized  void updateCurrentPriority() {
        Platform.runLater(() -> {
            int mini = length * height;
            Set<Integer> set = priorityValue.keySet();
            for(int currentPrio : set) {
                //
                /*if(!agent.goodPosition() && (agent.getAgentPriority() < mini)) {
                    mini = agent.getAgentPriority();
                }*/
                Position tmp = priorityValue.get(currentPrio);
                if((getGoal(tmp) != null) || (getAgent(tmp) != null)) {
                    Agent a = getAgent(tmp);
                    if((a == null) || (!a.goodPosition())) {
                        if(currentPrio < mini) {
                            mini = currentPrio;
                        }
                    }
                }
            }
            if(currentPriority != mini) {
                System.out.println("###\n# Priority : " + currentPriority + " => " + mini + "\n###");
                currentPriority = mini;
            }
        });
    }

    public synchronized Position getBestBlank() {
        Queue<Position> queue = new PriorityQueue<>(
                20,
                Comparator.comparingDouble(p -> p.Manhattan(bestBlank))
        );
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                if(isFree(i, j)) {
                    queue.add(new Position(i, j));
                }
            }
        }
        return queue.poll();
    }

    @Override
    public void setChanged(){
        super.setChanged();
    }

    //TODO
    public void stop(){
        Agent.setRunnable(false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Agent.setRunnable(true);
    }

    @Override
    public void run() {
        token = true;
        computePriority();
        updateCurrentPriority();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Agent.setRunnable(true);
        for(Agent agent : agents) {
            agent.start();
        }
    }

    private void computePriority() {
        Graph.direction order[] = new Graph.direction[] {Graph.direction.up, Graph.direction.right, Graph.direction.down, Graph.direction.left};
        boolean[][] tab = new boolean[height][length];
        List<Position> visited = new ArrayList<>();
        bestBlank = new Position(0, 0);
        Position center = new Position(height / 2, length / 2);
        int score = 0;
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                tab[i][j] = true;
                Position p = new Position(i, j);
                if(!visited.contains(p)) {
                    if(getAgent(p) == null) {
                        Set<Position> result = inspect(p);
                        if(result.size() > score) {
                            score = result.size();
                            int manhattan = height * length;
                            for(Position pos : result) {
                                int m = center.Manhattan(pos);
                                if(m < manhattan) {
                                    manhattan = m;
                                    bestBlank = pos;
                                }
                            }
                        }
                        visited.addAll(result);
                    } else {
                        visited.add(p);
                    }
                }
            }
        }
        visited = new ArrayList<>();
        int priority = height * length, sense = 0;
        Position current = new Position(bestBlank);
        while(current != null) {
            Agent a = getGoal(current);
            tab[current.getX()][current.getY()] = false;
            if(a != null) {
                a.setAgentPriority(priority);
            }
            priorityValue.put(priority, current);
            priorityPos.put(current, priority);
            priority--;
            visited.add(current);
            int nsense = nextSense(sense);
            Position next = getPosition(current, order[nsense]);
            if((next == null) || visited.contains(next)) {
                next = getPosition(current, order[sense]);
                if((next == null) || visited.contains(next)) {
                    sense = nextSense(nsense);
                    next = getPosition(current, order[sense]);
                    if(visited.contains(next)) {
                        next = null;
                    }
                }
            }
            current = next;
        }
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if(tab[i][j]) {
                    Agent a = getGoal(new Position(i, j));
                    if(a != null) {
                        a.setAgentPriority(priority);
                        priority--;
                    }
                    tab[i][j] = false;
                }
            }
        }
        currentPriority = priority;
    }

    private int nextSense(int sense) {
        return (sense < 3) ? sense + 1 : 0;
    }

    private Position getPosition(Position p, Graph.direction order) {
        Position npos;
        switch (order) {
            case up:
                npos = new Position(p.getX() - 1, p.getY());
                break;
            case right:
                npos = new Position(p.getX(), p.getY() + 1);
                break;
            case down:
                npos = new Position(p.getX() + 1, p.getY());
                break;
            default:
                npos = new Position(p.getX(), p.getY() - 1);
        }
        if(!checkPosition(npos)) {
            npos = null;
        }
        return npos;
    }

    private Set<Position> inspect(Position position) {
        Stack<Position> stack = new Stack<>();
        Set<Position> visited = new HashSet<>();
        stack.push(position);
        int i = 0;
        while(!stack.empty()) {
            i++;
            Position current = stack.pop();
            for(Position p : current.getAdjacency()) {
                if(checkPosition(p) && (getAgent(p) == null) && !visited.contains(p)) {
                    stack.push(p);
                }
            }
            visited.add(current);
        }
        return visited;
    }

}
