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

    private static final int BoardWidth = 10;
    private static final int BoardHeight = 22;

    private Timeline timeline;
    private boolean isFallingDone = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int currentScore = 0;

    private int curX = 0;
    private int curY = 0;

    private Tetromino curBlock;

    private Tetromino.Tetrominoes[] gameBoard;
    private Color[] colorTable;

    private String currentStatus;
    private int currentTimerResolution;

    private GameWindow tetrisFrame;

    public GameBoardPanel(GameWindow tetrisFrame, int timerResolution) {
        super(400, 800);  // Adjust canvas size as needed
        curBlock = new Tetromino();
        timeline = new Timeline(new KeyFrame(Duration.millis(timerResolution), e -> actionPerformed()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        currentTimerResolution = timerResolution;

        gameBoard = new Tetromino.Tetrominoes[BoardWidth * BoardHeight];

        colorTable = new Color[]{
                Color.BLACK,      // Color for NO_BLOCK
                Color.CYAN,       // Color for I
                Color.BLUE,       // Color for J
                Color.ORANGE,     // Color for L
                Color.YELLOW,     // Color for O
                Color.GREEN,      // Color for S
                Color.PURPLE,     // Color for T
                Color.RED         // Color for Z
        };

        setOnKeyPressed(this::handleKeyPress);
        setFocusTraversable(true); // Permitir que el canvas tenga el foco

        this.tetrisFrame = tetrisFrame;
        initBoard();
        requestFocus(); // Solicitar el foco para capturar eventos de teclado
    }

    private void handleKeyPress(KeyEvent event) {
        if (!isStarted || curBlock.getShape() == Tetromino.Tetrominoes.NO_BLOCK) {
            return;
        }

        KeyCode keycode = event.getCode();

        if (keycode == KeyCode.P) {
            pause();
            return;
        }

        if (isPaused) {
            return;
        }

        switch (keycode) {
            case LEFT:
                isMovable(curBlock, curX - 1, curY);
                break;
            case RIGHT:
                isMovable(curBlock, curX + 1, curY);
                break;
            case UP:
                isMovable(curBlock.rotateRight(), curX, curY);
                break;
            case DOWN:
                advanceOneLine();
                break;
            case SPACE:
                advanceToEnd();
                break;
            default:
                break;
        }
    }

    private void setResolution() {
        switch (currentScore / 10) {
            case 10:
                currentTimerResolution = 100;
                break;
            case 9:
                currentTimerResolution = 130;
                break;
            case 8:
                currentTimerResolution = 160;
                break;
            case 7:
                currentTimerResolution = 190;
                break;
            case 6:
                currentTimerResolution = 220;
                break;
            case 5:
                currentTimerResolution = 250;
                break;
            case 4:
                currentTimerResolution = 280;
                break;
            case 3:
                currentTimerResolution = 310;
                break;
            case 2:
                currentTimerResolution = 340;
                break;
            case 1:
                currentTimerResolution = 370;
                break;
            case 0:
                currentTimerResolution = 370;
                break;
        }

        timeline.getKeyFrames().set(0, new KeyFrame(Duration.millis(currentTimerResolution), e -> actionPerformed()));
    }

    private void initBoard() {
        for (int i = 0; i < BoardWidth * BoardHeight; i++) {
            gameBoard[i] = Tetromino.Tetrominoes.NO_BLOCK;
        }
    }

    private void actionPerformed() {
        if (isFallingDone) {
            isFallingDone = !isFallingDone;
            newTetromino();
        } else {
            advanceOneLine();
        }
    }

    public void start() {
        if (isPaused) {
            return;
        }

        isStarted = true;
        isFallingDone = false;
        currentScore = 0;
        initBoard();

        newTetromino();
        timeline.play();
    }

    public void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            timeline.pause();
        } else {
            timeline.play();
        }

        draw();
    }

    private int blockWidth() {
        return (int) getWidth() / BoardWidth;
    }

    private int blockHeight() {
        return (int) getHeight() / BoardHeight;
    }

    private Tetromino.Tetrominoes curTetrominoPos(int x, int y) {
        return gameBoard[(y * BoardWidth) + x];
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (!isPaused) {
            currentStatus = "Score: " + currentScore;
        } else {
            currentStatus = "PAUSED";
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Consolas", 28));
        gc.fillText(currentStatus, 15, 35);

        int boardTop = (int) getHeight() - BoardHeight * blockHeight();

        int tempY = curY;
        while (tempY > 0) {
            if (!atomIsMovable(curBlock, curX, tempY - 1, false))
                break;
            tempY--;
        }
        for (int i = 0; i < 4; i++) {
            int x = curX + curBlock.getX(i);
            int y = tempY - curBlock.getY(i);
            drawTetromino(gc, 0 + x * blockWidth(), boardTop + (BoardHeight - y - 1) * blockHeight(), curBlock.getShape(), true);
        }

        for (int i = 0; i < BoardHeight; i++) {
            for (int j = 0; j < BoardWidth; j++) {
                Tetromino.Tetrominoes shape = curTetrominoPos(j, BoardHeight - i - 1);
                if (shape != Tetromino.Tetrominoes.NO_BLOCK)
                    drawTetromino(gc, 0 + j * blockWidth(), boardTop + i * blockHeight(), shape, false);
            }
        }

        if (curBlock.getShape() != Tetromino.Tetrominoes.NO_BLOCK) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curBlock.getX(i);
                int y = curY - curBlock.getY(i);
                drawTetromino(gc, 0 + x * blockWidth(), boardTop + (BoardHeight - y - 1) * blockHeight(), curBlock.getShape(), false);
            }
        }
    }

    private void drawTetromino(GraphicsContext gc, int x, int y, Tetromino.Tetrominoes bs, boolean isShadow) {
        Color curColor = colorTable[bs.ordinal()];

        if (!isShadow) {
            gc.setFill(curColor);
            gc.fillRect(x + 1, y + 1, blockWidth() - 2, blockHeight() - 2);
        } else {
            gc.setFill(curColor.darker().darker());
            gc.fillRect(x + 1, y + 1, blockWidth() - 2, blockHeight() - 2);
        }
    }

    private void removeFullLines() {
        int fullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; i--) {
            boolean isFull = true;

            for (int j = 0; j < BoardWidth; j++) {
                if (curTetrominoPos(j, i) == Tetromino.Tetrominoes.NO_BLOCK) {
                    isFull = false;
                    break;
                }
            }

            if (isFull) {
                ++fullLines;
                for (int k = i; k < BoardHeight - 1; k++) {
                    for (int l = 0; l < BoardWidth; ++l)
                        gameBoard[(k * BoardWidth) + l] = curTetrominoPos(l, k + 1);
                }
            }
        }

        if (fullLines > 0) {
            currentScore += fullLines;
            isFallingDone = true;
            curBlock.setShape(Tetromino.Tetrominoes.NO_BLOCK);
            setResolution();
            draw();
        }
    }

    private boolean atomIsMovable(Tetromino chkBlock, int chkX, int chkY, boolean flag) {
        for (int i = 0; i < 4; i++) {
            int x = chkX + chkBlock.getX(i);
            int y = chkY - chkBlock.getY(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (curTetrominoPos(x, y) != Tetromino.Tetrominoes.NO_BLOCK) {
                return false;
            }
        }

        if (flag) {
            curBlock = chkBlock;
            curX = chkX;
            curY = chkY;
            draw();
        }

        return true;
    }

    private boolean isMovable(Tetromino chkBlock, int chkX, int chkY) {
        return atomIsMovable(chkBlock, chkX, chkY, true);
    }

    private void newTetromino() {
        curBlock.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curBlock.minY();

        if (!isMovable(curBlock, curX, curY)) {
            curBlock.setShape(Tetromino.Tetrominoes.NO_BLOCK);
            timeline.stop();
            isStarted = false;
            GameOver(currentScore);
        }
    }

    private void tetrominoFixed() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curBlock.getX(i);
            int y = curY - curBlock.getY(i);
            gameBoard[(y * BoardWidth) + x] = curBlock.getShape();
        }

        removeFullLines();

        if (!isFallingDone) {
            newTetromino();
        }
    }

    private void advanceOneLine() {
        if (!isMovable(curBlock, curX, curY - 1)) {
            tetrominoFixed();
        }
    }

    private void advanceToEnd() {
        int tempY = curY;
        while (tempY > 0) {
            if (!isMovable(curBlock, curX, tempY - 1))
                break;
            --tempY;
        }
        tetrominoFixed();
    }

    private void GameOver(int dbScore) {
        // Usar un diálogo de JavaFX para el Game Over
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over!");
        alert.setHeaderText(null);
        alert.showAndWait();
        setResolution();
        start();
    }
}
