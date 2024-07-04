package SourceFiles;

import java.awt.GridLayout;
import javax.swing.JFrame;

public class GameWindow extends JFrame {

    // Constructor
    public GameWindow() {
        setTitle("Tetris :D");
        setSize(400, 814);
        setResizable(false);

        setLayout(new GridLayout(1, 2));                    // establecer layout de la ventana con una fila y 2 columnas 

        GameBoardPanel gameBoard = new GameBoardPanel(this, 400);  // panel de tablero con tiempo de resolucion 400
        add(gameBoard);
        gameBoard.start();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        // operacion por defecto para cerrar la ventana
        setLocationRelativeTo(null);                           // centra la ventana en la pantalla
        setVisible(true);                                      // hace visible la ventana
    }
}
