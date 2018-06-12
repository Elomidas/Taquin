package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import model.Board;
import model.Main;

public class SettingsController extends Controller {

    /**
     * Objets FXML
     */
    @FXML
    private Spinner<Integer> spinnerSize;

    @FXML
    private Spinner<Integer> spinnerAgents;

    @FXML
    private Button launch;

    /**
     * Constructeur par défaut
     */
    public SettingsController(){
        //Nothing
    }

    /**
     * Fonction d'initialisation de notre fenêtre
     * Initialisation de nos spinners
     */
    @FXML
    private void initialize(){

        // Value factory.
        int sizeMax = 8;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, sizeMax, 5, 1);

        // Initialisation des 2 spinners
        spinnerSize.setValueFactory(valueFactory);

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, sizeMax*sizeMax -1, 15, 1);

        spinnerAgents.setValueFactory(valueFactory);

    }

    /**
     * Lancement du jeu
     */
    private void launchGame(){
        Platform.runLater(() -> {
            // Creation du board

            int size = spinnerSize.getValue();
            board = new Board(size, size);

            int maxi = Math.min(spinnerAgents.getValue(), (size*size)-1);
            for(int i=0;(i < maxi); i++){
                board.add();
            }
            main.startGame(board);
        });
    }

    /**
     * Méthode setMain() redéfinie
     * @param main
     * @param board
     */
    @Override
    public void setMain(Main main, Board board) {
        super.main = main;
        launch.setOnMouseClicked(MouseEvent -> launchGame());
    }

    /**
     * Méthode stop() redéfinie
     */
    @Override
    public void stop() {
        //Nothing atm
    }
}
