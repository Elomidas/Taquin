package controller;

import model.Board;
import model.Main;

public abstract class Controller {

    /**
     * Main utilisé dans les controlleurs
     */
    protected Main main;

    /**
     * Plateau utilisé dans le controlleur
     */
    protected Board board;

    /**
     * Méthode setMain() redéfinie
     * @param main
     * @param board
     */
    public abstract void setMain(Main main, Board board);

    /**
     * Méthode Stop() redéfinie
     */
    public abstract void stop();
}
