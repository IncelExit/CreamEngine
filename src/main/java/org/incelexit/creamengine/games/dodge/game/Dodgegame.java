package org.incelexit.creamengine.games.dodge.game;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.incelexit.creamengine.bot.CEBot;
import org.incelexit.creamengine.games.dodge.game.callbacks.RenderMessageSetterCallback;
import org.incelexit.creamengine.games.dodge.game.listeners.DodgeGameChannelListener;
import org.incelexit.creamengine.games.dodge.game.objects.Player;
import org.incelexit.creamengine.games.dodge.game.objects.common.Extent;
import org.incelexit.creamengine.games.dodge.game.objects.common.GameObject;
import org.incelexit.creamengine.games.dodge.game.objects.common.Position;
import org.incelexit.creamengine.util.ChannelMessenger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Dodgegame implements Runnable {

    ChannelMessenger channelMessenger;
    Thread gameLoopThread = new Thread(this);
    long previousTime = System.nanoTime();
    double UPS = 10;
    double FPS = 10;
    final double timeU = 1000000000 / UPS;
    final double timeF = 1000000000 / FPS;
    double deltaU = 0, deltaF = 0;
    int frames = 0, ticks = 0;
    Random r = new Random();

    String renderMessageId;

    int sceneX = 20;
    int sceneY = 10;

    char[][] scene;

    List<GameObject> gameObjects = new ArrayList<>();
    private Player player;
    private DodgeGameChannelListener channelListener;
    private String previousFrame = "";

    public Dodgegame(MessageChannel channel) {
        this.channelMessenger = new ChannelMessenger(channel);
        this.scene = new char[sceneX][sceneY];
        setupChannelListener();
    }

    private void setupChannelListener() {
        this.channelListener = new DodgeGameChannelListener(this, channelMessenger.getChannel());
        CEBot.getBot().registerListener(this.channelListener);
    }

    public void setRenderMessageId(String renderMessageId) {
        this.renderMessageId = renderMessageId;
    }

    public void start() {
        channelMessenger.sendMessage("Initializing ...", new RenderMessageSetterCallback(this));
        this.player = new Player(this, new Position(0, 0));
        this.gameObjects.add(player);
        gameLoopThread.start();
    }

    public void finish() {
        gameLoopThread = null;
        CEBot.getBot().removeListener(this.channelListener);
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        while (gameLoopThread == thisThread) {
            long currentTime = System.nanoTime();
            double deltaT = currentTime - previousTime;
            previousTime = currentTime;
            deltaU += deltaT / timeU;
            deltaF += deltaT / timeF;

            if (deltaU >= 1) {
                update();
                ticks++;
                deltaU--;
            }

            if (deltaF >= 1) {
                render();
                frames++;
                deltaF--;
            }
        }
    }

    public void processInput(Message message) {
        String content = message.getContentDisplay();
        int length = 1;
        if (content.length() == 2) {
            length = Integer.parseInt(content.substring(0, 1));
            content = content.substring(1, 2);
        }
        switch (content) {
            case "w" -> player.move(0, -length);
            case "a" -> player.move(-length, 0);
            case "s" -> player.move(0, length);
            case "d" -> player.move(length, 0);
        }
        channelMessenger.deleteMessage(message);
    }


    private void update() {
        /*
        int random = r.nextInt(4);
        switch (random) {
            case 0 -> this.player.move(1, 0);
            case 1 -> this.player.move(0, 1);
            case 2 -> this.player.move(-1, 0);
            case 3 -> this.player.move(0, -1);
        }
         */
        System.out.println(player.getPosition().getX() + " " + player.getPosition().getY());
    }

    private void render() {
        char[][] frame = getBackground();

        for (GameObject object : gameObjects) {
            renderObjectOnFrame(object, frame);
        }

        String renderedFrame = renderFrame(frame);

        if (!previousFrame.equals(renderedFrame) && renderMessageId != null) {
            channelMessenger.editMessage(renderMessageId, renderedFrame);
            previousFrame = renderedFrame;
        }

    }

    private char[][] getBackground() {
        char[][] frame = new char[sceneX][sceneY];
        for (char[] column : frame) {
            Arrays.fill(column, '#');
        }
        return frame;
    }

    private void renderObjectOnFrame(GameObject object, char[][] frame) {
        Extent frameExtent = getExtent(frame);
        if (frameExtent.getX() > 0 && frameExtent.getY() > 0) {
            Position objectPosition = object.getPosition();
            char[][] renderedObject = object.render();

            Extent objectExtent = getExtent(renderedObject);
            if (objectExtent.getX() % 2 != 1 || objectExtent.getY() % 2 != 1) {
                throw new IllegalStateException("Objects must always have odd amounts of rows and columns to render them.");
            }

            int xOffset = objectExtent.getX() / 2 - objectPosition.getX();
            int yOffset = objectExtent.getY() / 2 - objectPosition.getY();


            Position luCorner = new Position(
                    Math.max(0, xOffset),
                    Math.max(0, yOffset)
            );

            Position rlCorner = new Position(
                    Math.min(objectExtent.getX(), frameExtent.getX() + xOffset),
                    Math.min(objectExtent.getY(), frameExtent.getY() + yOffset)

            );


            for (int x = luCorner.getX(); x < rlCorner.getX(); x++) {
                System.arraycopy(renderedObject[x], luCorner.getY(),
                        frame[x - xOffset], -yOffset,
                        rlCorner.getY() - luCorner.getY());
            }
        }
    }

    private Extent getExtent(char[][] frame) {
        int frameX = frame.length;
        if (frameX > 0) {
            int frameY = frame[0].length;
            return new Extent(frameX, frameY);
        }
        return new Extent(0, 0);
    }

    private String renderFrame(char[][] frame) {
        if (frame.length == 0)
            return "";

        StringBuilder renderedFrame = new StringBuilder();
        renderedFrame.append("```");
        for (int row = 0; row < frame[0].length; row++) {
            for (char[] chars : frame) {
                renderedFrame.append(chars[row]);
            }
            renderedFrame.append("\n");
        }
        renderedFrame.append("```");
        return renderedFrame.toString();
    }

    public boolean checkCollision(Position position) {
        return !(position.getX() < 0
                 || position.getX() > sceneX - 1
                 || position.getY() < 0
                 || position.getY() > sceneY - 1);
    }
}
