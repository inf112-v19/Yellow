package inf112.skeleton.app;

import BoardElements.Belt;
import BoardElements.ExpressBelt;
import BoardElements.Spin;
import Grid.Direction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import map.GameMap;
import map.MapTile;
import Cards.*;


public class Robot  implements IRobot{
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
    private int x0 = (((Math.round(w) - (tilePixelWidth * mapWidth)) / 2) + (tilePixelWidth / 2)) / 10 - 100;
    private int y0 = (((Math.round(h) - (tilePixelHeight * mapHeight)) / 2) + (tilePixelHeight / 2)) / 10 * 3 - 9;
    private int turn = RoboRallyDemo.getTurn();

    //Initiating Board element objects.
    ExpressBelt ebelt = new ExpressBelt(gameMap);
    Belt belt = new Belt(gameMap);
    Spin spin = new Spin(gameMap);
    private boolean initPowerdown = false;
    private boolean execPowerdown = false;

    private int indexToBePushed;
    private Direction dirToBePushed;
    private Boolean willDie;

    public Robot(Sprite sprite) {
        this.sprite = sprite;
    }

    public Robot(int[] checkpoint) {
        this.checkpoint = checkpoint;
        this.posX = checkpoint[0];
        this.posY = checkpoint[1];
    }

    public Robot(Sprite sprite, int[] checkpoint) {
        this.sprite = sprite;
        this.checkpoint = checkpoint;
        this.posX = checkpoint[0];
        this.posY = checkpoint[1];
        this.willDie = false;
    }

    public Boolean getAlive() {
        return this.alive;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public Sprite getSprite() {
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

    public void moveSprite(float x, float y){
        this.sprite.setPosition(x, y);
    }

    public void rotateSprite(float z){
        this.sprite.rotate(z);
    }

    public int getTilePixelWidth(){
        return this.tilePixelWidth;
    }

    public int getTilePixelHeight(){
        return this.tilePixelHeight;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setCheckpoint(int x, int y) {
        this.checkpoint[0] = x;
        this.checkpoint[1] = y;
    }

    public void setFlagsPassed(int flagsPassed) {
        this.flagsPassed = flagsPassed;
    }

    public void setDirection(Direction dir){
        this.dir = dir;
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

    public void rotate_right() {
        if (this.getDirection() == Direction.WEST) {
            this.dir = Direction.NORTH;
        } else if (this.getDirection() == Direction.NORTH) {
            this.dir = Direction.EAST;
        } else if (this.getDirection() == Direction.EAST) {
            this.dir = Direction.SOUTH;
        } else if (this.getDirection() == Direction.SOUTH) {
            this.dir = Direction.WEST;
        }
        this.sprite.rotate(-90);
    }

    public void rotate_left() {
        if (this.getDirection() == Direction.NORTH) {
            this.dir = Direction.WEST;
        } else if (this.getDirection() == Direction.WEST) {
            this.dir = Direction.SOUTH;
        } else if (this.getDirection() == Direction.SOUTH) {
            this.dir = Direction.EAST;
        } else if (this.getDirection() == Direction.EAST) {
            this.dir = Direction.NORTH;
        }
        this.sprite.rotate(90);
    }

    public void moveForward(int amount) {
        Direction current_direction = this.getDirection();
        if (current_direction == Direction.NORTH) {
            this.setPosY(this.getPosY() + amount);
            this.sprite.setPosition(this.sprite.getX(), this.sprite.getY() + (amount * (this.tilePixelWidth / 6))); // temp moving sprite
        } else if (current_direction == Direction.EAST) {
            this.setPosX(this.getPosX() + amount);
            this.sprite.setPosition(this.sprite.getX() + (amount * (this.tilePixelWidth / 6)), this.sprite.getY());
        } else if (current_direction == Direction.SOUTH) {
            this.setPosY(this.getPosY() - amount);
            this.sprite.setPosition(this.sprite.getX(), this.sprite.getY() - (amount * (this.tilePixelWidth / 6)));
        } else if (current_direction == Direction.WEST) {
            this.setPosX(this.getPosX() - amount);
            this.sprite.setPosition(this.sprite.getX() - (amount * (this.tilePixelWidth / 6)), this.sprite.getY());
        } else {
            System.out.println("Something went terribly wrong");
        }
        if (gameMap.isHole(this.posX, this.posY)) {
            this.died();
        }
    }

    public void move(String move) { // gets the command from a card and figures out which command to execute
        //String command = card.getCardSprite().getTexture().toString();
        switch (move) {
            case "BACKUP":
                canMove(1, -1);
                break;
            case "MOVE1":
                canMove(1, 1);
                break;
            case "MOVE2":
                canMove(2, 1);
                break;
            case "MOVE3":
                canMove(3, 1);
                break;
            case "ROTATE90":
                this.rotate_right();
                break;
            case "ROTATE180":
                this.rotate_right();
                this.rotate_right();
                break;
            case "ROTATEC90":
                this.rotate_left();
                break;
            default:
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
            RoboRallyDemo.killMe(RoboRallyDemo.getID(), false);
        } else {
            // moves the sprite the appropriate amount in both x and y direction to the robots backup
            if (this.getPosX() <= this.getCheckpoint()[0] && this.getPosY() <= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[0] - this.getPosX())), this.sprite.getY() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[1] - this.getPosY())));
            } else if (this.getPosX() >= this.getCheckpoint()[0] && this.getPosY() <= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() - ((this.tilePixelWidth / 6) * (this.getPosX() - this.getCheckpoint()[0])), this.sprite.getY() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[1] - this.getPosY())));
            } else if (this.getPosX() <= this.getCheckpoint()[0] && this.getPosY() >= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() + ((this.tilePixelWidth / 6) * (this.getCheckpoint()[0] - this.getPosX())), this.sprite.getY() - ((this.tilePixelWidth / 6) * (this.getPosY() - this.getCheckpoint()[1])));
            } else if (this.getPosX() >= this.getCheckpoint()[0] && this.getPosY() >= this.getCheckpoint()[1]) {
                this.sprite.setPosition(this.sprite.getX() - ((this.tilePixelWidth / 6) * (this.getPosX() - this.getCheckpoint()[0])), this.sprite.getY() - ((this.tilePixelWidth / 6) * (this.getPosY() - this.getCheckpoint()[1])));
            } else {
                System.out.println("Should definitely not be possible");
            }
            if (this.dir == Direction.EAST) {
                this.rotate_left();
            } else if (this.dir == Direction.SOUTH) {
                this.rotate_right();
                this.rotate_right();
            } else if (this.dir == Direction.WEST) {
                this.rotate_right();
            }
            this.dir = Direction.NORTH;
            this.setPosX(this.getCheckpoint()[0]); //update internal numbers of robot location
            this.setPosY(this.getCheckpoint()[1]);
            this.initPowerdown = false;
            this.execPowerdown = false;
        }
    }

    public void takeDamage() {
        if (this.damage < 9) {
            this.damage += 1;
            cardHandler = RoboRallyDemo.getCardHandler();
            cardHandler.lockDown();
        } else {
            this.died();
        }
    }

    public boolean getExecPowerdown() {return execPowerdown;}

    public void setExecPowerdown(boolean execPowerdown) {this.execPowerdown = execPowerdown;}

    public Boolean getInitPowerdown() {
        return initPowerdown;
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

    public void setInitPowerdown(boolean initPowerdown) {this.initPowerdown = initPowerdown;}

    public void doPowerdown() {
        damage = 0;
        RoboRallyDemo.getCardHandler().setCardDelt(9);
        RoboRallyDemo.getCardHandler().powerdownCards();
        setExecPowerdown(false);
        setInitPowerdown(false);
    }




    public boolean playerCollidesWithPlayer(int amount) {
        for (int i = 0; i < RoboRallyDemo.getRobots().length; i++) {
            if (RoboRallyDemo.getRobots()[i] != null) {
                if (i != RoboRallyDemo.getID()) {
                    if ((this.dir == Direction.NORTH && amount == 1) || (this.dir == Direction.SOUTH && amount == -1)) {
                        if (amount == 1) {
                            if (this.posY + amount == RoboRallyDemo.getRobots()[i].getPosY() && this.posX == RoboRallyDemo.getRobots()[i].getPosX()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                        if (amount == -1) {
                            if (this.posY - amount == RoboRallyDemo.getRobots()[i].getPosY() && this.posX == RoboRallyDemo.getRobots()[i].getPosX()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                    }
                    if ((this.dir == Direction.WEST && amount == 1) || (this.dir == Direction.EAST && amount == 1)) {
                        if (amount == 1) {
                            if (this.posX + amount == RoboRallyDemo.getRobots()[i].getPosX() && this.posY == RoboRallyDemo.getRobots()[i].getPosY()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                        if (amount == -1) {
                            if (this.posX - amount == RoboRallyDemo.getRobots()[i].getPosX() && this.posY == RoboRallyDemo.getRobots()[i].getPosY()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                    }
                    if ((this.dir == Direction.SOUTH && amount == 1) || (this.dir == Direction.NORTH && amount == -1)) {
                        if (amount == 1) {
                            if (this.posY - amount == RoboRallyDemo.getRobots()[i].getPosY() && this.posX == RoboRallyDemo.getRobots()[i].getPosX()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                        if (amount == -1) {
                            if (this.posY + amount == RoboRallyDemo.getRobots()[i].getPosY() && this.posX == RoboRallyDemo.getRobots()[i].getPosX()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                    }
                    if ((this.dir == Direction.WEST && amount == 1) || (this.dir == Direction.EAST && amount == -1)) {
                        if (amount == 1) {
                            if (this.posX - amount == RoboRallyDemo.getRobots()[i].getPosX() && this.posY == RoboRallyDemo.getRobots()[i].getPosY()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                        if (amount == -1) {
                            if (this.posX + amount == RoboRallyDemo.getRobots()[i].getPosX() && this.posY == RoboRallyDemo.getRobots()[i].getPosY()) {
                                indexToBePushed = i;
                                dirToBePushed = dir;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Amount: 1 means move forward, -1 move backwards
    public int checkNext(int amount) {
        if (this.dir == Direction.NORTH && (this.posY + amount == 12 || this.posY + amount == -1)) {
            return -1;
        }
        if (this.dir == Direction.EAST && (this.posX + amount == 12 || this.posX + amount == -1)) {
            return -1;
        }
        if (this.dir == Direction.SOUTH && (this.posY - amount == -1 || this.posY - amount == 12)) {
            return -1;
        }
        if (this.dir == Direction.WEST && (this.posX - amount == -1 || this.posX - amount == 12)) {
            return -1;
        }
        if (gameMap.wallNearby(this.dir, this.posX, this.posY, amount)) {
            return 0;
        }

        // If the move in a direction gives this robot and another robot the same location, then the other robot will be pushed in that direction for the same distance
        // Single player first:

        else if (!RoboRallyDemo.getSinglePlayerMode()) {
            if (playerCollidesWithPlayer(amount)) {
                if (canPush(this.dir, amount)) {
                    return 3;
                }
                else {
                    return 0;
                }
            }
        }
        else {
            return 1;
        }
        return 1;
    }

    public boolean canPush(Direction dir, int amount) {
        if (!RoboRallyDemo.getSinglePlayerMode()) {
            if (dir == Direction.NORTH) {
                if ((this.posY == 10 && amount > 0) || (this.posY == 1 && amount < 0) ||
                        (gameMap.isHole(RoboRallyDemo.getRobots()[indexToBePushed].posX, RoboRallyDemo.getRobots()[indexToBePushed].posY + amount))) {
                    willDie = true;
                    return true;
                }
                if (gameMap.wallNearby(Direction.NORTH, this.posX, this.posY + amount, amount)) {
                    return false;
                }
            }
            if (dir == Direction.EAST) {
                if ((this.posX == 10 && amount > 0) || (this.posX == 1 && amount < 0) ||
                        (gameMap.isHole(RoboRallyDemo.getRobots()[indexToBePushed].posX + amount, RoboRallyDemo.getRobots()[indexToBePushed].posY))) {
                    willDie = true;
                    return true;
                }
                if (gameMap.wallNearby(Direction.EAST, this.posX + amount, this.posY, amount)) {
                    return false;
                }
            }
            if (dir == Direction.SOUTH) {
                if ((this.posY == 10 && amount < 0) || (this.posY == 1 && amount > 0) ||
                        (gameMap.isHole(RoboRallyDemo.getRobots()[indexToBePushed].posX, RoboRallyDemo.getRobots()[indexToBePushed].posY - amount))) {
                    willDie = true;
                    return true;
                }
                if (gameMap.wallNearby(Direction.SOUTH, this.posX, this.posY - amount, amount)) {
                    return false;
                }
            }
            if (dir == Direction.WEST) {
                if ((this.posX == 10 && amount < 0) || (this.posX == 1 && amount > 0) ||
                        (gameMap.isHole(RoboRallyDemo.getRobots()[indexToBePushed].posX - amount, RoboRallyDemo.getRobots()[indexToBePushed].posY))) {
                    willDie = true;
                    return true;
                }
                if (gameMap.wallNearby(Direction.WEST, this.posX - amount, this.posY, amount)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void pushRobot(Direction dir, int amount) {
        if (!RoboRallyDemo.getSinglePlayerMode()) {
            if (dir == Direction.NORTH) {
                if (willDie) {
                    RoboRallyDemo.getRobots()[indexToBePushed].died();
                } else {
                    RoboRallyDemo.getRobots()[indexToBePushed].setPosY(RoboRallyDemo.getRobots()[indexToBePushed].getPosY() + amount);
                    updateMeOnline(RoboRallyDemo.getRobots()[indexToBePushed], amount, Direction.NORTH);
                }
            }
            if (dir == Direction.EAST) {
                if (willDie) {
                    RoboRallyDemo.getRobots()[indexToBePushed].died();
                } else {
                    RoboRallyDemo.getRobots()[indexToBePushed].setPosY(RoboRallyDemo.getRobots()[indexToBePushed].getPosX() + amount);
                    updateMeOnline(RoboRallyDemo.getRobots()[indexToBePushed], amount, Direction.EAST);
                }
            }
            if (dir == Direction.SOUTH) {
                if (willDie) {
                    RoboRallyDemo.getRobots()[indexToBePushed].died();
                } else {
                    RoboRallyDemo.getRobots()[indexToBePushed].setPosY(RoboRallyDemo.getRobots()[indexToBePushed].getPosY() - amount);
                    updateMeOnline(RoboRallyDemo.getRobots()[indexToBePushed], amount, Direction.SOUTH);
                }
            }
            if (dir == Direction.WEST) {
                if (willDie) {
                    RoboRallyDemo.getRobots()[indexToBePushed].died();
                } else {
                    RoboRallyDemo.getRobots()[indexToBePushed].setPosX(RoboRallyDemo.getRobots()[indexToBePushed].getPosX() - amount);
                    updateMeOnline(RoboRallyDemo.getRobots()[indexToBePushed], amount, Direction.EAST);
                }
            }
        }
    }

    private void updateMeOnline(Robot robot, int amount, Direction dir) {
        if (!RoboRallyDemo.getSinglePlayerMode()) {
            if (dir == Direction.NORTH) {
                robot.sprite.setPosition(RoboRallyDemo.getRobots()[indexToBePushed].sprite.getX(), robot.sprite.getY() + (amount * (tilePixelWidth / 6)));
            } else if (dir == Direction.EAST) {
                robot.sprite.setPosition(RoboRallyDemo.getRobots()[indexToBePushed].sprite.getX() + (amount * (tilePixelWidth / 6)), robot.sprite.getY());
            } else if (dir == Direction.SOUTH) {
                robot.sprite.setPosition(RoboRallyDemo.getRobots()[indexToBePushed].sprite.getX(), robot.sprite.getY() - (amount * (tilePixelWidth / 6)));
            } else if (dir == Direction.WEST) {
                robot.sprite.setPosition(RoboRallyDemo.getRobots()[indexToBePushed].sprite.getX() - (amount * (tilePixelWidth / 6)), robot.sprite.getY());
            }
        }
    }


    public void canMove(int loops, int amount) {
        for (int i = 0; i < loops; i++) {
            if (this.checkNext(amount) == 1) {
                this.moveForward(amount);
            } else if (this.checkNext(amount) == -1) {
                this.died();
                break;
            } else if (this.checkNext(amount) == 0) {
                break;
            } else if (this.checkNext(amount) == 3) {
                pushRobot(dirToBePushed, amount);
                this.moveForward(amount);
            } else {
                break;
            }
        }
    }
}
