package model;

import java.util.Stack;

public class BoardSimplifier {
    private Stack<Operation> operations;

    void simplify(Board origin) {

        Position center = origin.getCenter();
        operations = new Stack<>();
        if(origin.getGoal(center) != null) {
            Position best = new Position(0, 0);
            int manhattan = Integer.MAX_VALUE;
            for(int i = 0; i < origin.getHeight(); i++) {
                for(int j = 0; j < origin.getLength(); j++) {
                    Position pos = new Position(i, j);
                    if(origin.getGoal(pos) == null) {
                        int tmp = center.Manhattan(pos);
                        if(tmp < manhattan) {
                            best = pos;
                            manhattan = tmp;
                        }
                    }
                }
            }
            int di = (best.getX() > center.getX()) ? -1 : 1;
            int dj = (best.getY() > center.getY()) ? -1 : 1;
            int ni = di, nj = 0;
            for(int i = best.getX(), j = best.getY();
                (i != center.getX()) || (j != center.getY());
                i += ni, j += nj) {
                if(i == center.getX()) {
                    ni = 0;
                    nj = dj;
                }
                Position next = new Position(i+ni, j+nj);
                Agent agent = origin.getGoal(next);
                agent.setTarget(best);
                operations.add(new Operation(ni, nj));
                best = next;
            }
        }
    }

    void restore(Board origin) {
        Position current = origin.getCenter();
        while(!operations.empty()) {
            Operation operation = operations.pop();
            operation.invert();

            Position next = new Position(current.getX() + operation.di, current.getY() + operation.dj);
            Agent agent = origin.getGoal(next);
            agent.setTarget(current);
            current = next;
        }
    }

    class Operation {
        int di, dj;

        Operation(int i, int j) {
            di = i;
            dj = j;
        }

        public void invert() {
            di = -di;
            dj = -dj;
        }
    }
}
