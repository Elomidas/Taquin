package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Board;
import model.Main;
import model.Position;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller implements Observer {

    private static double gridWidth = 50;
    private static double gridHeight = 50;

    private static double windowWidth = 800;
    private static double windowHeigth = 533;

    private static final String defaultImg = "default.jpg";

    private ConcurrentLinkedQueue<modif> modifs;
    private Thread t;

    private Main main;

    private Board board;

    private Image[][] images;

    @FXML
    private GridPane gridPane;

    public Controller() {
        board = new Board(5,5);
        board.add(0,4, 4,0, "etoile.jpg");
        board.add(0,0, 4,4, "etoile.jpg");
        board.add(4,0, 0,4, "etoile.jpg");
        board.add(4,4, 0,0, "etoile.jpg");
        board.add(1,1, 2,2, "etoile.jpg");
        board.add(2,1, 3,4, "etoile.jpg");
        //board.add(4,4, 0,0, "etoile.jpg");
        modifs = new ConcurrentLinkedQueue<>();
    }

    @FXML
    private void initialize(){

        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.setPrefSize(gridWidth*board.getLength(), gridHeight*board.getHeight());

        gridPane.setLayoutX(( windowWidth-gridPane.getPrefWidth() )/2);
        gridPane.setLayoutY(( windowHeigth-gridPane.getPrefHeight() )/2);

        images = new Image[board.getLength()][board.getHeight()];

        this.draw();

    }

    private void draw(){
        for(int i=0;i<board.getLength();i++) {

            for(int j = 0; j<board.getHeight(); j++){
                gridPane.addColumn(j);
                ImageView img;
                if(!board.isFree(i,j)){
                    img = getImageView(board.getAgent(i,j).getImg());
                } else {
                    img = getImageView(defaultImg);
                }
                gridPane.add(img, j, i);
            }
        }
    }

    public void setMain(Main main){
        this.main = main;
        this.board.addObserver(this);
        t = new Thread(board);
        t.start();
    }

    private ImageView getImageView(String name) {
        ImageView imageView = new ImageView(name);
        imageView.setFitWidth(gridWidth);
        imageView.setFitHeight(gridHeight);
        return imageView;
    }

    @Override
    public void update(Observable observable, Object o) {

        Position[] positions = (Position[]) (o);
        Position oldPos = new Position(positions[0]);
        Position newPos = new Position(positions[1]);
        Platform.runLater(() -> {
            this.updateDisplay(oldPos, newPos);
            board.giveToken();
        });
    }

    private void updateDisplay(Position oldPos, Position newPos) {
        ImageView newImg = getImageView(board.getAgent(newPos).getImg());
        gridPane.add(this.getImageView(defaultImg), oldPos.getY(), oldPos.getX());
        gridPane.add(newImg, newPos.getY(), newPos.getX());
    }

    public void stop(){
        board.stop();
    }

    class modif {
        public int line, column;
        public boolean action;

        public modif(int px, int py, boolean a) {
            line = px;
            column = py;
            action = a;
        }
    }
}
