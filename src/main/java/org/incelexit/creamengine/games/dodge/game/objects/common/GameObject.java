package org.incelexit.creamengine.games.dodge.game.objects.common;

import org.incelexit.creamengine.games.dodge.game.Dodgegame;

public abstract class GameObject {

    protected Position position;
    protected Dodgegame game;

    protected GameObject(Dodgegame game){
        this.game = game;
    }

    public void move(int x, int y) {
        this.move(new Movement(x, y));
    }
    public void move(Movement movement) {
        this.moveTo(this.position.copyAndMove(movement));
    }

    public void moveTo(Position position) {
        if (this.checkCollision(position)) {
            this.position = position;
        }
    }

    public abstract char[][] render();

    public Position getPosition() {
        return position;
    }

    public abstract boolean checkCollision(Position position);
}
