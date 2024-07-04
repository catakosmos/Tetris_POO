package SourceFiles;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris :D");
        primaryStage.setResizable(false);

        GameBoardPanel gameBoard = new GameBoardPanel(this, 400);  // Pasar this (GameWindow) como referencia

        // Crear un contenedor StackPane y agregar GameBoardPanel como hijo
        StackPane root = new StackPane();
        root.getChildren().add(gameBoard);

        primaryStage.setScene(new Scene(root));  // Usar el contenedor como raíz de la escena
        gameBoard.start();

        primaryStage.setOnCloseRequest(event -> System.exit(0));  // Cerrar la aplicación al cerrar la ventana

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
