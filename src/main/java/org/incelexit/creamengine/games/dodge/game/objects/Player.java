package org.incelexit.creamengine.games.dodge.game.objects;

import org.incelexit.creamengine.games.dodge.game.Dodgegame;
import org.incelexit.creamengine.games.dodge.game.objects.common.GameObject;
import org.incelexit.creamengine.games.dodge.game.objects.common.Position;

public class Player extends GameObject {

    public Player(Dodgegame game, Position initialPosition) {
        super(game);
        this.position = initialPosition;
    }

    @Override
    public char[][] render() {
        char[][] renderedObject = new char[1][1];
        renderedObject[0][0] = '@';
        return renderedObject;
    }

    @Override
    public boolean checkCollision(Position position) {
        return game.checkCollision(position);
    }
}
