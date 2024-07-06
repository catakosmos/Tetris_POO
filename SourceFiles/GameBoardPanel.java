package SourceFiles;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GameBoardPanel extends Canvas {

    private static final int AnchoJuego = 10;
    private static final int AlturaJuego = 22;

    private Timeline timeline;
    private boolean PiezaColocada = false;
    private boolean Start = false;
    private boolean Pausado = false;
    private int PuntacionActual = 0;

    private int CoordenadaXactual = 0;
    private int CoordenadaYActual = 0;

    private Tetromino PiezaActual;

    private Tetromino.Tetrominoes[] Tablero;
    private Color[] ColorBloque;

    private String EstadoJuego;
    private int VelocidadTimerActual;

    private GameWindow VentanaJuego;

    public GameBoardPanel(GameWindow VentanaJuego, int VelocidadTimer) {
        super(400, 800);  // establece el tamaño del canvas
        PiezaActual = new Tetromino();
        timeline = new Timeline(new KeyFrame(Duration.millis(VelocidadTimer), e -> actionPerformed()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        VelocidadTimerActual = VelocidadTimer;

        Tablero = new Tetromino.Tetrominoes[AnchoJuego * AlturaJuego];

        ColorBloque = new Color[]{
                Color.BLACK,      // Color for NO_BLOCK
                Color.CYAN,       // Color for I
                Color.BLUE,       // Color for J
                Color.ORANGE,     // Color for L
                Color.YELLOW,     // Color for O
                Color.GREEN,      // Color for S
                Color.PURPLE,     // Color for T
                Color.RED         // Color for Z
        };

        setOnKeyPressed(this::AccionTeclado);
        setFocusTraversable(true); // Permitir que el canvas tenga el foco

        this.VentanaJuego = VentanaJuego;
        IniciarTablero();
        requestFocus(); // Solicitar el foco para capturar eventos de teclado
    }

    private void AccionTeclado(KeyEvent event) {
        if (!Start || PiezaActual.getForma() == Tetromino.Tetrominoes.NO_BLOCK) {
            return;
        }

        KeyCode keycode = event.getCode();

        if (keycode == KeyCode.P) {
            Pausa();
            return;
        }

        if (Pausado) {
            return;
        }

        switch (keycode) {
            case LEFT:
                Movible(PiezaActual, CoordenadaXactual - 1, CoordenadaYActual);
                break;
            case RIGHT:
                Movible(PiezaActual, CoordenadaXactual + 1, CoordenadaYActual);
                break;
            case UP:
                Movible(PiezaActual.rotateRight(), CoordenadaXactual, CoordenadaYActual);
                break;
            case DOWN:
                BajarUnaLinea();
                break;
            case SPACE:
                ColocarALFinal();
                break;
            default:
                break;
        }
    }

    private void CambiarVelociad() {
        switch (PuntacionActual / 10) {
            case 10:
                VelocidadTimerActual = 100;
                break;
            case 9:
                VelocidadTimerActual = 130;
                break;
            case 8:
                VelocidadTimerActual = 160;
                break;
            case 7:
                VelocidadTimerActual = 190;
                break;
            case 6:
                VelocidadTimerActual = 220;
                break;
            case 5:
                VelocidadTimerActual = 250;
                break;
            case 4:
                VelocidadTimerActual = 280;
                break;
            case 3:
                VelocidadTimerActual = 310;
                break;
            case 2:
                VelocidadTimerActual = 340;
                break;
            case 1:
                VelocidadTimerActual = 370;
                break;
            case 0:
                VelocidadTimerActual = 400;
                break;
        }

        timeline.getKeyFrames().set(0, new KeyFrame(Duration.millis(VelocidadTimerActual), e -> actionPerformed()));
    }

    private void IniciarTablero() {
        for (int i = 0; i < AnchoJuego * AlturaJuego; i++) {
            Tablero[i] = Tetromino.Tetrominoes.NO_BLOCK;
        }
    }

    private void actionPerformed() {
        if (PiezaColocada) {
            PiezaColocada = !PiezaColocada;
            NuevaPieza();
        } else {
            BajarUnaLinea();
        }
    }

    public void start() {
        if (Pausado) {
            return;
        }

        Start = true;
        PiezaColocada = false;
        PuntacionActual = 0;
        IniciarTablero();

        NuevaPieza();
        timeline.play();
    }

    public void Pausa() {
        if (!Start) {
            return;
        }

        Pausado = !Pausado;
        if (Pausado) {
            timeline.pause();
        } else {
            timeline.play();
        }

        DibujarJuego();
    }

    private int AnchoBloque() {
        return (int) getWidth() / AnchoJuego;
    }

    private int AlturaBloque() {
        return (int) getHeight() / AlturaJuego;
    }

    private Tetromino.Tetrominoes PosicionPieza(int x, int y) {
        return Tablero[(y * AnchoJuego) + x];
    }

    private void DibujarJuego() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (!Pausado) {
            EstadoJuego = "Score: " + PuntacionActual;
        } else {
            EstadoJuego = "PAUSA";
        }

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Consolas", 28));
        gc.fillText(EstadoJuego, 15, 35);

        int InicioTablero = (int) getHeight() - AlturaJuego * AlturaBloque();

        int tempY = CoordenadaYActual;
        while (tempY > 0) {
            if (!BloqueMovible(PiezaActual, CoordenadaXactual, tempY - 1, false))
                break;
            tempY--;
        }
        for (int i = 0; i < 4; i++) {
            int x = CoordenadaXactual + PiezaActual.getX(i);
            int y = tempY - PiezaActual.getY(i);
            PrintTetromino(gc, 0 + x * AnchoBloque(), InicioTablero + (AlturaJuego - y - 1) * AlturaBloque(), PiezaActual.getForma(), true);
        }

        for (int i = 0; i < AlturaJuego; i++) {
            for (int j = 0; j < AnchoJuego; j++) {
                Tetromino.Tetrominoes Forma = PosicionPieza(j, AlturaJuego - i - 1);
                if (Forma != Tetromino.Tetrominoes.NO_BLOCK)
                    PrintTetromino(gc, 0 + j * AnchoBloque(), InicioTablero + i * AlturaBloque(), Forma, false);
            }
        }

        if (PiezaActual.getForma() != Tetromino.Tetrominoes.NO_BLOCK) {
            for (int i = 0; i < 4; i++) {
                int x = CoordenadaXactual + PiezaActual.getX(i);
                int y = CoordenadaYActual - PiezaActual.getY(i);
                PrintTetromino(gc, 0 + x * AnchoBloque(), InicioTablero + (AlturaJuego - y - 1) * AlturaBloque(), PiezaActual.getForma(), false);
            }
        }
    }

    private void PrintTetromino(GraphicsContext gc, int x, int y, Tetromino.Tetrominoes bs, boolean Sombra) {
        Color curColor = ColorBloque[bs.ordinal()];

        if (!Sombra) {
            gc.setFill(curColor);
            gc.fillRect(x + 1, y + 1, AnchoBloque() - 2, AlturaBloque() - 2);
        } else {
            gc.setFill(curColor.darker().darker());
            gc.fillRect(x + 1, y + 1, AnchoBloque() - 2, AlturaBloque() - 2);
        }
    }

    private void RemoverLineas() {
        int lineasLLenas = 0;

        for (int i = AlturaJuego - 1; i >= 0; i--) {
            boolean isFull = true;

            for (int j = 0; j < AnchoJuego; j++) {
                if (PosicionPieza(j, i) == Tetromino.Tetrominoes.NO_BLOCK) {
                    isFull = false;
                    break;
                }
            }

            if (isFull) {
                ++lineasLLenas;
                for (int k = i; k < AlturaJuego - 1; k++) {
                    for (int l = 0; l < AnchoJuego; ++l)
                        Tablero[(k * AnchoJuego) + l] = PosicionPieza(l, k + 1);
                }
            }
        }

        if (lineasLLenas > 0) {
            PuntacionActual += lineasLLenas;
            PiezaColocada = true;
            PiezaActual.ElegirForma(Tetromino.Tetrominoes.NO_BLOCK);
            CambiarVelociad();
            DibujarJuego();
        }
    }

    private boolean BloqueMovible(Tetromino BloqueActual, int Xactual, int Yactual, boolean Boleano) {
        for (int i = 0; i < 4; i++) {
            int x = Xactual + BloqueActual.getX(i);
            int y = Yactual - BloqueActual.getY(i);
            if (x < 0 || x >= AnchoJuego || y < 0 || y >= AlturaJuego)
                return false;
            if (PosicionPieza(x, y) != Tetromino.Tetrominoes.NO_BLOCK) {
                return false;
            }
        }

        if (Boleano) {
            PiezaActual = BloqueActual;
            CoordenadaXactual = Xactual;
            CoordenadaYActual = Yactual;
            DibujarJuego();
        }

        return true;
    }

    private boolean Movible(Tetromino chkBlock, int chkX, int chkY) {
        return BloqueMovible(chkBlock, chkX, chkY, true);
    }

    private void NuevaPieza() {
        PiezaActual.FormaRandom();
        CoordenadaXactual = AnchoJuego / 2 + 1;
        CoordenadaYActual = AlturaJuego - 1 + PiezaActual.minY();

        if (!Movible(PiezaActual, CoordenadaXactual, CoordenadaYActual)) {
            PiezaActual.ElegirForma(Tetromino.Tetrominoes.NO_BLOCK);
            timeline.stop();
            Start = false;
            GameOver(PuntacionActual);
        }
    }

    private void PiezaFijada() {
        for (int i = 0; i < 4; i++) {
            int x = CoordenadaXactual + PiezaActual.getX(i);
            int y = CoordenadaYActual - PiezaActual.getY(i);
            Tablero[(y * AnchoJuego) + x] = PiezaActual.getForma();
        }

        RemoverLineas();

        if (!PiezaColocada) {
            NuevaPieza();
        }
    }

    private void BajarUnaLinea() {
        if (!Movible(PiezaActual, CoordenadaXactual, CoordenadaYActual - 1)) {
            PiezaFijada();
        }
    }

    private void ColocarALFinal() {
        int tempY = CoordenadaYActual;
        while (tempY > 0) {
            if (!Movible(PiezaActual, CoordenadaXactual, tempY - 1))
                break;
            --tempY;
        }
        PiezaFijada();
    }



    private void GameOver(int Puntuacion) {
        // Usar un diálogo de JavaFX para el Game Over
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over!");
        alert.setHeaderText(null);
        alert.setContentText("Puntuación obtenida: " + Puntuacion);
        alert.showAndWait();
        CambiarVelociad();
        start();
    }
}
