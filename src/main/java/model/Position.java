package model;

import java.util.Objects;

public class Position {

    private int px, py;

    public Position(int x, int y) {
        setPos(x, y);
    }

    public Position(Position pos) {
        this(pos.getX(), pos.getY());
    }

    public int getX() {
        return px;
    }

    public int getY() {
        return py;
    }

    private void setX(int x) {
        px = x;
    }

    private void setY(int y) {
        py = y;
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Position pos = (Position) obj;
        if(pos.getX() != this.getX())
            return false;
        return pos.getY() == this.getY();
    }

    @Override
    public int hashCode(){
        return Objects.hash(px, py);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(px)
                .append(",")
                .append(py)
                .append("]");
        return builder.toString();
    }

    public int Manhattan(Position pos) {
        return (Math.abs(px - pos.getX())) + Math.abs(py - pos.getY());
    }

    public Position[] getAdjacency() {
        return new Position[] {
                new Position(px - 1, py),
                new Position(px, py + 1),
                new Position(px + 1, py),
                new Position(px, py - 1)
        };
    }
}
