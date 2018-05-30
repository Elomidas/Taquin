package model;

import java.util.Objects;

public class Position extends Thread {

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

    protected void Compute() {
        //TODO
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
}
