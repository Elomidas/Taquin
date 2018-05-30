package model;

public class Position {

    private int px, py;

    public Position(int x, int y) {
        setPos(x, y);
    }

    public int getX() {
        return px;
    }

    public int getY() {
        return py;
    }

    public void setX(int x) {
        px = x;
    }

    public void setY(int y) {
        py = y;
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }
}
