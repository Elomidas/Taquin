package model.moves;

import model.Agent;
import model.Position;

public class MoveStrategy {
    private int horizontally, vertically;

    /**
     * Move strategy
     * @param h Horizontal delta
     * @param v Vertical delta
     */
    public MoveStrategy(int h, int v) {
        horizontally = h;
        vertically = v;
    }

    /**
     * Move the agent given the horizontally and vertically deltas
     * @param agent Agent to move
     * @return true if agent has been moved, false else
     */
    public boolean move(Agent agent) {
        Position newPos = new Position(agent.getPosition().getX() + horizontally, agent.getPosition().getY() + vertically);
        if(Agent.getPlateau().checkPosition(newPos)) {
            agent.setPosition(newPos);
            return true;
        }
        return false;
    }
}
