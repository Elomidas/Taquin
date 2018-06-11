package model.moves;

import model.Agent;
import model.Position;

public class MoveStrategy {
    private int horizontally, vertically;
    public final static MoveStrategy _up = new MoveStrategy(0, 1),
                                    _down = new MoveStrategy(0, -1),
                                    _left = new MoveStrategy(-1, 0),
                                    _right = new MoveStrategy(1, 0);

    /**
     * Move strategy
     * @param h Horizontal delta
     * @param v Vertical delta
     */
    private MoveStrategy(int h, int v) {
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
        if(Agent.getPlateau().checkPosition(newPos) && Agent.getPlateau().isFree(newPos.getX(), newPos.getY())) {
            agent.setPosition(newPos);
            return true;
        }
        return false;
    }
}
