package org.incelexit.creamengine.games.dodge.game.objects.common;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position copy() {
        return new Position(x,y);
    }

    public Position copyAndMove(Movement movement) {
        Position newPosition = new Position(x,y);
        newPosition.move(movement);
        return newPosition;
    }

    public void move(Movement movement) {
        this.x += movement.getX();
        this.y += movement.getY();
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
