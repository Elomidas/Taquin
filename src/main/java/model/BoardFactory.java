package model;

import java.util.ArrayList;

public class BoardFactory {
    static public Board getNewBoard(int size, int agents) {

        Board board = new Board(size, size);

        ArrayList<Position> posStart = new ArrayList<>(),
                posEnd = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                posStart.add(new Position(i, j));
                posEnd.add(new Position(i, j));
            }
        }


        for(int i = 0; i < agents; i++) {
            int start = (int) Math.floor(Math.random() * posStart.size()),
                    end = (int) Math.floor(Math.random() * posEnd.size());
            Position pStart = posStart.get(start),
                    pEnd = posEnd.get(end);
            posStart.remove(start);
            posEnd.remove(end);
            board.add(pStart, pEnd);
        }

        board.computePriority();

        board.printGoal();

        board.Simplify();

        board.printGoal();
/*
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        Agent.setRunnable(true);

        return board;
    }
}
