package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Board;
import model.Main;
import model.Position;

import java.util.Observable;
import java.util.Observer;

public class GameController extends Controller implements Observer {

    private static final double gridWidth = 50;
    private static final double gridHeight = 50;

    private static final double windowWidth = 800;
    private static final double windowHeigth = 533;

    private static final String defaultImg = "default.jpg";

    /**
     * Thread utilisé pour gérer le board
     */
    private Thread t;

    @FXML
    private GridPane gridPane;

    public GameController() {
        //Nothing
    }

    @FXML
    private void initialize(){
        //Nothing
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

    private void init(){

        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.setPrefSize(gridWidth*board.getLength(), gridHeight*board.getHeight());

        gridPane.setLayoutX(( windowWidth-gridPane.getPrefWidth() )/2);
        gridPane.setLayoutY(( windowHeigth-gridPane.getPrefHeight() )/2);

        this.draw();
    }

    public void setMain(Main main, Board board){
        super.main = main;
        super.board = board;
        this.init();
        super.board.addObserver(this);
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
}
