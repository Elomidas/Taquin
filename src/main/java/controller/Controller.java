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
        board.start();
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
        Position oldPos = positions[0];
        Position newPos = positions[1];
        if(!newPos.equals(oldPos)) {
            modifs.offer(new modif(oldPos.getX(), oldPos.getY(), false));
            modifs.offer(new modif(newPos.getX(), newPos.getY(), true));
        }
        Platform.runLater(this::updateDisplay);
    }

    private void updateDisplay() {
        while(!modifs.isEmpty()) {
            modif m = modifs.poll();
            if(m != null) {
                gridPane.add(getImageView(m.action ? "etoile.jpg" : defaultImg), m.column, m.line);
            }
        }
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
