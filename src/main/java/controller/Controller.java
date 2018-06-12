package controller;

import model.Board;
import model.Main;

public abstract class Controller {

    protected Main main;

    protected Board board;

    public abstract void setMain(Main main, Board board);

    public abstract void stop();
}
