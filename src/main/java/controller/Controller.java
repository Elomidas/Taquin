package controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Board;
import model.Main;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {

    private static double gridWidth = 50;
    private static double gridHeight = 50;

    private static double windowWidth = 800;
    private static double windowHeigth = 533;

    private static final String defaultImg = "default.png";

    private Main main;

    private Board board;

    private Image[][] images;

    @FXML
    private GridPane gridPane;

    public Controller() {
        this.board = new Board();
    }

    @FXML
    private void initialize(){

        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.setPrefSize(gridWidth*board.getLength(), gridHeight*board.getHigh());

        gridPane.setLayoutX(( windowWidth-gridPane.getPrefWidth() )/2);
        gridPane.setLayoutY(( windowHeigth-gridPane.getPrefHeight() )/2);

        images = new Image[board.getLength()][board.getHigh()];

        for(int i=0;i<board.getLength();i++){
            gridPane.addRow(i);

            for(int j=0;j<board.getHigh();j++){
                gridPane.addColumn(j);
                if(!board.isFree(i,j)){
                    images[i][j] = new Image(board.getAgent(i,j).getImg());
                }
                else
                    images[i][j] = new Image(defaultImg);

                ImageView imageView = new ImageView(images[i][j]);
                imageView.setFitWidth(gridWidth);
                imageView.setFitHeight(gridHeight);
                gridPane.add(imageView, i, j);
            }
        }
        gridPane.setGridLinesVisible(true);
    }

    public void setMain(Main main){
        this.main = main;
        this.board.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
