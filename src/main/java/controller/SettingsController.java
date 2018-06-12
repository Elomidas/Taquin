package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import model.Board;
import model.Main;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class SettingsController extends Controller {

    @FXML
    private Spinner<Integer> spinnerSize;

    @FXML
    private Spinner<Integer> spinnerAgents;

    @FXML
    private Button launch;

    public SettingsController(){
        //Nothing
    }

    @FXML
    private void initialize(){

        // Value factory.
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 6, 5, 1);

        // Initialisation des 2 spinners
        spinnerSize.setValueFactory(valueFactory);

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 5, 1);

        spinnerAgents.setValueFactory(valueFactory);

    }

    private void launchGame(){
        Platform.runLater(() -> {
            // Creation du board

            board = new Board(spinnerSize.getValue(), spinnerSize.getValue());
            List<Position> start = new ArrayList<>(), target = new ArrayList<>();

            for (int i=0;i<board.getLength();i++){
                for (int j=0;j<board.getHeight();j++){
                    start.add(new Position(i, j));
                    target.add(new Position(i, j));
                }
            }
            int nb = 1;
            for(int i=0;i<spinnerAgents.getValue();i++){
                int indStart = (int) (Math.random() * (start.size()));
                int indTarget = (int) (Math.random() * (target.size()));
                board.add(start.get(indStart).getX(), start.get(indStart).getY(), target.get(indTarget).getX(),target.get(indTarget).getY(), nb + ".jpg");
                nb++;
            }
            main.startGame(board);
        });
    }

    @Override
    public void setMain(Main main, Board board) {
        super.main = main;
        launch.setOnMouseClicked(MouseEvent -> launchGame());
    }

    @Override
    public void stop() {
        //Nothing atm
    }
}
