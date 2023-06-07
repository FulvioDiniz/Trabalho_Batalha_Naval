package batalha_naval.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TelaInicialController implements Initializable {

    @FXML
    private AnchorPane Fundo;

    @FXML
    private Button NovoJogo;

    @FXML
    private Button Cadastrar;

    @FXML
    private Button Ranking;

    @FXML
    private Button Sair;

    private Stage stageIniciarJogo;

    @FXML
    void ButtonNovoJogo(ActionEvent event) {
        if (stageIniciarJogo.getOwner() == null) {
            stageIniciarJogo.initOwner((Stage) Fundo.getScene().getWindow());
            ((Button) event.getSource()).getScene().getWindow().hide();
        }
        stageIniciarJogo.showAndWait();

    }

    @FXML
    void ButtonCadastrar(ActionEvent event) {

    }

    @FXML
    void ButtonRanking(ActionEvent event) {

    }

    @FXML
    void ButtonSair(ActionEvent event) {
        if (event.getSource() == Sair) {
            System.exit(0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Parent parent;
        FXMLLoader fxmlLoader;
        try {
            stageIniciarJogo = new Stage();
            fxmlLoader = new FXMLLoader(getClass().getResource("/resources/poov/telas/TelaBatalhaNaval.fxml"));
            parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            stageIniciarJogo.setScene(scene);
            stageIniciarJogo.setTitle("Batalha Naval");
            stageIniciarJogo.setResizable(false);
            stageIniciarJogo.initModality(Modality.APPLICATION_MODAL);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("ERRO");
            alert.setHeaderText("Erro");
            alert.setContentText("Erro carregando a aplicação!");
            alert.showAndWait();
            // closes the javafx application
            Platform.exit();

        }

    }
}