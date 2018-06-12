package model;

import javafx.application.Platform;
import model.communication.Message;
import model.communication.Messages;
import model.path.Graph;

import java.util.*;

public class Board extends Observable implements Runnable {

    public enum corner {tl, tr, br, bl, none}
    private ArrayList<Agent> agents;
    private int height, length;
    private List<Position> posStart, posEnd;
    private int currentPriority;
    private HashMap<Position, Integer> priorityPos;
    private HashMap<Integer, Position> priorityValue;

    //TODO
    //Centrer une case vide et refaire le taquin après

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
        currentPriority = 100;
        priorityPos = new HashMap<>();
        priorityValue = new HashMap<>();
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < length; j++) {
                posStart.add(new Position(i, j));
                posEnd.add(new Position(i, j));
            }
        }
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
        int start = (int)Math.floor(Math.random() * posStart.size()),
                end = (int)Math.floor(Math.random() * posEnd.size());
        Position pStart = posStart.get(start),
                pEnd = posEnd.get(end);
        posStart.remove(start);
        posEnd.remove(end);
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

    public synchronized boolean checkCase(Position pos) {
        Agent a = getAgent(pos), g = getGoal(pos);
        return a == g;
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

    public synchronized int getCurrentPriority() {
        return currentPriority;
    }

    public synchronized int getPriority(Position p) {
        return priorityPos.get(p);
    }

    private boolean checkMini(int mini) {
        return (currentPriority < mini) || (currentPriority > (mini + 1));
    }

    public synchronized void updateCurrentPriority() {
        Platform.runLater(() -> {
            //Recherche de la case la plus prioritaire non satisfaite
            int mini = length * height;
            Set<Integer> set = priorityValue.keySet();
            for (int currentPrio : set) {
                Position tmp = priorityValue.get(currentPrio);
                if ((getGoal(tmp) != null) || (getAgent(tmp) != null)) {
                    Agent a = getGoal(tmp);
                    if ((a != null) && (!a.goodPosition())) {
                        if ((currentPrio < mini) && (checkMini(currentPrio))) {
                            mini = currentPrio;
                        }
                    }
                }
            }

            //Changement de la priorité
            if(checkMini(mini)) {
                System.out.println("###\n# Priority : " + currentPriority + " => " + mini + "\n###");
                currentPriority = mini;
            } else {
                System.out.println("NOT UPDATED");
            }
        });
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
        computePriority();
        updateCurrentPriority();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Agent.setRunnable(true);
        for(Agent agent : agents) {
            agent.start();
        }
    }

    private void computePriority() {
        int priority = 1;
        int iMin = 0,
                iMax = height -1,
                jMin = 0,
                jMax = length - 1;
        for(int i = iMin, j = jMin, di = 0, dj = 1;
            (iMax >= iMin) && (jMax >= jMin);
            j += dj, i += di) {
            priority = prio(i, j, priority);
            if((di == 1) && (i == iMax)) {
                di = 0;
                dj = -1;
                jMax--;
            } else if((di == -1) && (i == iMin)) {
                di = 0;
                dj = 1;
                jMin++;
            } else if((dj == 1) && (j == jMax)) {
                dj = 0;
                di = 1;
                iMin++;
            } else if((dj == -1) && (j == jMin)) {
                dj = 0;
                di = -1;
                iMax--;
            }
        }
    }

    private int prio(int i, int j, int prio) {
        Position p = new Position(i, j);
        Agent a = getGoal(p);
        if(a != null) {
            a.setAgentPriority(prio);
        }
        priorityValue.put(prio, p);
        priorityPos.put(p, prio);
        Graph.setPrio(p, prio);
        return prio + 1;
    }

    private boolean XOR(boolean a, boolean b) {
        return (a || b) && !(a && b);
    }

    private int modifSense(int sense, int modif) {
        int nsense = sense + modif;
        while(nsense < 0) {
            nsense += 4;
        }
        while(nsense > 3) {
            nsense -= 4;
        }
        return nsense;
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

    public synchronized corner getCorner(Position position) {
        Position positions[] = position.getAdjacency();
        int prio = priorityPos.get(position);
        int count = 0;
        for(Position p : positions) {
            if((!checkPosition(p)) || (priorityPos.get(p) < prio)) {
                count--;
            }
        }
        if(count < -2) {
            boolean b = false, r = false;
            if(position.getX() > ((height / 2) + (height % 2))) {
                b = true;
            }
            if(position.getY() > ((length / 2) + (length % 2))) {
                r = true;
            }
            if(b && r) {
                return corner.br;
            }
            if(b) {
                return corner.bl;
            }
            if(r) {
                return corner.tr;
            }
            return corner.tl;
        }
        return corner.none;
    }

}
