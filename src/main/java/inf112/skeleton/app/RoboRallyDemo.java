package inf112.skeleton.app;

import Grid.IGrid;
import Grid.MyGrid;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Pools;
import map.GameMap;
import map.IGameMap;
import map.MapTile;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;

public class RoboRallyDemo implements ApplicationListener, InputProcessor {
    private static TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private Cards CardButton;
    private Robot robot;
    private FitViewport viewPort;

    private static CardHandler cardHandler;

    private Cards statBoard0;

    private int tick = 0;
    private int turn = 0;


    private SpriteBatch batch;
    private Texture texture;
    private Sprite sprite;
    private float posX, posY;
    private TiledMapTileSet mapSet;
    private BitmapFont font;

    private Sprite statBoardSprite;

    private static GameMap map;
    private IGrid grid;

    //create the initial state of the game
    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        batch = new SpriteBatch();

        //set the camera
        setCamera(w, h);
        //creation of the map
        tiledMap = new TmxMapLoader().load("Models/roborallymap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        createGrid();
        texture = new Texture(Gdx.files.internal("Models/tank.png"));
        sprite = new Sprite(texture);

        map = new GameMap(grid);
        posX = 0;
        posY = 0;
        int[] startpos = {Math.round(posX), Math.round(posY)};
        robot = new Robot(sprite, startpos);

        grid.set(robot.getPosX(), robot.getPosY(), MapTile.PLAYER);
        sprite.setPosition(robot.getX1(), robot.getY1());

        //create the card that Is clicked
        Texture cardTexture = new Texture(Gdx.files.internal("Models/AlleBevegelseKortUtenPrioritet/genericCard.png"));
        cardHandler = new CardHandler(batch, robot, map);

        font = new BitmapFont();

        //create the end turn button
        buttonCreation(700, 500);
        statBoardCreation(700,930);

        //set the position of all the cardsprites
        cardHandler.setCardSprites();

        //create the 9 cards cards
        cardHandler.createInitialDecklist();

        //creation of the 5 cardSlots
        cardHandler.createCardSlots();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
    }

    //rendering of the map and all the sprites
    @Override
    public void render() {
        //Gray background color
        Gdx.gl.glClearColor(128 / 255f, 128 / 255f, 128 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        Cards selectedCards[] = cardHandler.getSelectedCards();
        batch.begin();
        sprite.draw(batch);

        //rotation of sprite, rotate 90 degrees every 100th gametick
        if (tick % 100 == 0) {
            for (int i = 0; i < selectedCards.length; i++) {
               // System.out.println(selectedCards[i]);
            }
           // System.out.println("\n");
        }

        doTurn();
        //draw the cardslots
        cardHandler.drawCardSlots();

        cardHandler.getLockedList();

        //draw button
        CardButton.getCardSprite().draw(batch);

        statBoard0.getCardSprite().draw(batch);
        drawStats();

        //draw Cards
        cardHandler.drawCards();

        tick++;
        batch.end();
    }

    @Override
    //an atempt on an resize method
    public void resize(int width, int height) {
        viewPort.update(width, height);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    //method for general movment. Keyboard at the moment, but will be cards in the future
    @Override
    public boolean keyDown(int keycode) {
        float moveAmount = 75.0f;
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
            moveAmount = 150.0f;

        if (keycode == Keys.LEFT)
            posX -= moveAmount;
        if (keycode == Keys.RIGHT)
            posX += moveAmount;
        if (keycode == Keys.UP)
            posY += moveAmount;
        if (keycode == Keys.DOWN)
            posY -= moveAmount;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    //this method is used to click and move a card around on the screen. if you click a card, the clickedCard, will get the right card from the Deck
    //if tou click a card and it is inside a cardSlot a boolean will change (isClicked=true), this I will use in the touchUp method
    // And if you click the Execute button the it will change a boolean value
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cardHandler.click(button, screenX, screenY, CardButton);
        return false;
    }

    //if a card is inside a cardslot and it is released move it into the middle of the slot,
    //if it is outside then move it back to its default pos
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        cardHandler.letGo(screenX, screenY, CardButton);
        return false;
    }

    @Override
    //if a card is clicked on and draged, then move that clicked card
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        cardHandler.dragged(screenX, screenY, CardButton);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void createGrid() {
        grid = new MyGrid(12, 12, MapTile.OPEN);
        //sets conveyerbelt element on map
        grid.set(6, 11, MapTile.CONVEYERBELTEAST);
        grid.set(7, 11, MapTile.CONVEYERBELTEAST);
        grid.set(8, 11, MapTile.CONVEYERBELTEAST);
        grid.set(9, 11, MapTile.CONVEYERBELTSOUTH);
        grid.set(9, 10, MapTile.CONVEYERBELTSOUTH);
        grid.set(9, 9, MapTile.CONVEYERBELTWEST);
        grid.set(8, 9, MapTile.CONVEYERBELTWEST);
        grid.set(7, 9, MapTile.CONVEYERBELTWEST);
        grid.set(6, 9, MapTile.CONVEYERBELTNORTH);
        grid.set(6, 10, MapTile.CONVEYERBELTNORTH);

        grid.set(2, 5, MapTile.CONVEYERBELTEAST);
        grid.set(3, 5, MapTile.CONVEYERBELTEAST);
        grid.set(4, 5, MapTile.CONVEYERBELTNORTH);

        grid.set(4, 6, MapTile.CONVEYERBELTNORTH);
        grid.set(4, 7, MapTile.CONVEYERBELTWEST);
        grid.set(3, 7, MapTile.CONVEYERBELTSOUTH);
        grid.set(3, 6, MapTile.CONVEYERBELTEAST);

        //setting repairsite elements on map
        grid.set(11, 0, MapTile.REPAIRSITE);
        grid.set(2, 10, MapTile.REPAIRSITE);
        //setting lasers on elements on map
        grid.set(3, 0, MapTile.LASER);
        grid.set(3, 1, MapTile.LASER);
        grid.set(3, 2, MapTile.LASER);
        grid.set(3, 3, MapTile.LASER);
        grid.set(3, 4, MapTile.LASER);

        grid.set(1, 3, MapTile.SPINLEFT);
        grid.set(3, 0, MapTile.LASER);
        grid.set(3, 1, MapTile.LASER);
        grid.set(3, 2, MapTile.LASER);
        grid.set(3, 3, MapTile.LASER);
        grid.set(3, 4, MapTile.LASER);

        grid.set(9, 0, MapTile.LASER);
        grid.set(9, 1, MapTile.LASER);
        grid.set(9, 2, MapTile.LASER);

        grid.set(10, 5, MapTile.LASER);
        grid.set(10, 6, MapTile.LASER);
        grid.set(10, 7, MapTile.LASER);
        grid.set(10, 8, MapTile.LASER);

        grid.set(7, 5, MapTile.LASER);
        grid.set(10, 6, MapTile.LASER);
        grid.set(10, 7, MapTile.LASER);

        grid.set(0, 9, MapTile.LASER);
        grid.set(1, 9, MapTile.LASER);
        grid.set(2, 9, MapTile.LASER);
        grid.set(3, 9, MapTile.LASER);
        grid.set(4, 9, MapTile.LASER);


        grid.set(1, 1, MapTile.CHECKPOINT1);
        grid.set(1, 8, MapTile.CHECKPOINT2);
        grid.set(6, 7, MapTile.CHECKPOINT3);
        grid.set(10, 3, MapTile.CHECKPOINT4);

        grid.set(2, 2, MapTile.HOLE);
    }

    public static TiledMap getTiledMap() {
        return tiledMap;
    }

    public static GameMap getIGameMap() {
        return map;
    }

    public void setCamera(float w, float h) {
        //camera that is for scaling viewpoint
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w * 6, h * 6);
        camera.translate(-400, -2700);
        viewPort = new FitViewport(w * 6, h * 6);
    }

    public void buttonCreation(float x, float y) {
        Texture buttonTexture = new Texture(Gdx.files.internal("Models/Button.png"));
        Sprite buttonSprite = new Sprite(buttonTexture);
        buttonSprite.setPosition(x, y);
        CardButton = new Cards(x, y, "", 0, buttonSprite);
    }

    //creation og the stat-board
    public void statBoardCreation(float x, float y){
        Texture statTexture = new Texture(Gdx.files.internal("Models/stats.PNG"));
        statBoardSprite = new Sprite(statTexture);
        statBoardSprite.setPosition(x,y);
        statBoard0= new Cards(x,y, "statBoard",0, statBoardSprite);
    }

    //draw the stat font on top of the board
    public void drawStats(){
        int hp= 9-robot.getDamage();
        font.setColor(0,0,0,1);
        font.draw(batch, "Player1:", statBoard0.getCardSprite().getX()+10,  statBoard0.getCardSprite().getY()+statBoard0.getCardSprite().getHeight()-30);
        font.draw(batch, ""+hp, statBoard0.getCardSprite().getX()+90,  statBoard0.getCardSprite().getY()+statBoard0.getCardSprite().getHeight()-30);
        font.draw(batch, ""+robot.getLives(), statBoard0.getCardSprite().getX()+175,  statBoard0.getCardSprite().getY()+statBoard0.getCardSprite().getHeight()-30);
        font.draw(batch, ""+robot.getFlagsPassed(), statBoard0.getCardSprite().getX()+250,  statBoard0.getCardSprite().getY()+statBoard0.getCardSprite().getHeight()-30);
    }

    public void doTurn() {
        Cards selectedCards[] = cardHandler.getSelectedCards();
        if (selectedCards[0] != null && selectedCards[1] != null && selectedCards[2] != null && selectedCards[3] != null && selectedCards[4] != null && cardHandler.getisDone()) {
            if (turn >= 5) {
                System.out.println("Ferdig med ein heil runde!");
                for (int h = 0; h < 5; h++) {
                    cardHandler.lockDown();
                }
                turn = 0;
                cardHandler.setNotFirst(true);
                cardHandler.nullyFy();
                checkLock(selectedCards);
                cardHandler.setisDone(false);
                cardHandler.setCardSprites();

                System.out.println("\n");
            }
            if (tick % 40 == 0) {
                robot.move(selectedCards[turn]);
                map.move(selectedCards[turn]);
                System.out.println("DidTURN "+(turn+1));
                robot.getSprite().draw(batch);
                turn++;
            }
        }
    }
    public static CardHandler getCardHandler(){
        return cardHandler;
    }

    //cleans up the screen, by removing sprites no longer in use, and adding the sprites that should be locked to the spriteslocked list
    public void checkLock(Cards[] selectedCards){
        for (int v = 0; v < cardHandler.getSpritePos().size(); v++) {
            boolean locked=false;
            for(int i=0; i<selectedCards.length; i++){
                if(selectedCards[i]!=null){
                    //System.out.println(cardHandler.getSpritePos().get(v).getTexture().toString() + " " + selectedCards[i].getCardSprite().getTexture().toString());
                    if(cardHandler.getSpritePos().get(v)==selectedCards[i].getCardSprite()){
                        //System.out.println("LOCKED");
                        locked=true;
                        break;
                    }
                    cardHandler.setSpritesLocked(i, selectedCards[i].getCardSprite());
                }
            }
            if(!locked){
                //System.out.println("GONE!");
                cardHandler.getSpritePos().get(v).setPosition(10000, 10000);
            }
        }
    }
}