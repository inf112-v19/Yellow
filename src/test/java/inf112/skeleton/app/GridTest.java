package inf112.skeleton.app;

import Grid.Direction;
import Grid.IGrid;
import Grid.MyGrid;
import map.GameMap;
import map.IGameMap;
import map.MapTile;
import map.MovePlayerException;
import org.junit.Test;

import static org.junit.Assert.*;

public class GridTest {

    //creating a grid, and sets an element to a location on that grid, checks if the get method returns same element
    @Test
    public void gridSetGetTest(){
        IGrid grid = new MyGrid(12,12, null);
        grid.set(1,1, MapTile.PLAYER);
        assertEquals(MapTile.PLAYER, grid.get(1,1));
    }

    @Test
    public void mapGetCellTest(){
        IGrid grid = new MyGrid(12,12, MapTile.OPEN);
        grid.set(2, 2, MapTile.PLAYER);
        IGameMap map = new GameMap(grid);
        assertEquals(map.getCell(2,2),grid.get(2,2));
    }

    //checks that moveplayer method fails when moving into a wall
    @Test
    public void mapBadMoveTest(){
        IGrid grid = new MyGrid(12,12,MapTile.OPEN);
        //sets player position
        grid.set(1,1,MapTile.PLAYER);

        //sets walls where player can not move
        grid.set(1,2,MapTile.WALL);
        grid.set(1,0,MapTile.WALL);
        grid.set(2,1,MapTile.WALL);
        grid.set(0,1,MapTile.WALL);

        //sets up map
        IGameMap map = new GameMap(grid);

        //assert that you cannot move in this direction
        assertFalse(map.playerCanGo(Direction.NORTH));
        assertFalse(map.playerCanGo(Direction.SOUTH));
        assertFalse(map.playerCanGo(Direction.WEST));
        assertFalse(map.playerCanGo(Direction.EAST));

        try {
            map.movePlayer(Direction.NORTH);
            fail("Moving north should fail");
        } catch (MovePlayerException e) {
            assertTrue(true);
        }

        try {
            map.movePlayer(Direction.SOUTH);
            fail("Moving south should fail");
        } catch (MovePlayerException e) {
            assertTrue(true);
        }

        try {
            map.movePlayer(Direction.EAST);
            fail("Moving east should fail");
        } catch (MovePlayerException e) {
            assertTrue(true);
        }

        try {
            map.movePlayer(Direction.WEST);
            fail("Moving west should fail");
        } catch (MovePlayerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void mapGoodMoveTest() throws MovePlayerException{
        IGrid grid = new MyGrid(12,12, MapTile.OPEN);
        //sets player position
        grid.set(2,3,MapTile.PLAYER);

        //sets up map
        IGameMap map = new GameMap(grid);

        //should both succeed
        map.movePlayer(Direction.NORTH);
        assertEquals(MapTile.PLAYER, map.getCell(2, 4));
        assertEquals(MapTile.OPEN, map.getCell(2, 3 ));
        map.movePlayer(Direction.NORTH);
        assertEquals(MapTile.PLAYER, map.getCell(2, 5));
        assertEquals(MapTile.OPEN, map.getCell(2, 4));

    }

    @Test
    public void mapGoodMoveTest2() throws MovePlayerException{
        IGrid grid = new MyGrid(25,25, MapTile.OPEN);
        //sets player position
        int x = 12;
        int y = 12;
        grid.set(x, y, MapTile.PLAYER);

        //sets up map
        IGameMap map = new GameMap(grid);

        for(int i = 0; i < 13; i++){
            Direction dir = Direction.getRandomDir();
            map.movePlayer(dir);

            if(dir == Direction.NORTH) {
                y++;
            }
            else if(dir == Direction.WEST) {
                x--;
            }
            else if(dir == Direction.SOUTH) {
                y--;
            }
            else if(dir == Direction.EAST) {
                x++;
            }

            assertEquals(MapTile.PLAYER, map.getCell(x, y));

        }
    }
}