package Twenty;

import org.junit.Before;
import org.junit.Test;
import phase1.Account;
import phase1.AccountManager;
import phase1.Game;

import static org.junit.Assert.assertEquals;

public class TwentyBoardManagerTest {

    private TwentyBoardManager twentyBoardManager;
    private TwentyTile tile1, tile2, tile3;

    @Before
    public void setup(){
        AccountManager.activeAccount = new Account("sid", "1234");
        AccountManager.activeAccount.setActiveGameName(Game.TWENTY_NAME);
        twentyBoardManager = new TwentyBoardManager(3);
    }

    @Test
    public void testIsValidMoveBlank(){
        //  Initialize the board as all blank tiles, and test if a valid move can be made.
        assertEquals("twentyBoardManager.isValidMove() failed test 1.", true, twentyBoardManager.isValidMove(true));
        assertEquals("twentyBoardManager.isValidMove() failed test 2.", true, twentyBoardManager.isValidMove(false));
    }

    @Test
    public void testIsValidMoveFilled(){
        // Fill the board such that it's 'complete' in the horizontal direction, and check
        // the value of isValidMove.
        TwentyBoard twentyBoard = twentyBoardManager.twentyBoard;
        tile1 = new TwentyTile(2, 2);
        tile2 = new TwentyTile(4, 2);
        tile3 = new TwentyTile(8, 2);
        for(int row = 0; row < 3; row++){
            twentyBoard.insertTile(row,0, tile1);
            twentyBoard.insertTile(row,1, tile2);
            twentyBoard.insertTile(row,2, tile3);
        }
        assertEquals("twentyBoard.isValidMove() failed test 3.", false, twentyBoardManager.isValidMove(true));
    }

    @Test
    public void testTouchMoveInvalid(){
        // Fill the board such that it's 'complete' in the horizontal direction, and check
        // if a move is made by touchMove
        TwentyBoard twentyBoard = twentyBoardManager.twentyBoard;
        tile1 = new TwentyTile(2, 2);
        tile2 = new TwentyTile(4, 2);
        tile3 = new TwentyTile(8, 2);
        for(int row = 0; row < 3; row++){
            twentyBoard.insertTile(row,0, tile1);
            twentyBoard.insertTile(row,1, tile2);
            twentyBoard.insertTile(row,2, tile3);
        }

        twentyBoardManager.touchMove('L');
        assertEquals("twentyBoard.touchMove() failed test 1.", false, twentyBoard.isCollapsable(true));
    }

    @Test
    public void testTouchMove(){
        // TODO: Perhaps write this in a cleaner way.
        // Fill the board such that a tile can be shifted leftwards, and see if it does get
        // shifted leftwards after touchMove()
        TwentyBoard twentyBoard = twentyBoardManager.twentyBoard;
        tile1 = new TwentyTile(2, 2);
        twentyBoard.insertTile(0,2,tile1);
        twentyBoardManager.touchMove('L');
        assertEquals("twentyBoard.touchMove() failed test 1.", 2, twentyBoard.getTile(0,0).getId());

        // Test on a rightwards movement
        twentyBoardManager = new TwentyBoardManager(3);
        twentyBoard = twentyBoardManager.twentyBoard;
        tile1 = new TwentyTile(2, 2);
        twentyBoard.insertTile(0,0,tile1);
        twentyBoardManager.touchMove('R');
        assertEquals("twentyBoard.touchMove() failed test 2.", 2, twentyBoard.getTile(0,2).getId());

        // Test on a downwards movement
        twentyBoardManager = new TwentyBoardManager(3);
        twentyBoard = twentyBoardManager.twentyBoard;
        tile1 = new TwentyTile(2, 2);
        twentyBoard.insertTile(0,2,tile1);
        twentyBoardManager.touchMove('D');
        assertEquals("twentyBoard.touchMove() failed test 3.", 2, twentyBoard.getTile(2,2).getId());

        // Test on an upwards movement
        twentyBoardManager = new TwentyBoardManager(3);
        twentyBoard = twentyBoardManager.twentyBoard;
        tile1 = new TwentyTile(2, 2);
        twentyBoard.insertTile(2,0,tile1);
        twentyBoardManager.touchMove('U');
        assertEquals("twentyBoard.touchMove() failed test 4.", 2, twentyBoard.getTile(0,0).getId());
    }



}
