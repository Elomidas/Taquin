package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
                StackPane stackPane;
                if(!board.isFree(i,j)){
                    stackPane = getRectangle(board.getAgent(i, j).getAgentId() + "");
                } else {
                    stackPane = getRectangle("");
                }
                gridPane.add(stackPane, j, i);
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

    private StackPane getRectangle(String content){
        Label label = new Label(content);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(gridWidth);
        rectangle.setHeight(gridHeight);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.WHITE);
        return new StackPane(rectangle, label);
    }

    @Override
    public void update(Observable observable, Object o) {

        Position[] positions = (Position[]) (o);
        Position oldPos = new Position(positions[0]);
        Position newPos = new Position(positions[1]);
        Platform.runLater(() -> {
            this.updateDisplay(oldPos, newPos);
        });
    }

    private void updateDisplay(Position oldPos, Position newPos) {
        StackPane newStackPane = getRectangle(board.getAgent(newPos).getAgentId() + (board.checkCase(newPos) ? "+" : ""));
        gridPane.add(this.getRectangle(""), oldPos.getY(), oldPos.getX());
        gridPane.add(newStackPane, newPos.getY(), newPos.getX());
        if(board.finish()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Taquin résolu !");
            alert.setContentText("Tous les agents ont rejoint leurs places.");
            alert.show();
        }
    }

    public void stop(){
        board.stop();
    }
}
