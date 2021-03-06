package inf112.skeleton.app;


import Grid.Direction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import map.GameMap;
import map.MapTile;
import Cards.*;
import BoardElements.ExpressBelt;
import BoardElements.Belt;
import BoardElements.*;

public class AIRobot implements IRobot{
    private int id;
    private CardHandler cardHandler;
    private Sprite sprite;
    private Boolean alive = true;
    private int posX = 0;
    private int posY = 0;
    private int[] checkpoint = {posX, posY};
    private int flagsPassed = 0;
    private int lives = 3;
    private int damage = 0;
    private Direction dir = Direction.NORTH;
    private float w = Gdx.graphics.getWidth() * 6;
    private float h = Gdx.graphics.getHeight() * 6;
    private TiledMap tiledMap = RoboRallyDemo.getTiledMap();
    private GameMap gameMap = RoboRallyDemo.getIGameMap();
    private MapProperties prop = tiledMap.getProperties();
    private int mapWidth = prop.get("width", Integer.class);
    private int mapHeight = prop.get("height", Integer.class);
    private int tilePixelWidth = prop.get("tilewidth", Integer.class);
    private int tilePixelHeight = prop.get("tileheight", Integer.class);
    private int x0 = (((Math.round(w) - (tilePixelWidth * mapWidth)) / 2) + (tilePixelWidth / 2)) / 10 -100;
    private int y0 = (((Math.round(h) - (tilePixelHeight * mapHeight)) / 2) + (tilePixelHeight / 2)) / 10 * 3 - 9;
    private int turn = RoboRallyDemo.getTurn();
    private Deck randomDeck;
    private Cards selectedCards[];

    //Initiating Board element objects.
    ExpressBelt ebelt = new ExpressBelt(gameMap);
    Belt belt = new Belt(gameMap);
    Spin spin = new Spin(gameMap);

    public int rng(Deck deck){
        return (int) (Math.random() * deck.getDeckList().size() - 1 + 1);
    }

    public void makeDeck(){
        randomDeck = new Deck();
        for(CardValues value : CardValues.values()) {
            Cards testCard = new Cards(0, 0, value.getName(), value.getPriority(), value.getSprite());
            randomDeck.getDeckList().add(testCard);
        }
    }

    public void fillDeck(){
        selectedCards = new Cards[5];
        for(int j = 0; j<selectedCards.length; j++) {
            int random = rng(randomDeck);
            selectedCards[j] = randomDeck.getDeckList().get(random);
            randomDeck.getDeckList().remove(random);
        }
    }


    public void doTurn(int turn){
        if (RoboRallyDemo.amIAliveAI(id)) {
            makeDeck();
            fillDeck();
            move(selectedCards[turn]);
        }
    }

    public AIRobot(Sprite sprite, int[] checkpoint, int id){
        this.sprite=sprite;
        this.checkpoint = checkpoint;
        this.posX = checkpoint[0];
        this.posY = checkpoint[1];
        this.id = id;
    }

    public void setDirection(Direction dir){
        this.dir = dir;
    }

    public void moveSprite(float x, float y){
        this.sprite.setPosition(x, y);
    }

    public void rotateSprite(float z){
        this.sprite.rotate(z);
    }

    public Boolean getAlive() {
        return this.alive;
    }

    public int getPosX(){
        return this.posX;
    }

    public int getPosY(){
        return this.posY;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public int[] getCheckpoint() {
        return this.checkpoint;
    }

    public int getFlagsPassed() {
        return this.flagsPassed;
    }

    public Direction getDirection() {
        return dir;
    }

    public int getLives() {
        return this.lives;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getSpriteX() {
        return this.x0 + (this.posX * (this.tilePixelWidth / 6));
    }

    public int getSpriteY() {
        return this.y0 + (this.posY * (this.tilePixelWidth / 6));
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setCheckpoint(int x, int y){
        this.checkpoint[0] = x;
        this.checkpoint[1] = y;
    }

    public void setPosX(int newX) {
        this.posX = newX;
    }

    public void setPosY(int newY) {
        this.posY = newY;
    }

    public void setDamage(int newDamage) {
        this.damage = newDamage;
    }

    public void setLives(int newLives) {
        this.lives = newLives;
    }

    public int getTilePixelWidth(){
        return this.tilePixelWidth;
    }

    public int getTilePixelHeight(){
        return this.tilePixelHeight;
    }

    public void rotate_right() {
        if(this.getDirection() == Direction.WEST){
            this.dir = Direction.NORTH;
        }
        else if (this.getDirection() == Direction.NORTH){
            this.dir = Direction.EAST;
        }
        else if (this.getDirection() == Direction.EAST){
            this.dir = Direction.SOUTH;
        }
        else if (this.getDirection() == Direction.SOUTH){
            this.dir = Direction.WEST;
        }
        this.sprite.rotate(-90);
    }

    public void rotate_left() {
        if(this.getDirection() == Direction.NORTH){
            this.dir = Direction.WEST;
        }
        else if (this.getDirection() == Direction.WEST){
            this.dir = Direction.SOUTH;
        }
        else if (this.getDirection() == Direction.SOUTH){
            this.dir = Direction.EAST;
        }
        else if (this.getDirection() == Direction.EAST){
            this.dir = Direction.NORTH;
        }
        this.sprite.rotate(90);
    }

    public void moveForward(int amount){
        Direction current_direction = this.getDirection();
        if (current_direction == Direction.NORTH) {
            this.setPosY(this.getPosY() + amount);
            this.sprite.setPosition(this.sprite.getX(), this.sprite.getY() + (amount * (this.tilePixelWidth / 6))); // temp moving sprite
        }
        else if (current_direction == Direction.EAST) {
            this.setPosX(this.getPosX() + amount);
            this.sprite.setPosition(this.sprite.getX() + (amount * (this.tilePixelWidth / 6)), this.sprite.getY());
        }
        else if (current_direction == Direction.SOUTH) {
            this.setPosY(this.getPosY() - amount);
            this.sprite.setPosition(this.sprite.getX(), this.sprite.getY() - (amount * (this.tilePixelWidth / 6)));
        }
        else if (current_direction == Direction.WEST) {
            this.setPosX(this.getPosX() - amount);
            this.sprite.setPosition(this.sprite.getX() - (amount * (this.tilePixelWidth / 6)), this.sprite.getY());
        }
        else {
            System.out.println("Something went terribly wrong");
        }
        if (gameMap.isHole(this.posX, this.posY)) {
            this.died();
        }
    }

    public void move(Cards card){ // gets the command from a card and figures out which command to execute
        String command = card.getCardSprite().getTexture().toString();
        switch (command){
            case "Models/AlleBevegelseKortUtenPrioritet/BackUp.png":
                canMove(1,-1);
                break;
            case "Models/AlleBevegelseKortUtenPrioritet/Move-1.png":
                canMove(1,1);
                break;
            case "Models/AlleBevegelseKortUtenPrioritet/Move-2.png":
                canMove(2,1);
                break;
            case "Models/AlleBevegelseKortUtenPrioritet/Move-3.png":
                canMove(3,1);
                break;
            case "Models/AlleBevegelseKortUtenPrioritet/Rotate-90.png":
                this.rotate_right();
                break;
            case "Models/AlleBevegelseKortUtenPrioritet/Rotate-180.png":
                this.rotate_right();
                this.rotate_right();
                break;
            case "Models/AlleBevegelseKortUtenPrioritet/Rotate-C90.png":
                this.rotate_left();
                break;
            default:
        }

        ExpressBelt.doExpressBelt(this);
        Belt.doBelt(this);
        Spin.doSpin(this);

        //gameMap.fireLasers(this);
        //add method to fire my laser
        if (gameMap.isCheckpoint(this.posX, this.posY, this.flagsPassed)) {
            this.flagsPassed += 1;
            this.setCheckpoint(this.getPosX(), this.getPosY());
            System.out.println("You made it to backup number " + this.flagsPassed);
        }
        if (gameMap.isRepairSite(this.posX, this.posY, this.turn) == 1) {
            this.setCheckpoint(this.posX, this.posY);
            System.out.println("Backup on repairsite!");
        }
        else if (gameMap.isRepairSite(this.posX, this.posY, this.turn) == 2) {
            this.setCheckpoint(this.posX, this.posY);
            if(this.damage != 0) {this.damage -=1;}
        }
        else if (gameMap.isRepairSite(this.posX, this.posY, this.turn) == 3) {
            this.setCheckpoint(this.posX, this.posY);
            if(this.damage > 1) {
                this.damage -= 2; // put in choice for option cards.
            }
            else if(this.damage == 1) {this.damage = 0;}
        }
    }

    public void died() {
        this.lives -= 1; // loose an option card of the players choice
        this.damage = 0;
        this.takeDamage();
        this.takeDamage();
        this.alive = false;
        if (this.lives == 0) {
            // the robot needs to be deleted from the game.
            System.out.println("AI " + id + " died.");
            RoboRallyDemo.killMe(id, true);
        }
        else {
            // moves the sprite the appropriate amount in both x and y direction to the robots backup
            if(this.getPosX() <= this.getCheckpoint()[0] && this.getPosY() <= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[0] - this.getPosX())), this.sprite.getY() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[1] - this.getPosY())));
            }
            else if(this.getPosX() >= this.getCheckpoint()[0] && this.getPosY() <= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() - ((this.tilePixelWidth / 6) * (this.getPosX() - this.getCheckpoint()[0])), this.sprite.getY() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[1] - this.getPosY())));
            }
            else if(this.getPosX() <= this.getCheckpoint()[0] && this.getPosY() >= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[0] - this.getPosX())), this.sprite.getY() - ((this.tilePixelWidth / 6) * (this.getPosY() - this.getCheckpoint()[1])));
            }
            else if(this.getPosX() >= this.getCheckpoint()[0] && this.getPosY() >= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() - ((this.tilePixelWidth / 6) * (this.getPosX() - this.getCheckpoint()[0])), this.sprite.getY() - ((this.tilePixelWidth / 6) * (this.getPosY() - this.getCheckpoint()[1])));
            }
            else {
                System.out.println("Should definitely not be possible");
            }
            if (this.dir == Direction.EAST) {
                this.rotate_left();
            }
            else if (this.dir == Direction.SOUTH) {
                this.rotate_right();
                this.rotate_right();
            }
            else if (this.dir == Direction.WEST) {
                this.rotate_right();
            }
            this.dir = Direction.NORTH;
            this.setPosX(this.getCheckpoint()[0]); //update internal numbers of robot location
            this.setPosY(this.getCheckpoint()[1]);
        }
    }

    public void takeDamage() {
        if (this.damage < 9) {
            this.damage += 1;
            cardHandler = RoboRallyDemo.getCardHandler();
            cardHandler.lockDown();
        }
        else {
            this.died();
        }
    }

    public int checkNext(int amount) {
        if (this.dir == Direction.NORTH && (this.posY + amount == 12 || this.posY + amount == -1)) {
            return -1;
        }
        else if (this.dir == Direction.EAST && (this.posX + amount == 12 || this.posX + amount == -1)) {
            return -1;
        }
        else if (this.dir == Direction.SOUTH && (this.posY - amount == -1 || this.posY - amount == 12)) {
            return -1;
        }
        else if (this.dir == Direction.WEST && (this.posX - amount == -1 || this.posX - amount == 12)) {
            return -1;
        }
        else if (gameMap.wallNearby(this.dir, this.posX, this.posY, amount)) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public void canMove(int loops, int amount) {
        for (int i = 0; i < loops; i++){
            if (this.checkNext(amount) == 1) {
                this.moveForward(amount);
            }
            else if (this.checkNext(amount) == -1) {
                this.died();
                break;
            }
            else if (this.checkNext(amount) == 0) {
                System.out.println("You hit a wall!");
                break;
            }
            else {
                break;
            }
        }
    }

    public void robotFireLasers(AIRobot[] robot) {
        for (int i = 0; i < robot.length; i++) {
            if (this.getDirection() == Direction.NORTH) {
                boolean targetHit = false;
                int tempY1 = this.getPosY()+1;
                while (targetHit == false && tempY1 < 12) {
                    if (robot[i].getPosX() == this.getPosX() && robot[i].getPosY() == tempY1) {
                        robot[i].takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get((double) this.getPosX(), tempY1 + 0.5) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempY1++;
                }
            } else if (this.getDirection() == Direction.EAST) {
                boolean targetHit = false;
                int tempX1 = this.getPosX()+1;
                while (targetHit == false && tempX1 != 12) {
                    if (robot[i].getPosX() == tempX1 && robot[i].getPosY() == this.getPosY()) {
                        robot[i].takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get(tempX1 + 0.5, this.getPosY()) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempX1++;
                }
            } else if (this.getDirection() == Direction.SOUTH) {
                boolean targetHit = false;
                int tempY1 = this.getPosY()-1;
                while (targetHit == false && tempY1 != -1) {
                    if (robot[i].getPosX() == this.getPosX() && robot[i].getPosY() == tempY1) {
                        robot[i].takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get(this.getPosX(), tempY1 - 0.5) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempY1--;
                }
            } else if (this.getDirection() == Direction.WEST) {
                boolean targetHit = false;
                int tempX1 = this.getPosX()-1;
                while (targetHit == false && tempX1 != -1) {
                    if (robot[i].getPosX() == tempX1 && robot[i].getPosY() == this.getPosY()) {
                        robot[i].takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get(tempX1 - 0.5, this.getPosY()) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempX1--;
                }
            }

        }
    }

    public void robotFireLasers(Robot robot) {
            if (this.getDirection() == Direction.NORTH) {
                boolean targetHit = false;
                int tempY1 = this.getPosY()+1;
                while (targetHit == false && tempY1 < 12) {
                    if (robot.getPosX() == this.getPosX() && robot.getPosY() == tempY1) {
                        robot.takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get((double) this.getPosX(), tempY1 + 0.5) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempY1++;
                }
            } else if (this.getDirection() == Direction.EAST) {
                boolean targetHit = false;
                int tempX1 = this.getPosX()+1;
                while (targetHit == false && tempX1 != 12) {
                    if (robot.getPosX() == tempX1 && robot.getPosY() == this.getPosY()) {
                        robot.takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get(tempX1 + 0.5, this.getPosY()) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempX1++;
                }
            } else if (this.getDirection() == Direction.SOUTH) {
                boolean targetHit = false;
                int tempY1 = this.getPosY()-1;
                while (targetHit == false && tempY1 != -1) {
                    if (robot.getPosX() == this.getPosX() && robot.getPosY() == tempY1) {
                        robot.takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get(this.getPosX(), tempY1 - 0.5) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempY1--;
                }
            } else if (this.getDirection() == Direction.WEST) {
                boolean targetHit = false;
                int tempX1 = this.getPosX()-1;
                while (targetHit == false && tempX1 != -1) {
                    if (robot.getPosX() == tempX1 && robot.getPosY() == this.getPosY()) {
                        robot.takeDamage();
                        targetHit = true;
                    } else if (gameMap.getTiles().get(tempX1 - 0.5, this.getPosY()) == MapTile.WALL) {
                        targetHit = true;
                    }
                    tempX1--;
                }
            }
    }
}
