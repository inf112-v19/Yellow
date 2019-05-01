package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import map.GameMap;

import javax.smartcardio.Card;

public class Button implements IButton {

    private RoboRallyDemo game;
    CardHandler cardHandler;
    Robot robot;

    private int posX;
    private int posY;

    private String name;
    private Sprite buttonSprite;

    public Button(int posX, int posY, String name, Sprite buttonSprite, RoboRallyDemo game) {
        this.posX = posX;
        this.posY = posY;
        this.buttonSprite = buttonSprite;
        this.game = game;
        this.name = name;
        this.cardHandler = game.getCardHandler();
        if (!mainMenu.getMainRunning()) {
            this.robot = game.getRobot();
        }
        createButton(posX, posY);
    }

    public void createButton(int x, int y) {
        this.buttonSprite.setPosition(x, y);
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public Sprite getSprite() {
        return buttonSprite;
    }

    public boolean buttonClicked(int screenX, int screenY, Button button) {

        float NewScreenY = Gdx.graphics.getHeight() - screenY;
        if ((screenX > getPosX()) && (screenX < (getPosX() + buttonSprite.getWidth()) && (NewScreenY > getPosY())) && (NewScreenY < (getPosY() + buttonSprite.getHeight()))) {
            System.out.println("*click*");
            switch (name) {
                case "powerDown_inactive":
                    if (!robot.getInitiatePowerdown()) {
                        robot.setInitiatePowerdown(true);
                        System.out.println("Hey it worked");
                        return true;
                    }
                case "endRoundButton":
                    if (cardHandler.getSelectedCards().length == 5) {
                        System.out.println("endRoundButton");
                        cardHandler.setIsDone(true);
                        game.doTurn();
                        return true;
                    }
                case "clientButton":
                    return true;
                case "startButton":
                    game.create();
                    System.out.println("start");
                    mainMenu.setMainRunning(false);
                    cardHandler.nullyFy();
                    return true;
                case "serverButton":
                    return true;
            }
        }
        return false;
    }
}