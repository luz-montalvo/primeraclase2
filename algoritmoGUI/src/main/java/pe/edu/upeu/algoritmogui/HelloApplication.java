package pe.edu.upeu.algoritmogui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        var contenido = "Bienvenido a JavaFX";
        Label mensaje = new Label(contenido);
        mensaje.setStyle("-fx-font-size: 18px;");
        Button boton = new Button("Saludar");

        Button boton1 = new Button("Volver");
        TextField tf = new TextField();

        boton.setOnAction((ActionEvent evento) -> {
            String valor = tf.getText();
            mensaje.setText("Bienvenido " + valor + " !!");
            mensaje.setStyle("-fx-fx-text-fill: black;");
        });

        boton1.setOnAction((ActionEvent e) -> {
            mensaje.setText(contenido);
            mensaje.setStyle("-fx-fx-text-fill: red;");
        });

        VBox raiz = new VBox(15, mensaje, tf, boton, boton1);
        raiz.setAlignment(Pos.CENTER);
        raiz.setStyle("-fx-padding: 30;");

        Scene escena = new Scene(raiz, 400, 250);
        stage.setTitle("Hola JavaFX");
        stage.setScene(escena);
        stage.show();
    }
}
