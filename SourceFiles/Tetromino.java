package SourceFiles;

import java.util.Random;

public class Tetromino {
    enum Tetrominoes {
        NO_BLOCK, Forma_Z, Forma_S, Forma_I, Forma_T, Forma_Cuadrado, Forma_L, Forma_J
    }

    ;

    private Tetrominoes tetrominoes;                       // almacena el tipo actual de pieza
    private int coords[][];                                // forma actual de la pieza
    private int tetrominoTabla[][][];                      // tabla que almacena las coordenadas de todas las formas posibles

    // CONSTRUCTOR 
    public Tetromino() {
        coords = new int[4][2];
        tetrominoTabla = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},         //
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},      // Forma Z
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}},        // Forma S
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}},        // Forma I
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},        // Forma T
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},         // Forma CUADRADO
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},      // Forma L
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}}        // Forma J
        };

        ElegirForma(Tetrominoes.NO_BLOCK);
    }

        // METODOS para establecer formas
    public void ElegirForma(Tetrominoes tetromino) {
        for (int i = 0; i < coords.length; i++) {                
            for (int j = 0; j < coords[i].length; j++) {
                coords[i][j] = tetrominoTabla[tetromino.ordinal()][i][j];    // establece las coordenadas de la pieza actual basado en el tipo proporcionado
            }
        }

        tetrominoes = tetromino;
    }

    public void FormaRandom() {                        // establece una forma aleatoria para la pieza
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        ElegirForma(Tetrominoes.values()[x]);
    }

    public Tetrominoes getForma() {                        // devuelve el tipo de pieza actual
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
        if (tetrominoes == Tetrominoes.Forma_Cuadrado) {
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
        if (tetrominoes == Tetrominoes.Forma_Cuadrado) {
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
