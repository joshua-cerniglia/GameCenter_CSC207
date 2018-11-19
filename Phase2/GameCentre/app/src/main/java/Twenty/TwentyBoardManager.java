package Twenty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import fall2018.csc2017.CoreClasses.BoardManager;
import phase1.AccountManager;
import phase1.SlidingGameFile;

public class TwentyBoardManager extends BoardManager {


    /**
     * The board being managed.
     */
    protected TwentyBoard twentyBoard;

    /**
     * The 2048 Game File holding the data for this board.
     */
    private SlidingGameFile gameFile;

    public TwentyBoardManager(int size) {

        // Initialize the board as all blank tiles.
        List<TwentyTile> tiles = new ArrayList<>();
        final int numTiles = size * size;
        for (int tileNum = 0; tileNum != numTiles; tileNum++) {
            tiles.add(new TwentyTile(0, 0));
        }

        // Create the board, and specify number of rows, columns
        this.twentyBoard = new TwentyBoard(tiles, size, size);

        // Create a new GameFile, and initialize it with this shuffled board.
        SlidingGameFile gameFile = new SlidingGameFile(this.twentyBoard, Instant.now().toString());

        // Add this new GameFile to the current active account's list of GameFiles.
        AccountManager.activeAccount.addGameFile(gameFile);
        this.gameFile = gameFile;
        this.gameStates = this.gameFile.getGameStates();
        this.numMoves = gameFile.numMoves;
        this.maxUndos = gameFile.maxUndos;
    }

    public void touchMove(){

    }

    public boolean isValidMove(char dir){
        return (TwentyBoard)(this.board).isCollapsable(dir);
    }
}