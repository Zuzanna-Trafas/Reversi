package com.example.reversi_po;

public class Coordinates {
    private int X;
    private int Y;

    Coordinates(int x, int y) {
        X = x;
        Y = y;
    }

    @Override
    public String toString() { return X + " " + Y; }

    int getX() { return X; }

    int getY() { return Y; }

    void set(int x, int y) {
        X = x;
        Y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return ((Coordinates) o).X == X && ((Coordinates) o).Y == Y ;
    }
}
