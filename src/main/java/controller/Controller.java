package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Board;
import model.Main;
import model.Position;

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
        board = new Board(5,5);
        board.add(2,2, 3,3, "etoile.jpg");
        board.add(4,0, 0,4, "etoile.jpg");
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

        for(int i=0;i<board.getLength();i++){
            gridPane.addRow(i);

            for(int j = 0; j<board.getHeight(); j++){
                gridPane.addColumn(j);
                if(!board.isFree(i,j)){
                    images[i][j] = new Image(board.getAgent(i,j).getImg());
                }
                else
                    images[i][j] = new Image(defaultImg);

                ImageView imageView = new ImageView(images[i][j]);
                imageView.setFitWidth(gridWidth*70/100);
                imageView.setFitHeight(gridHeight*70/100);
                gridPane.add(imageView, j, i);
            }
        }
        gridPane.setGridLinesVisible(true);
    }

    private void draw(){
        for(int i=0;i<board.getLength();i++){
            for(int j = 0; j<board.getHeight(); j++){
                Image image;
                if(!board.isFree(i,j)){
                    image = new Image(board.getAgent(i,j).getImg());
                }
                else
                    image = new Image(defaultImg);

                ImageView imageView = new ImageView(image);
                //imageView.setFitWidth(gridWidth);
                //imageView.setFitHeight(gridHeight);
                gridPane.add(imageView, j, i);
            }
        }
    }

    public void setMain(Main main){
        this.main = main;
        this.board.addObserver(this);
        board.start();
    }

    @Override
    public void update(Observable observable, Object o) {
        Platform.runLater(() -> {
            System.out.println("test");
            //gridPane.getChildren().clear();
            Position[] positions = (Position[]) (o);
            Position oldPos = positions[0];
            Position newPos = positions[1];

            ImageView imageView = new ImageView("etoile.jpg");
            imageView.setFitWidth(gridWidth*70/100);
            imageView.setFitHeight(gridHeight*70/100);

            gridPane.getChildren().remove(gridPane.getChildren().get((newPos.getY()+1) * (newPos.getX()+1)));
            //gridPane.add(imageView, newPos.getY(), newPos.getX());
            //gridPane.setGridLinesVisible(true);
            //draw();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void stop(){
        board.stop();
    }
}
