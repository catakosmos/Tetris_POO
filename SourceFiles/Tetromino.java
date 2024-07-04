package SourceFiles;

import java.util.Random;

public class Tetromino {
    enum Tetrominoes {
        NO_BLOCK, Z_SHAPE, S_SHAPE, I_SHAPE, T_SHAPE, O_SHAPE, L_SHAPE, J_SHAPE
    }

    ;

    private Tetrominoes tetrominoes;                       // almacena el tipo actual de pieza
    private int coords[][];                                // forma actual de la pieza
    private int tetrominoTable[][][];                      // tabla que almacena las coordenadas de todas las formas posibles

    // CONSTRUCTOR 
    public Tetromino() {
        coords = new int[4][2];
        tetrominoTable = new int[][][]{        
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},         // NO_BLOCK
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},      // Z_SHAPE
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}},        // S_SHAPE
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}},        // I_SHAPE
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},        // T_SHAPE
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},         // O_SHAPE
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},      // L_SHAPE
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}}        // J_SHAPE
        };

        setShape(Tetrominoes.NO_BLOCK);
    }

        // METODOS para establecer formas
    public void setShape(Tetrominoes tetromino) {                            
        for (int i = 0; i < coords.length; i++) {                
            for (int j = 0; j < coords[i].length; j++) {
                coords[i][j] = tetrominoTable[tetromino.ordinal()][i][j];    // establece las coordenadas de la pieza actual basado en el tipo proporcionado
            }
        }

        tetrominoes = tetromino;
    }

    public void setRandomShape() {                        // establece una forma aleatoria para la pieza
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        setShape(Tetrominoes.values()[x]);
    }

    public Tetrominoes getShape() {                        // devuelve el tipo de pieza actual
        return tetrominoes;
    }

    // METODOS para coordenadas
    public void setX(int idx, int x) {
        coords[idx][0] = x;
    }

    public void setY(int idx, int y) {
        coords[idx][1] = y;
    }

    public int getX(int idx) {
        return coords[idx][0];
    }

    public int getY(int idx) {
        return coords[idx][1];
    }

    // METODOS para mínimas coordenadas
    public int minX() {
        int ret = 0;
        for (int i = 0; i < coords.length; i++) {
            ret = Math.min(ret, coords[i][0]);
        }
        return ret;
    }

    public int minY() {
        int ret = 0;
        for (int i = 0; i < coords.length; i++) {
            ret = Math.min(ret, coords[i][1]);
        }
        return ret;
    }

    // ROTACIONES
    public Tetromino rotateLeft() {
        if (tetrominoes == Tetrominoes.O_SHAPE) {
            return this;
        }

        Tetromino ret = new Tetromino();
        ret.tetrominoes = tetrominoes;

        for (int i = 0; i < coords.length; i++) {
            ret.setX(i, getY(i));
            ret.setY(i, -getX(i));
        }

        return ret;
    }

    public Tetromino rotateRight() {
        if (tetrominoes == Tetrominoes.O_SHAPE) {
            return this;
        }

        Tetromino ret = new Tetromino();
        ret.tetrominoes = tetrominoes;

        for (int i = 0; i < coords.length; i++) {
            ret.setX(i, -getY(i));
            ret.setY(i, getX(i));
        }

        return ret;
    }
}
