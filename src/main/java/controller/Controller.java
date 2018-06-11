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

public class Controller implements Observer {

    private static double gridWidth = 50;
    private static double gridHeight = 50;

    private static double windowWidth = 800;
    private static double windowHeigth = 533;

    private static final String defaultImg = "default.jpg";

    private Main main;

    private Board board;

    private Image[][] images;

    @FXML
    private GridPane gridPane;

    public Controller() {
        board = new Board(5,5);
        board.add(0,0, 4,4, "etoile.jpg");
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
        gridPane.setGridLinesVisible(true);
        gridPane.setPadding(new Insets(3, 3, 3, 3));

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
        Platform.runLater(() -> {
            System.out.println("test");
            Position[] positions = (Position[]) (o);
            Position oldPos = positions[0];
            Position newPos = positions[1];

/*            int length = board.getLength(), heigth = board.getHeight();

            ObservableList<Node> children = gridPane.getChildren();
            int oldIndex = (oldPos.getX() * length) + oldPos.getY();
            children.set(oldIndex, getImageView(defaultImg));
            int newIndex = (newPos.getX() * length) + newPos.getY();
            children.set(newIndex, getImageView("etoile.jpg"));
            */

            gridPane.add(getImageView(defaultImg), oldPos.getX(), oldPos.getY());
            gridPane.add(getImageView("etoile.jpg"), newPos.getX(), newPos.getY());
            gridPane.setPadding(new Insets(3, 3, 3, 3));
            gridPane.setGridLinesVisible(true);

            System.out.println("End Test");
        });
    }

    public void stop(){
        board.stop();
    }
}
