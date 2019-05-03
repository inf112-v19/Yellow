package inf112.skeleton.app;

import Grid.Direction;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class RobotTest {

    int posX = 0;
    int posY = 0;
    int[] startpos;
    IRobot robot;
    Random random = new Random();
    int randInt;
    boolean randBool;
    Direction randDir;


    @Test
    public void createRobotTest() {
        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        assertEquals(robot.getCheckpoint(), startpos);
        assertEquals(robot.getPosX(), 0);
        assertEquals(robot.getPosY(), 0);
        assertEquals(robot.getDirection(), Direction.NORTH);
        assertEquals(robot.getDamage(), 0);
        assertEquals(robot.getLives(), 3);
        assertEquals(robot.getFlagsPassed(), 0);
        assertEquals(robot.getAlive(), true);
        assertEquals(robot.getSprite(), null);
        assertEquals(robot.getTilePixelWidth(), 10);
        assertEquals(robot.getTilePixelHeight(), 10);

    }


    @Test
    public void robotGettersSettersTest(){

        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        for( int i = 0; i < 10; i++){
            randInt = random.nextInt(++i);

            robot.setDamage(randInt);
            assertEquals(robot.getDamage(), randInt);
            robot.setPosX(randInt);
            assertEquals(robot.getPosX(), randInt);
            robot.setPosY(randInt);
            assertEquals(robot.getPosX(), randInt);
            robot.setCheckpoint(randInt, randInt);
            assertEquals(robot.getCheckpoint()[0], randInt);
            assertEquals(robot.getCheckpoint()[1], randInt);

            randBool = random.nextBoolean();

            robot.setAlive(randBool);
            assertEquals(robot.getAlive(), randBool);

            randDir = Direction.getRandomDir();

            robot.setDirection(randDir);
            assertEquals(robot.getDirection(), randDir);

        }
    }

    @Test
    public void damageTest(){

        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        assertEquals(robot.getDamage(), 0);

        for(int i = 0; i < 10; i++){
            robot.setDamage(0);
            randInt = random.nextInt(9);
            for(int j = 0; j < randInt; j++){
                robot.takeDamage();
            }
            assertEquals(robot.getDamage(), randInt);
        }
    }

    @Test
    public void damageTest2(){

        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        assertEquals(robot.getDamage(), 0);

        for(int i = 0; i < 2; i++){
            robot.setDamage(0);
            //randInt = random.nextInt(9) + 10;
            randInt = random.nextInt(7);
            for(int j = 0; j < randInt + 10; j++){
                robot.takeDamage();
            }
            assertEquals(robot.getDamage(), randInt + 2);
        }
    }

    @Test
    public void damageTest3(){
        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        assertEquals(robot.getDamage(), 0);

        for(int i = 0; i < 1; i++){
            robot.setDamage(0);
            //randInt = random.nextInt(9) + 10;
            randInt = random.nextInt(7);
            for(int j = 0; j < randInt + 18; j++){
                robot.takeDamage();
            }
            assertEquals(robot.getDamage(), randInt + 2);
        }
    }


    @Test
    public void damageLivesTest(){

        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        assertEquals(robot.getDamage(), 0);
        assertEquals(robot.getLives(), 3);

        for(int i = 0; i < 10; i++){
            robot.setDamage(0);
            randInt = random.nextInt(9);
            for(int j = 0; j < randInt; j++){
                robot.takeDamage();
            }
            assertEquals(robot.getLives(), 3);
        }
    }

    @Test
    public void damageLivesTest2(){

        startpos = new int[]{Math.round(posX), Math.round(posY)};
        robot = new TestBot(startpos);

        assertEquals(robot.getDamage(), 0);
        assertEquals(robot.getLives(), 3);

        for(int i = 2; i > 0; i--){
            robot.setDamage(0);
            randInt = random.nextInt(7);
            for(int j = 0; j < randInt + 10; j++){
                robot.takeDamage();
            }
            assertEquals(robot.getLives(), 2);
        }
    }
}
