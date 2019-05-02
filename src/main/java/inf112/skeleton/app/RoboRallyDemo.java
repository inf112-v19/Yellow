package inf112.skeleton.app;

import Grid.IGrid;
import Grid.MyGrid;
import Server.Client;
import Server.Server;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import map.GameMap;
import map.MapTile;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class RoboRallyDemo implements ApplicationListener, InputProcessor {
    private static TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private int i = 0;
    private int tick = 0;
    private static int turn = 0;
    private static Boolean isEndOfTurn = false;

    private Button endTurnButton;
    private Button powerdownButton;
    private static Robot robot;
    private AIRobot AIrobot;
    private FitViewport viewPort;
    private static CardHandler cardHandler;
    private Cards statBoard0;
    private Cards card;

    private Cards selectedCards[];

    private boolean firstRund = true;
    private mainMenu mainMenu;

    private boolean firstround=true;

    private SpriteBatch batch;
    private Texture texture;
    private Sprite sprite;
    private Sprite AIsprite;
    private float posX, posY;
    private BitmapFont font;
    private Sprite statBoardSprite;
    private static GameMap map;
    private IGrid grid;
    private int[][] starts = {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}};
    private Server server;
    private static Client client;
    private static Robot[] robots = new Robot[8];
    private Sprite[] sprites = new Sprite[8];
    private Texture[] textures = new Texture[8];
    private int clientCount;
    private String[] colors = {"Gold", "Cyan", "Green", "Red", "Blue", "Purple", "Basil", "Lemon"};
    private static int ID;
    private static boolean ready[] = {false, false, false, false, false, false, false, false};
    private String[][] moves;
    private int[] order;
    private static boolean singlePlayerMode = false;
    private static AIRobot[] AIs = new AIRobot[3];
    private int[][] AIstarts = {{3,0}, {6,0}, {9,0}};

    public static boolean getSinglePlayerMode() {
        return singlePlayerMode;
    }

    public static void setReady(int id) {
        ready[id] = true;
    }

    public static void killMe(int id, boolean AI) {
        if(singlePlayerMode && !AI) {
            robot = null;
        }
        else if (AI){
            AIs[id] = null;
        }
        else {
            robots[id] = null;
            String died = "/d/" + id + "/e/";
            client.getBackendClient().send(died.getBytes());
        }
    }

    public static boolean amIAlive() {
        if(singlePlayerMode && robot != null) {
            return true;
        }
        else if (!singlePlayerMode && robots[getID()] != null) {
            return true;
        }
        return false;
    }

    public static boolean amIAliveAI(int id) {
        if(AIs[id] != null) {
            return true;
        }
        return false;
    }

    public static void setDead(int id) {
        robots[id] = null;
    }

    public  void createv2(){
        batch = new SpriteBatch();
        tiledMap = new TmxMapLoader().load("Models/roborallymap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        createGrid();
        map = new GameMap(grid);

        if (singlePlayerMode) {
            texture = new Texture(Gdx.files.internal("Models/tank0.png"));
            sprite = new Sprite(texture);
            robot = new Robot(sprite, starts[0]);
            for (int i = 0; i < AIs.length; i++) {
                textures[i] = new Texture(Gdx.files.internal("Models/tank" + (i+1) + ".png"));
                sprites[i] = new Sprite(textures[i]);
                AIs[i] = new AIRobot(sprites[i], AIstarts[i], i);
                System.out.println("created AI" + i);
            }
            for(int i = 0; i < AIs.length; i++) {
                if (AIs[i] != null) {
                    sprites[i].setPosition(AIs[i].getSpriteX(), AIs[i].getSpriteY());
                }
            }
            if (robot != null) {
                sprite.setPosition(robot.getSpriteX(), robot.getSpriteY());
            }
        }

        if (!singlePlayerMode) {
            clientCount = client.getClientCount();
            order = new int[clientCount * 5];
            moves = new String[clientCount][5];
            for (int i = 0; i < clientCount; i++) {
                textures[i] = new Texture(Gdx.files.internal("Models/tank" + (i) + ".png"));
                sprites[i] = new Sprite(textures[i]);
                robots[i] = new Robot(sprites[i], starts[i]);
                System.out.println("created robot" + i);
            }
            for (int i = 0; i < clientCount; i++) {
                if (robots[i] != null) {
                    sprites[i].setPosition(robots[i].getSpriteX(), robots[i].getSpriteY());
                }
            }
        }

        //create the card that Is clicked
        Texture cardTexture = new Texture(Gdx.files.internal("Models/AlleBevegelseKortUtenPrioritet/genericCard.png"));
        if (singlePlayerMode) {
            cardHandler = new CardHandler(batch, robot, map);
        } else if (!singlePlayerMode) {
            cardHandler = new CardHandler(batch, robots[ID], map);
        }
        font = new BitmapFont();
        //create the end turn button
        endTurnButtonCreation(700, 510);
        statBoardCreation(700, 910);

        //set the position of all the cardsprites
        cardHandler.setCardSprites();

        //create the 9 cards cards
        cardHandler.createInitialDecklist();

        //creation of the 5 cardSlots
        cardHandler.createCardSlots();

        Gdx.input.setInputProcessor(this);
    }


    //create the initial state of the game
    @Override
    public void create() {
        if (firstRund) {
            batch = new SpriteBatch();
            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();
            //set the camera
            setCamera(w, h);
            mainMenu = new mainMenu(batch);
            firstRund = false;
        }
        if (mainMenu.getMainRunning()) {
            mainMenu.startMenu();
            //creation of the map
        }
        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void dispose() {
        batch.dispose();
//        texture.dispose();
    }

    //rendering of the map and all the sprites
    @Override
    public void render() {
        Gdx.gl.glClearColor(128 / 255f, 128 / 255f, 128 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (mainMenu.getMainRunning()) {
            batch.begin();
            mainMenu.render();
        }else{
            selectedCards = cardHandler.getSelectedCards();
            if(selectedCards[0]!=null){
                if(selectedCards[0].getName()=="clickedCard"){
                    cardHandler.crushBug();
                }
            }
            camera.update();
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
            //Cards selectedCards[] = cardHandler.getSelectedCards();
            batch.begin();
            if (singlePlayerMode) {
                for (int i = 0; i < AIs.length; i++){
                    if (AIs[i] != null) {
                        sprites[i].draw(batch);
                    }
                }
                if (robot != null) {
                    sprite.draw(batch);
                }
            } else {
                for (int i = 0; i < clientCount; i++) {
                    if (robots[i] != null) {
                        sprites[i].draw(batch);
                    }
                }
            }
            doTurn();
            //draw the cardslots
            if (amIAlive()) {
                cardHandler.drawCardSlots();
                cardHandler.drawLockedList();
                //draw button
                powerdownButtonCreation(700, 710);

                endTurnButton.getSprite().draw(batch);

                powerdownButton.getSprite().draw(batch);

                //draw Cards
                cardHandler.drawCards();
            }
            statBoard0.getCardSprite().draw(batch);

            drawStats();

            tick++;
        }

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

    @Override
    public boolean keyDown(int keycode) {
        return false;
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
    //if you click a card and it is inside a cardSlot a boolean will change (isClicked=true), this I will use in the touchUp method
    // And if you click the Execute button the it will change a boolean value
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (mainMenu.getMainRunning()) {
            if (mainMenu.getClientBtn().buttonClicked(screenX, screenY, mainMenu.getClientBtn())) {
                System.out.println("DU TRYKKET PÅ CLIENT"); // TODO remove
                if(client != null) {
                    System.out.println("You can only have one client per computer.");
                } else {
                    client = new Client("Player", "10.111.15.150", 55557);
                    boolean wait = false;
                    while (!wait) {
                        System.out.println("Waiting for the server to start the game."); // TODO remove
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (client.getStarted()) {
                            System.out.println("create blir callet"); // TODO remove
                            createv2();
                        }
                        wait = client.getStarted();
                    }
                }
            }
            if (mainMenu.getServerBtn().buttonClicked(screenX, screenY, mainMenu.getServerBtn())) {
                System.out.println("DU TRYKKET PÅ SERVER"); // TODO remove
                if (server == null) {
                    server = new Server(55557);
                    client = new Client("Player", "localhost", 55557);
                }
                else {
                    System.out.println("You already have a server running!");
                    System.out.println("Wait for clients to connect, and start the server.");
                    System.out.println("For singleplayer mode (with 3 AIs) please restart the game and press start directly.");
                }
            }
            if (mainMenu.getStartBtn().buttonClicked(screenX, screenY, mainMenu.getStartBtn())) {
                System.out.println("DU TRYKKET PÅ START"); // TODO remove
                if (server != null) {
                    System.out.println("Starting the multiplayer game!");
                    server.setStarted(true);
                    client.getBackendClient().send("/s//e/".getBytes());
                    mainMenu.setMainRunning(false);
                    createv2();
                }
                else if (server == null) {
                    singlePlayerMode = true;
                    System.out.println("Starting singleplayer game!");
                    setID(0);
                    createv2();
                    mainMenu.setMainRunning(false);
                }
            }

        } else {
            endTurnButton.buttonClicked(screenX, screenY, endTurnButton);
            powerdownButton.buttonClicked(screenX, screenY, powerdownButton);
            cardHandler.click(Input.Buttons.LEFT, screenX, screenY);
        }
        return false;
    }

    public Robot getRobot() {
        return robot;
    }

    //if a card is inside a cardslot and it is released move it into the middle of the slot,
    //if it is outside then move it back to its default pos
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!mainMenu.getMainRunning()) {
            cardHandler.letGo(screenX, screenY);
        }
        return false;
    }

    public boolean insideCard(float screenX, float screenY, Cards card) {
        float NewscreenY = Gdx.graphics.getHeight() - screenY;
        return (screenX > card.getCardSprite().getX()) && (screenX < (card.getCardSprite().getX() + card.getCardSprite().getWidth())) && (NewscreenY > card.getCardSprite().getY()) && (NewscreenY < (card.getCardSprite().getY() + card.getCardSprite().getHeight()));
    }

    @Override
    //if a card is clicked on and draged, then move that clicked card
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!mainMenu.getMainRunning()){
            cardHandler.dragged(screenX, screenY);
        }
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
        grid.set(6, 11,MapTile.EXPRESSCONVEYERBELTNORTHTOEAST);
        grid.set(7, 11,MapTile.EXPRESSCONVEYERBELTEAST);
        grid.set(8, 11,MapTile.EXPRESSCONVEYERBELTEAST);
        grid.set(9, 11,MapTile.EXPRESSCONVEYERBELTEASTTOSOUTH);
        grid.set(9, 10,MapTile.EXPRESSCONVEYERBELTSOUTH);
        grid.set(9, 9, MapTile.EXPRESSCONVEYERBELTSOUTHTOWEST);
        grid.set(8, 9, MapTile.EXPRESSCONVEYERBELTWEST);
        grid.set(7, 9, MapTile.EXPRESSCONVEYERBELTWEST);
        grid.set(6, 9, MapTile.EXPRESSCONVEYERBELTWESTTONORTH);
        grid.set(6, 10,MapTile.EXPRESSCONVEYERBELTNORTH);

        grid.set(2, 5, MapTile.EXPRESSCONVEYERBELTEAST);
        grid.set(3, 5, MapTile.EXPRESSCONVEYERBELTEAST);
        grid.set(4, 5, MapTile.EXPRESSCONVEYERBELTEASTTONORTH);

        grid.set(4, 6, MapTile.EXPRESSCONVEYERBELTEASTTONORTH);
        grid.set(4, 7, MapTile.EXPRESSCONVEYERBELTNORTHTOWEST);
        grid.set(3, 7, MapTile.EXPRESSCONVEYERBELTWESTTOSOUTH);
        grid.set(3, 6, MapTile.EXPRESSCONVEYERBELTSOUTHTOEAST);

        grid.set(8, 4, MapTile.CONVEYERBELTNORTHTOEAST);
        grid.set(8, 3, MapTile.CONVEYERBELTWESTTONORTH);
        grid.set(9, 4, MapTile.CONVEYERBELTEASTTOSOUTH);
        grid.set(9, 3, MapTile.CONVEYERBELTSOUTHTOWEST);

        //setting repairsite elements on map
        grid.set(11, 0,MapTile.REPAIRSITE);
        grid.set(2, 10,MapTile.REPAIRSITE);

        grid.set(1,3,  MapTile.SPINLEFT);
        grid.set(1,6,  MapTile.SPINLEFT);
        grid.set(7,10, MapTile.SPINLEFT);
        grid.set(8,2,  MapTile.SPINRIGHT);
        grid.set(8,6 , MapTile.SPINRIGHT);
        grid.set(8,10, MapTile.SPINRIGHT);

        grid.set(9,0,  MapTile.LASERNORTH);

        grid.set(10, 5,MapTile.LASERNORTH);

        grid.set(7, 5, MapTile.LASERNORTH);

        grid.set(0, 9, MapTile.LASEREAST);

        grid.set(1, 1, MapTile.CHECKPOINT1);
        grid.set(1, 8, MapTile.CHECKPOINT2);
        grid.set(6, 7, MapTile.CHECKPOINT3);
        grid.set(10, 3,MapTile.CHECKPOINT4);

        grid.set(2, 2, MapTile.HOLE);

        grid.set(9.0, -0.5, MapTile.WALL);
        grid.set(-0.5, 9.0, MapTile.WALL);

        grid.set(4.5,2.0,MapTile.WALL);
        grid.set(4.5,3.0,MapTile.WALL);
        grid.set(4.5,4.0,MapTile.WALL);
        grid.set(4.5,5.0,MapTile.WALL);
        grid.set(4.5,6.0,MapTile.WALL);
        grid.set(4.5,7.0,MapTile.WALL);
        grid.set(4.5,8.0,MapTile.WALL);
        grid.set(4.5,9.0,MapTile.WALL);

        grid.set(0.0,4.5,MapTile.WALL);
        grid.set(1.0,4.5,MapTile.WALL);
        grid.set(2.0,4.5,MapTile.WALL);
        grid.set(3.0,4.5,MapTile.WALL);
        grid.set(4.0,4.5,MapTile.WALL);
        grid.set(5.0,4.5,MapTile.WALL);
        grid.set(6.0,4.5,MapTile.WALL);
        grid.set(7.0,4.5,MapTile.WALL);
        grid.set(8.0,4.5,MapTile.WALL);
        grid.set(9.0,4.5,MapTile.WALL);
        grid.set(10.0,4.5,MapTile.WALL);

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

    public void endTurnButtonCreation(int x, int y) {
        Texture buttonTexture = new Texture(Gdx.files.internal("Models/Button.png"));
        Sprite buttonSprite = new Sprite(buttonTexture);
        buttonSprite.setPosition(x, y);
        endTurnButton = new Button(x, y, "endRoundButton", buttonSprite);
    }


    public void powerdownButtonCreation(int x, int y) {
        if (singlePlayerMode) {
            if (robot != null && !robot.getInitPowerdown()) {
                Texture powerdownbuttonTexture = new Texture(Gdx.files.internal("Models/Powerdown_inactive.jpg"));
                Sprite powerdownbuttonSprite = new Sprite(powerdownbuttonTexture);
                powerdownbuttonSprite.setPosition(x, y);
                this.powerdownButton = new Button(x, y, "powerDown_inactive", powerdownbuttonSprite);

            } else if (robot != null && robot.getInitPowerdown()){
                Texture powerdownbuttonTexture = new Texture(Gdx.files.internal("Models/Powerdown_active.jpg"));
                Sprite powerdownbuttonSprite = new Sprite(powerdownbuttonTexture);
                powerdownbuttonSprite.setPosition(x, y);
                this.powerdownButton = new Button(x, y, "powerDown_active", powerdownbuttonSprite);
            }
        }
        if (!singlePlayerMode) {
            for(int i = 0; i < clientCount; i++) {
                if (robots[i] != null && !robots[i].getInitPowerdown()) {
                    Texture powerdownbuttonTexture = new Texture(Gdx.files.internal("Models/Powerdown_inactive.jpg"));
                    Sprite powerdownbuttonSprite = new Sprite(powerdownbuttonTexture);
                    powerdownbuttonSprite.setPosition(x, y);
                    powerdownButton = new Button(x, y, "powerDown_inactive", powerdownbuttonSprite);
                }
                else if (robots[i] != null && robots[i].getInitPowerdown()){
                    Texture powerdownbuttonTexture = new Texture(Gdx.files.internal("Models/Powerdown_active.jpg"));
                    Sprite powerdownbuttonSprite = new Sprite(powerdownbuttonTexture);
                    powerdownbuttonSprite.setPosition(x, y);
                    powerdownButton = new Button(x, y, "powerDown_active", powerdownbuttonSprite);
                }
            }

        }
    }





    //creation og the stat-board
    public void statBoardCreation ( float x, float y){
        Texture statTexture = new Texture(Gdx.files.internal("Models/stats.PNG"));
        statBoardSprite = new Sprite(statTexture);
        statBoardSprite.setPosition(x, y);
        statBoard0 = new Cards(x, y, "statBoard", 0, statBoardSprite);
    }

    //draw the stat font on top of the board
    public void drawStats(){
        if (!singlePlayerMode) {
            for(int i = 0; i < clientCount; i++){
                if (robots[i] != null) {
                    int hp = 9 - robots[i].getDamage();
                    font.setColor(1, 0, 0, 1);
                    if(ready[i]) {
                        drawReady(i, ID);
                    }
                    else if (i == ID) {
                        String you = colors[i]+ " (you)";
                        font.draw(batch, you + ":", statBoard0.getCardSprite().getX() + 10, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
                    }
                    else {
                        font.draw(batch, colors[i] + ":", statBoard0.getCardSprite().getX() + 10, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
                    }
                    font.setColor(0,0,0,1);
                    font.draw(batch, "" + hp, statBoard0.getCardSprite().getX() + 90, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
                    font.draw(batch, "" + robots[i].getLives(), statBoard0.getCardSprite().getX() + 175, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
                    font.draw(batch, "" + robots[i].getFlagsPassed(), statBoard0.getCardSprite().getX() + 250, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
                }
            }
        } else {
            if (robot != null) {
                int hp = 9 - robot.getDamage();
                font.setColor(0, 0, 0, 1);

                String you = colors[0]+ " (you)";
                font.draw(batch, you + ":", statBoard0.getCardSprite().getX() + 10, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30);
                font.draw(batch, "" + hp, statBoard0.getCardSprite().getX() + 90, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30);
                font.draw(batch, "" + robot.getLives(), statBoard0.getCardSprite().getX() + 175, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30);
                font.draw(batch, "" + robot.getFlagsPassed(), statBoard0.getCardSprite().getX() + 250, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30);
            }

            for(int i = 0; i < AIs.length; i++) {
                if (AIs[i] != null) {
                    int hpAI = 9 - AIs[i].getDamage();
                    String AI = colors[i+1] + " (AI)";
                    font.draw(batch, AI + ":", statBoard0.getCardSprite().getX() + 10, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25 - 25);
                    font.draw(batch, "" + hpAI, statBoard0.getCardSprite().getX() + 90, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25 - 25);
                    font.draw(batch, "" + AIs[i].getLives(), statBoard0.getCardSprite().getX() + 175, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25 - 25);
                    font.draw(batch, "" + AIs[i].getFlagsPassed(), statBoard0.getCardSprite().getX() + 250, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25 - 25);
                }
            }
        }
    }


    public void drawReady(int i, int ID){
        font.setColor(0, 1, 0, 1);
        if (i == ID) {
            String you = colors[i] + " (you)";
            font.draw(batch, you + ":", statBoard0.getCardSprite().getX() + 10, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
        } else {
            font.draw(batch, colors[i] + ":", statBoard0.getCardSprite().getX() + 10, statBoard0.getCardSprite().getY() + statBoard0.getCardSprite().getHeight() - 30 - i * 25);
        }
    }


    //support metode
    public void printSelectedCards() {
        selectedCards = cardHandler.getSelectedCards();
        for (int i = 0; i < selectedCards.length; i++) {
            if (selectedCards[i] != null) {
                System.out.println("Card in cardslot" + i + " " + selectedCards[i].getName());
            }
        }
    }




    public void doTurn () {
        selectedCards = cardHandler.getSelectedCards();

        if (areCardSlotsFull() && cardHandler.getisDone() && checkMode()) {
            if (!singlePlayerMode) {
                client.getBackendClient().send("/o//e/".getBytes());
                moves = client.getMoves();
                order = client.getOrder();
            }
            if (turn >= 5) {
                System.out.println("Ferdig med ein heil runde!");
                if (!singlePlayerMode) {
                    for (int i = 0; i < clientCount; i++) {
                        if (robots[i] != null) {
                            robots[i].setAlive(true);
                        }
                    }
                }
                else {
                    if (robot != null) {
                        robot.setAlive(true);
                    }
                    for(int i = 0; i < AIs.length; i++) {
                        if (AIs[i] != null) {
                            AIs[i].setAlive(true);
                        }
                    }
                }
                turn = 0;
                cardHandler.setNotFirst(true);
                cardHandler.nullyFy();
                checkLock(selectedCards);
                cardHandler.setisDone(false);
                cardHandler.setCardSprites();

                System.out.println("\n");
                if (!singlePlayerMode ) {
                    for(int i = 0; i < clientCount; i++) {
                        ready[i] = false;
                    }
                    if (server != null) {
                        server.roundStart();
                    }
                    drawStats();
                }
            }
            if (tick % 500 == 0) {
                if (singlePlayerMode) {
                    if (robot != null && robot.getAlive()) {
                        robot.move(selectedCards[turn].getName());
                    }
                    for (int i = 0; i < AIs.length; i++) {
                        if (AIs[i] != null && AIs[i].getAlive()) {
                            AIs[i].doTurn(turn);
                        }
                    }
                    for (int i=0; i<AIs.length; i++){
                        AIs[i].robotFireLasers(AIs);
                    }
                    System.out.println("AIDOINGMOVE!: " + turn);
                    System.out.println("DidTURN " + (turn));
                    turn++;
                    if (robot != null) {
                        robot.getSprite().draw(batch);
                    }
                    for (int i = 0; i < AIs.length; i++) {
                        if (AIs[i] != null) {
                            AIs[i].getSprite().draw(batch);
                        }
                    }

                }
                else if (!singlePlayerMode) {
                    for (int j = 0; j < clientCount; j++) {
                        if (robots[order[turn + j]] != null && robots[order[turn + j]].getAlive()) {
                            robots[order[turn + j]].move(moves[order[turn + j]][turn]);
                        }
                    }
                    turn++;
                    for (int i = 0; i < clientCount; i++) {
                        if (robots[i] != null) {
                            robots[i].getSprite().draw(batch);
                        }
                    }
                    System.out.println("DidTURN " + (turn));
                }
            }
        }
    }

    private boolean areCardSlotsFull() {
        if(selectedCards[0] != null && selectedCards[1] != null && selectedCards[2] != null && selectedCards[3] != null && selectedCards[4] != null && amIAlive()) {
            return true;
        }
        else if(!amIAlive()) {
            return true;
        }
        return false;
    }

    public static CardHandler getCardHandler(){
        return cardHandler;
    }

    //cleans up the screen, by removing sprites no longer in use
    public void checkLock(Cards[] selectedCards){
        for (int v = 0; v < cardHandler.getSpritePos().size(); v++) {
            boolean locked=false;
            for(int i=0; i<selectedCards.length; i++){
                if(selectedCards[i]!=null){
                    if(cardHandler.getSpritePos().get(v)==selectedCards[i].getCardSprite()){
                        locked=true;
                        break;
                    }
                }
            }
            if(!locked){
                cardHandler.getSpritePos().get(v).setPosition(10000, 10000);
            }
        }
    }

    public static boolean getEndOfTurn() {
        return isEndOfTurn;
    }


    public static int getTurn() {
        return turn;
    }

    public static void setID(int id){
        ID = id;
    }

    public static int getID() {
        return ID;
    }

    public static Client getClient() {
        return client;
    }

    public boolean checkMode() {
        if(singlePlayerMode) {
            return true;
        } else {
            return client.askReady();
        }
    }

    /*private void createWindow() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String disconnect = "/d/" + client.getID() + "/e/";
                client.send(disconnect.getBytes());
                client.setRunning(false);
                client.getBackendClient().close();

            }
        });
    }*/
}




