package batalha_naval.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import batalha_naval.dao.ConexaoFactoryPostgreSQL;
import batalha_naval.dao.PessoaDAO;
import batalha_naval.dao.core.DAOFactory;
import batalha_naval.model.Barco;
import batalha_naval.model.Filter.PessoaFilter;

public class TelaBatalhaNavalController implements Initializable, Runnable {

    @FXML
    private Button ButtonPosicionar2;

    @FXML
    private Button ButtonPosicionar1;

    @FXML
    private Text TituloLabel;

    @FXML
    private Button ButtonCoura1;

    @FXML
    private Button ButtonCoura2;

    @FXML
    private Button ButtonPorta1;

    @FXML
    private Button ButtonPorta2;

    @FXML
    private Button ButtonSub1;

    @FXML
    private Button ButtonSub2;

    @FXML
    private Text labelNomeJogador2;

    @FXML
    private Text LabelNomeJogador1;

    @FXML
    private Text LabelTempo;

    @FXML
    private Text LabelAcertoJogador1;

    @FXML
    private Text LabelAcertoJogador2;

    @FXML
    private Text LabelErrosJogador12;

    @FXML
    private Text LabelErrosJogador2;

    @FXML
    private GridPane GridPane1;

    @FXML
    private GridPane GridPane2;

    @FXML
    private Text LabelPontosJogador1;

    @FXML
    private Text LabelPontosJogador2;

    @FXML
    private Text NomeJogador1;

    @FXML
    private Label Labeltext;

    private Button[][] buttons1;
    private Button[][] buttons2;
    private Instant tempoinicial = Instant.now();
    private Duration duracao;
    private PessoaFilter Jogadorfilter1;
    private PessoaFilter Jogadorfilter2;
    private String nomeJogador1;
    private String nomeJogador2;
    private DAOFactory daoFactory;
    private Barco barco;
    private String nomeBarco;
    private Stage primaryStage;
    private int contadordeSubmarinos = 0;
    private int contadordePortaAvioes = 0;
    private int contadordeCouracados = 0;
    // private boolean validador = false;
    private boolean validadorPosicionamento = false;
    private threadVerificaBarcos verificaBarcos;
    private threadVerificaBarcos2 verificaBarcos2;
    private boolean trocaJogador = false;

    public void setNomeJogador1(String text) {
        this.nomeJogador1 = text;

    }

    public void setNomeJogador2(String text) {
        this.nomeJogador2 = text;

    }

    public String getNomeJogador1() {
        return nomeJogador1;
    }

    public String getNomeJogador2() {
        return nomeJogador2;
    }

    public void setNomeBarco(String text) {
        this.nomeBarco = text;
    }

    public String getNomeBarco() {
        return nomeBarco;
    }

    public void inicializarCampos() {
        LabelPontosJogador1.setText("0");
        LabelPontosJogador2.setText("0");
        LabelAcertoJogador1.setText("0");
        LabelAcertoJogador2.setText("0");
        LabelErrosJogador12.setText("0");
        LabelErrosJogador2.setText("0");
        // TituloLabel.setText("Posicione o seu barco Jogador " + getNomeJogador1());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage(Stage primaryStage) {
        return primaryStage;
    }

    private void configurarEncerramentoJanela() {
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
            Thread.currentThread().interrupt();
            threadVerificaBarcos.currentThread().interrupt();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttons1 = new Button[10][10];
        buttons2 = new Button[10][10];
        // ButtonPosicionar1.setVisible(true);
        // ButtonPosicionar1.setDisable(false);
        // ButtonPosicionar2.setVisible(true);
        // ButtonPosicionar2.setDisable(false);
        inicializarCampos();
        EventHandler<ActionEvent> buttonClickHandler = new ButtonClickHandler();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Button button1 = new Button();
                button1.setMinSize(50, 50);
                button1.setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                buttons1[row][col] = button1;
                
                button1.setText("button1[" + row + "][" + col + "]]");
                button1.setUserData("Agua");
                button1.setOnMouseEntered(event -> {
                    Button b = (Button) event.getSource();
                    int linha = GridPane.getRowIndex(b);
                    int coluna = GridPane.getColumnIndex(b);
                    if (!trocaJogador && nomeBarco != null) {
                        if (nomeBarco.equals("Submarino") && b.getUserData().equals("Agua")) {
                            boolean validadorSubmarino = true;
                            Boolean validador2 = true;
                            for (int i = 0; i < 2; i++) {
                                if (linha - i >= 0) { // Verifica se o índice é válido
                                    Button botaoAcima = buttons1[linha - i][coluna];
                                    if (!botaoAcima.getUserData().equals("Agua")) {
                                        validador2 = false;
                                        break;
                                    }
                                } else {
                                    validador2 = false;
                                    break;
                                }
                            }

                            if (validador2) {
                                for (int j = 0; j < 2; j++) {
                                    if (linha - j >= 0 && coluna >= 0) { // Verifica se os índices são válidos
                                        Button botaoAcima2 = buttons1[linha - j][coluna];
                                        b.setStyle(
                                                "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                                        botaoAcima2.setStyle(
                                                "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");

                                    } else {
                                        validadorSubmarino = false;

                                    }
                                }
                            }
                            if (!validadorSubmarino || !validador2) {
                                b.styleProperty()
                                        .set("-fx-background-color: red; -fx-border-color: black; -fx-text-fill: red");
                            }
                        }
                        if (nomeBarco.equals("PortaAvioes") && b.getUserData().equals("Agua")) {
                            boolean validador3 = true;
                            for (int i = 0; i < 5; i++) {
                                if (linha - i >= 0) { // Verifica se o índice é válido
                                    Button botaoAcima = buttons1[linha - i][coluna];
                                    if (!botaoAcima.getUserData().equals("Agua")) {
                                        validador3 = false;
                                        break;
                                    }
                                } else {
                                    validador3 = false;
                                    break;
                                }
                            }

                            if (validador3) {
                                for (int j = 0; j < 5; j++) {
                                    if (linha - j >= 0 && coluna >= 0) { // Verifica se os índices são válidos
                                        Button botaoAcima2 = buttons1[linha - j][coluna];
                                        b.setStyle(
                                                "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                                        botaoAcima2.setStyle(
                                                "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                                    }
                                }
                            } else {
                                b.setStyle(
                                        "-fx-background-color: red; -fx-border-color: black; -fx-text-fill: red");
                            }
                        }

                        if (nomeBarco.equals("Couracado") && b.getUserData().equals("Agua")) {
                            boolean validador2 = true;
                            for (int i = 0; i < 4; i++) {
                                if (linha - i >= 0) { // Verifica se o índice é válido
                                    Button botaoAcima = buttons1[linha - i][coluna];
                                    if (!botaoAcima.getUserData().equals("Agua")) {
                                        validador2 = false;
                                        break;
                                    }
                                } else {
                                    validador2 = false;
                                    break;
                                }
                            }

                            if (validador2) {
                                for (int j = 0; j < 4; j++) {
                                    if (linha - j >= 0 && coluna >= 0) { // Verifica se os índices são válidos
                                        Button botaoAcima2 = buttons1[linha - j][coluna];
                                        b.setStyle(
                                                "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                                        botaoAcima2.setStyle(
                                                "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                                    }
                                }
                            } else {
                                b.setStyle(
                                        "-fx-background-color: red; -fx-border-color: black; -fx-text-fill: red");
                            }
                        }
                    }
                });
                button1.setOnMouseExited(event -> {
                    Button b = (Button) event.getSource();
                    int linha = GridPane.getRowIndex(b);
                    int coluna = GridPane.getColumnIndex(b);
                    if (nomeBarco != null) {
                        if (nomeBarco.equals("Submarino")) {
                            for (int i = 0; i < 2; i++) {
                                if (linha - i >= 0 || coluna - i >= 0) {
                                    Button botaoAcima = buttons1[linha - i][coluna];
                                    b.setStyle(
                                            "-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                                    botaoAcima.setStyle(
                                            "-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                                }
                            }
                        }
                        if (nomeBarco.equals("Couracado")) {
                            for (int i = 0; i < 4; i++) {
                                Button botaoAcima = buttons1[linha - i][coluna];
                                b.setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                                botaoAcima.setStyle(
                                        "-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                            }
                        }
                        if (nomeBarco.equals("PortaAvioes")) {
                            for (int i = 0; i < 5; i++) {
                                Button botaoAcima = buttons1[linha - i][coluna];
                                b.setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                                botaoAcima.setStyle(
                                        "-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                            }
                        }
                    }

                });
                button1.setOnAction(buttonClickHandler);
                GridPane1.add(button1, col, row);

                Button button2 = new Button();
                button2.setMinSize(50, 50);
                button2.setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                buttons2[row][col] = button2;
                button2.setText("button2[" + row + "][" + col + "]]");
                button2.setUserData("Agua");
                if (button2.getUserData().equals("Agua")) {
                    button2.setOnMouseEntered(event -> {
                        Button b = (Button) event.getSource();
                        b.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-text-fill: red");
                    });
                    button2.setOnMouseExited(event -> {
                        Button b = (Button) event.getSource();
                        b.setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                    });
                }
                button2.disableProperty().set(true);
                ButtonSub2.disableProperty().set(true);
                ButtonPorta2.disableProperty().set(true);
                ButtonCoura2.disableProperty().set(true);
                ButtonSub2.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-text-fill: black");
                ButtonPorta2.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-text-fill: black");
                ButtonCoura2.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-text-fill: black");
                ButtonSub1.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-text-fill: black");
                ButtonPorta1.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-text-fill: black");
                ButtonCoura1.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-text-fill: black");
                button2.setOnAction(buttonClickHandler);
                GridPane2.add(button2, col, row);
            }
        }
        Thread thread = new Thread(this);
        // thread.start();
        verificaBarcos = new threadVerificaBarcos();
        verificaBarcos2 = new threadVerificaBarcos2();
        // threadVerificaBarcos verificaBarcos = new threadVerificaBarcos();
        verificaBarcos.start();
        // verificaBarcos2.start();
    }

    @FXML
    void ButtonCoura2Clicado(ActionEvent event) {
        nomeBarco = "Couracado";
        setNomeBarco("Couracado");
        System.out.println("Couracado clicado" + contadordeCouracados + "vezes");
        if (contadordeCouracados == 1) {
            ButtonCoura2.disableProperty().set(true);
        } else {
            ButtonCoura2.disableProperty().set(false);
        }
        if (validadorPosicionamento) {
            contadordeCouracados++;
            validadorPosicionamento = false;
        }

    }

    @FXML
    void ButtonPorta2Clicado(ActionEvent event) {
        nomeBarco = "PortaAvioes";
        setNomeBarco("PortaAvioes");
        if (contadordePortaAvioes == 0) {
            ButtonPorta2.disableProperty().set(true);
        }
        if (validadorPosicionamento) {
            contadordePortaAvioes++;
            validadorPosicionamento = false;
        }

    }

    @FXML
    void ButtonSub2Clicado(ActionEvent event) {
        nomeBarco = "Submarino";
        setNomeBarco("Submarino");
        System.out.println("Submarino clicado" + contadordeSubmarinos + "vezes");
        if (contadordeSubmarinos == 4) {
            ButtonSub2.disableProperty().set(true);
        }
        if (validadorPosicionamento) {
            contadordeSubmarinos++;
            validadorPosicionamento = false;
        }

    }

    @FXML
    void ButtonPorta1Clicado(ActionEvent event) {
        nomeBarco = "PortaAvioes";
        setNomeBarco("PortaAvioes");
        if (contadordePortaAvioes == 0) {
            ButtonPorta1.disableProperty().set(true);
        }
        if (validadorPosicionamento) {
            contadordePortaAvioes++;
            validadorPosicionamento = false;
        }
    }

    @FXML
    void ButtonCoura1Clicado(ActionEvent event) {
        nomeBarco = "Couracado";
        setNomeBarco("Couracado");
        System.out.println("Couracado clicado" + contadordeCouracados + "vezes");
        if (contadordeCouracados == 1) {
            ButtonCoura1.disableProperty().set(true);
        } else {
            ButtonCoura1.disableProperty().set(false);
        }
        if (validadorPosicionamento) {
            contadordeCouracados++;
            validadorPosicionamento = false;
        }
    }

    @FXML
    void ButtonSub1Clicado(ActionEvent event) {
        nomeBarco = "Submarino";
        setNomeBarco("Submarino");
        System.out.println("Submarino clicado" + contadordeSubmarinos + "vezes");
        if (contadordeSubmarinos == 4) {
            ButtonSub1.disableProperty().set(true);
        }
        if (validadorPosicionamento) {
            contadordeSubmarinos++;
            validadorPosicionamento = false;
        }

    }

    @FXML
    void ButtonPosicionarClicado1(ActionEvent event) {
        verificaBarcos.interrupt();
        verificaBarcos2.start();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                buttons1[i][j].setDisable(true);
                buttons2[i][j].setDisable(false);
                buttons1[i][j].setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
                // buttons2[i][j].setUserData("Agua");
                ButtonSub2.setDisable(false);
                ButtonPorta2.setDisable(false);
                ButtonCoura2.setDisable(false);
            }
        }
        contadordeCouracados = 0;
        contadordePortaAvioes = 0;
        contadordeSubmarinos = 0;
        validadorPosicionamento = false;
        nomeBarco = "";
        ButtonPosicionar1.setDisable(true);
        trocaJogador = true;

    }

    @FXML
    void ButtonPosicionar2Clicado(ActionEvent event) {
        verificaBarcos2.interrupt();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                buttons2[i][j].setDisable(false);
                buttons1[i][j].setDisable(false);
                buttons2[i][j].setStyle("-fx-background-color: blue; -fx-border-color: black; -fx-text-fill: blue");
            }
        }
        ButtonPosicionar1.setVisible(false);
        ButtonPosicionar2.setVisible(false);

    }

    private class ButtonClickHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Button clickedButton = (Button) event.getSource();
            int row = GridPane.getRowIndex(clickedButton);
            int col = GridPane.getColumnIndex(clickedButton);

            System.out.println("Button clicked: " + row + ", " + col + "valor = " + clickedButton.getUserData());
            if (!trocaJogador && nomeBarco != null) {
                //verificaBarcos.start();
                if (nomeBarco.equals("Submarino") && clickedButton.getUserData().equals("Agua")) {
                    Boolean validador2 = true;
                    Boolean validadorSubmarino = true;

                    for (int i = 0; i < 2; i++) {
                        if (row - i >= 0) { // Verifica se o índice é válido
                            Button botaoAcima = buttons1[row - i][col];
                            if (!botaoAcima.getUserData().equals("Agua")) {
                                validador2 = false;
                                break;
                            }
                        } else {
                            validador2 = false;
                            break;
                        }
                    }

                    if (validador2) {
                        for (int j = 0; j < 2; j++) {
                            if (row - j >= 0 && col >= 0) { // Verifica se os índices são válidos
                                Button botaoAcima2 = buttons1[row - j][col];
                                clickedButton.setUserData("Submarino");
                                botaoAcima2.setUserData("Submarino");

                            } else {
                                validadorSubmarino = false;
                                break;
                            }
                        }
                    }

                    if (!validadorSubmarino || !validador2) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao posicionar o barco");
                        alert.setContentText("O Submarino não pode ser posicionado aqui");
                        if (contadordeSubmarinos > 0) {
                            contadordeSubmarinos--;
                            ButtonSub1.disableProperty().set(false);
                        }

                        alert.showAndWait();
                    } else {
                        validadorPosicionamento = true;
                    }

                    nomeBarco = "";
                }
                if (nomeBarco.equals("PortaAvioes") && clickedButton.getUserData().equals("Agua")) {
                    boolean validadorPorta = true;
                    boolean validador3 = true;
                    for (int i = 0; i < 5; i++) {
                        if (row - i >= 0) { // Verifica se o índice é válido
                            Button botaoAcima = buttons1[row - i][col];
                            if (!botaoAcima.getUserData().equals("Agua")) {
                                validador3 = false;
                                break;
                            }
                        } else {
                            validador3 = false;
                            break;
                        }
                    }

                    if (validador3) {
                        for (int j = 0; j < 5; j++) {
                            if (row - j >= 0 && col >= 0) { // Verifica se os índices são válidos
                                Button botaoAcima2 = buttons1[row - j][col];
                                clickedButton.setUserData("PortaAvioes");
                                botaoAcima2.setUserData("PortaAvioes");
                            } else {
                                validadorPorta = false;
                                break;
                            }
                        }
                    }

                    if (!validadorPorta || !validador3) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao posicionar o barco");
                        alert.setContentText("O Porta Avioes não pode ser posicionado aqui");
                        if (contadordePortaAvioes > 0) {
                            contadordePortaAvioes--;
                            ButtonPorta1.disableProperty().set(false);
                        }

                        alert.showAndWait();
                    }

                    nomeBarco = "";
                }

                if (nomeBarco.equals("Couracado") && clickedButton.getUserData().equals("Agua")) {
                    boolean validadorCouracado = true;
                    boolean validador2 = true;

                    for (int i = 0; i < 4; i++) {
                        if (row - i >= 0) { // Verifica se o índice é válido
                            Button botaoAcima = buttons1[row - i][col];
                            if (!botaoAcima.getUserData().equals("Agua")) {
                                validador2 = false;
                                break;
                            }
                        } else {
                            validador2 = false;
                            break;
                        }
                    }

                    if (validador2) {
                        for (int j = 0; j < 4; j++) {
                            if (row - j >= 0 && col >= 0) { // Verifica se os índices são válidos
                                Button botaoAcima2 = buttons1[row - j][col];
                                clickedButton.setUserData("Couracado");
                                botaoAcima2.setUserData("Couracado");
                            } else {
                                validadorCouracado = false;
                                break;
                            }
                        }
                    }

                    if (!validadorCouracado || !validador2) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao posicionar o barco");
                        alert.setContentText("O Couraçado não pode ser posicionado aqui");
                        if (contadordeCouracados > 0) {
                            contadordeCouracados--;
                            ButtonCoura1.disableProperty().set(false);
                        }

                        alert.showAndWait();
                    } else {
                        validadorPosicionamento = true;
                    }

                    nomeBarco = "";
                }
                threadVerificaBarcos.interrupted();

            } else if (nomeBarco != null) {
                if (nomeBarco.equals("Submarino") && clickedButton.getUserData().equals("Agua")) {
                    Boolean validador2 = true;
                    Boolean validadorSubmarino = true;

                    for (int i = 0; i < 2; i++) {
                        if (row - i >= 0) { // Verifica se o índice é válido
                            Button botaoAcima = buttons2[row - i][col];
                            if (!botaoAcima.getUserData().equals("Agua")) {
                                validador2 = false;
                                break;
                            }
                        } else {
                            validador2 = false;
                            break;
                        }
                    }

                    if (validador2) {
                        for (int j = 0; j < 2; j++) {
                            if (row - j >= 0 && col >= 0) { // Verifica se os índices são válidos
                                Button botaoAcima = buttons2[row - j][col];
                                clickedButton.setUserData("Submarino");
                                botaoAcima.setUserData("Submarino");

                            } else {
                                validadorSubmarino = false;
                                break;
                            }
                        }
                    }

                    if (!validadorSubmarino || !validador2) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao posicionar o barco");
                        alert.setContentText("O Submarino não pode ser posicionado aqui");
                        if (contadordeSubmarinos > 0) {
                            contadordeSubmarinos--;
                            ButtonSub1.disableProperty().set(false);
                        }

                        alert.showAndWait();
                    } else {
                        validadorPosicionamento = true;
                    }

                    nomeBarco = "";
                }
                if (nomeBarco.equals("PortaAvioes") && clickedButton.getUserData().equals("Agua")) {
                    boolean validadorPorta = true;
                    boolean validador3 = true;
                    for (int i = 0; i < 5; i++) {
                        if (row - i >= 0) { // Verifica se o índice é válido
                            Button botaoAcima = buttons2[row - i][col];
                            if (!botaoAcima.getUserData().equals("Agua")) {
                                validador3 = false;
                                break;
                            }
                        } else {
                            validador3 = false;
                            break;
                        }
                    }

                    if (validador3) {
                        for (int j = 0; j < 5; j++) {
                            if (row - j >= 0 && col >= 0) { // Verifica se os índices são válidos
                                Button botaoAcima = buttons2[row - j][col];
                                clickedButton.setUserData("PortaAvioes");
                                botaoAcima.setUserData("PortaAvioes");
                            } else {
                                validadorPorta = false;
                                break;
                            }
                        }
                    }

                    if (!validadorPorta || !validador3) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao posicionar o barco");
                        alert.setContentText("O Porta Avioes não pode ser posicionado aqui");
                        if (contadordePortaAvioes > 0) {
                            contadordePortaAvioes--;
                            ButtonPorta1.disableProperty().set(false);
                        }

                        alert.showAndWait();
                    }

                    nomeBarco = "";
                }

                if (nomeBarco.equals("Couracado") && clickedButton.getUserData().equals("Agua")) {
                    boolean validadorCouracado = true;
                    boolean validador2 = true;

                    for (int i = 0; i < 4; i++) {
                        if (row - i >= 0) { // Verifica se o índice é válido
                            Button botaoAcima = buttons2[row - i][col];
                            if (!botaoAcima.getUserData().equals("Agua")) {
                                validador2 = false;
                                break;
                            }
                        } else {
                            validador2 = false;
                            break;
                        }
                    }

                    if (validador2) {
                        for (int j = 0; j < 4; j++) {
                            if (row - j >= 0 && col >= 0) { // Verifica se os índices são válidos
                                Button botaoAcima = buttons2[row - j][col];
                                clickedButton.setUserData("Couracado");
                                botaoAcima.setUserData("Couracado");
                            } else {
                                validadorCouracado = false;
                                break;
                            }
                        }
                    }

                    if (!validadorCouracado || !validador2) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao posicionar o barco");
                        alert.setContentText("O Couraçado não pode ser posicionado aqui");
                        if (contadordeCouracados > 0) {
                            contadordeCouracados--;
                            ButtonCoura1.disableProperty().set(false);
                        }

                        alert.showAndWait();
                    } else {
                        validadorPosicionamento = true;
                    }

                    nomeBarco = "";
                }

            }
        }

    }

    private List<PessoaFilter> dadosBanco(String nome1, String nome2) {
        try {
            ConexaoFactoryPostgreSQL conexaoFactory = new ConexaoFactoryPostgreSQL();
            daoFactory = new DAOFactory(conexaoFactory);
            daoFactory.abrirConexao();
            PessoaDAO dao = daoFactory.getDAO(PessoaDAO.class);
            List<PessoaFilter> lista = new ArrayList<>();
            Jogadorfilter1 = new PessoaFilter();
            Jogadorfilter1 = dao.findByname(nome1);
            Jogadorfilter2 = new PessoaFilter();
            Jogadorfilter2 = dao.findByname(nome2);
            lista.add(Jogadorfilter1);
            lista.add(Jogadorfilter2);
            daoFactory.fecharConexao();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void atualizarTela() {
        Platform.runLater(() -> {
            LabelTempo.setText(duracao.getSeconds() + "s");
            LabelNomeJogador1.setText(getNomeJogador1());
            labelNomeJogador2.setText(getNomeJogador2());
            // TituloLabel.setText("Posicione o seu barco Jogador " + getNomeJogador1());

            // Atualize os campos
        });
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            configurarEncerramentoJanela();
            Duration duracao = Duration.between(tempoinicial, Instant.now());
            this.duracao = duracao;
            atualizarTela();
        }
    }

    public class threadVerificaBarcos extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (buttons1[i][j].getUserData().equals("Submarino")) {
                            buttons1[i][j].setStyle(
                                    "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                        }
                        if (buttons1[i][j].getUserData().equals("PortaAvioes")) {
                            buttons1[i][j].setStyle(
                                    "-fx-background-color: black; -fx-border-color: black; -fx-text-fill: black");

                        }
                        if (buttons1[i][j].getUserData().equals("Couracado")) {
                            buttons1[i][j].setStyle(
                                    "-fx-background-color: grey; -fx-border-color: black; -fx-text-fill: grey");
                        }
                        if (ButtonSub1.isDisable() && ButtonPorta1.isDisable() && ButtonCoura1.isDisable()
                                && ButtonSub2.isDisable() && ButtonPorta2.isDisable() && ButtonCoura2.isDisable()) {

                            ButtonPosicionar1.setVisible(true);
                            ButtonPosicionar1.setDisable(false);

                        }

                    }
                }
            }
        }
    }

    public class threadVerificaBarcos2 extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (buttons2[i][j].getUserData().equals("Submarino")) {
                            buttons2[i][j].setStyle(
                                    "-fx-background-color: green; -fx-border-color: black; -fx-text-fill: green");
                        }
                        if (buttons2[i][j].getUserData().equals("PortaAvioes")) {
                            buttons2[i][j].setStyle(
                                    "-fx-background-color: black; -fx-border-color: black; -fx-text-fill: black");

                        }
                        if (buttons2[i][j].getUserData().equals("Couracado")) {
                            buttons2[i][j].setStyle(
                                    "-fx-background-color: grey; -fx-border-color: black; -fx-text-fill: grey");
                        }
                        if (ButtonSub1.isDisable() && ButtonPorta1.isDisable() && ButtonCoura1.isDisable()
                                && ButtonSub2.isDisable() && ButtonPorta2.isDisable() && ButtonCoura2.isDisable()) {

                            ButtonPosicionar2.setVisible(true);
                            ButtonPosicionar2.setDisable(false);

                        }
                    }
                }
            }
        }

    }
}