package Checkers;

import java.time.Instant;
import java.util.Iterator;

import fall2018.csc2017.CoreClasses.Board;
import fall2018.csc2017.CoreClasses.BoardManager;
import fall2018.csc2017.CoreClasses.Tile;
import phase1.GameFile;

public class CheckersBoardManager extends BoardManager {

    /**
     * The board being managed.
     */
    protected static CheckersBoard board;

    /**
     * The SlidingGameFile holding the data for this board.
     */
    private CheckersGameFile gameFile;

    /**
     * Color of the player who won the game.
     */
    private String winner;

    /**
     * Whose turn it is
     */
    private boolean redsTurn;

    /**
     * Which AI the opponent is or human if you're playing a human
     */
    private String opponentType;

    /**
     * Indicates whether a piece has been slain on the current turn yet
     */
    private boolean hasSlain = false;


    /**
     * Initialize the data of this game given a GameFile, containing a Stack of Boards
     * (each representing a specific 'game state'), and attributes telling of the game's settings.
     *
     * @param gameFile: Represents a record of data for this game.
     */
    public CheckersBoardManager(CheckersGameFile gameFile) {
        super(gameFile);
        this.gameFile = gameFile;
        this.gameStates = gameFile.getGameStates();
        this.remainingUndos = gameFile.remainingUndos;
        this.remainingUndos = gameFile.maxUndos;
        //AccountManager.activeAccount.setActiveGameFile(gameFile);
        if (!gameFile.getGameStates().isEmpty()) {
            this.board = (CheckersBoard) gameFile.getGameStates().peek();
        }
    }

    /**
     * Manage a newly set uo checker board.
     *
     * @param size: The desired size of the board.
     */
    CheckersBoardManager(int size, boolean redsTurn) {
        super(size);
        CheckersTile[][] tiles = new CheckersTile[size][size];
        String id;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ((row + col) % 2 == 0) {
                    id = CheckersTile.BLACK_TILE;
                } else if (row < (size / 2 - 1)) {
                    id = CheckersTile.WHITE_PAWN;
                } else if (row > (size / 2)) {
                    id = CheckersTile.RED_PAWN;
                } else {
                    id = CheckersTile.EMPTY_WHITE_TILE;
                }
                tiles[row][col] = new CheckersTile(id);
            }
        }
        this.redsTurn = redsTurn;
        this.board = new CheckersBoard(tiles, size);

        // Create a new GameFile, and initialize it with this shuffled board.
        CheckersGameFile gameFile = new CheckersGameFile(this.board, Instant.now().toString());

        // Add this new GameFile to the current active account's list of GameFiles.
        //AccountManager.activeAccount.addGameFile(gameFile);
        this.gameFile = gameFile;
        this.gameStates = this.gameFile.getGameStates();
        this.maxUndos = gameFile.maxUndos;
        save(this.board);
    }

    /**
     * Highlights Tile is if the selected tile belongs to the right player
     *
     * @param position position of the tile
     */
    boolean isValidSelect(int position) {
        if (hasSlain){return false;} // if it has slain, you can't select anything else
        int row = position / board.getNumRows();
        int col = position % board.getNumCols();
        CheckersTile selectedTile = board.getCheckersTile(row, col);
        String tileId = selectedTile.getCheckersId();

        if ((redsTurn && tileId.contains("red")) || (!redsTurn && tileId.contains("white"))) {
            board.setHighLightedTile(row, col);
            setChanged();
            notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * Checks if the move is valid. Note in this version we don't force you to take a piece.
     *
     * @param position position of the target tile
     * @return true if and only if the move is allowed in Checkers
     */
    boolean isValidMove(int position) {
        if (position >= board.getNumRows() *board.getNumCols()){return false;}
        if (position < 0){return false;}
        CheckersTile highLightedTile = board.getHighLightedTile();
        String highId = highLightedTile.getCheckersId();
        int highRow = board.getHighLightedTilePosition()[0];
        int highCol = board.getHighLightedTilePosition()[1];
        int targetRow = position / board.getNumRows();
        int targetCol = position % board.getNumCols();
        CheckersTile targetTile = board.getCheckersTile(targetRow, targetCol);
        if (!isCorrectDirection(highId, highRow, targetRow)) {
            return false;
        }
        if (Math.abs(highRow - targetRow) == 1
                && targetTile.getCheckersId().equals(CheckersTile.EMPTY_WHITE_TILE)) {
            return Math.abs(targetCol - highCol) == 1;
        }
        if (Math.abs(highRow - targetRow) == 2
                && targetTile.getCheckersId().equals(CheckersTile.EMPTY_WHITE_TILE)
                && Math.abs(targetCol - highCol) == 2) {
            CheckersTile middleTile = board.getCheckersTile((targetRow + highRow) / 2, (targetCol + highCol) / 2);
            if (middleTile.getCheckersId().contains(CheckersTile.RED) && !redsTurn) {
                return true;
            } else return middleTile.getCheckersId().contains(CheckersTile.WHITE) && redsTurn;
        }
        return false;
    }

    /**
     * Checks if player is trying to move a Checkers piece in the right direction
     *
     * @param tileId    id of the selected tile
     * @param sourceRow starting row of the selected tile
     * @param targetRow destination row of the selected tile
     * @return true if the Checkers piece is moving in the right direction and false otherwise
     */
    private boolean isCorrectDirection(String tileId, int sourceRow, int targetRow) {
        if (tileId.contains(CheckersTile.RED)
                && !tileId.contains(CheckersTile.KING)) {
            return sourceRow > targetRow;
        }

        if (tileId.contains(CheckersTile.WHITE)
                && !tileId.contains(CheckersTile.KING)) {
            return sourceRow < targetRow;
        }
        return true;
    }

    /**
     * Moves the selected piece to a the target position. Potentially takes an opponent piece in the process.
     *
     * @param position: The position the selected piece is moved to.
     */
    void touchMove(int position) {
        CheckersBoard newBoard = board.createDeepCopy();
        this.board = newBoard;
        int highRow = board.getHighLightedTilePosition()[0];
        int highCol = board.getHighLightedTilePosition()[1];
        int row = position / board.getNumRows();
        int col = position % board.getNumCols();
        board.swapTiles(highRow, highCol, row, col);
        if (Math.abs(highCol-col)==2){
            board.setHighLightedTile(row, col);
            hasSlain = true;}
        if (hasSlain && stillHasMoves(row, col)){
            board.setHighLightedTile(row, col);
        }
        else {
            hasSlain = false;
            swapRedsTurn();
        }
        gameFile.addUndos();
        save(newBoard);
        setChanged();
        notifyObservers();

    }
    /**
     * Check if a piece that has just slain another can slay again!
     * @param sourceRow row of the piece in question
     * @param sourceColumn column of the piece in question
     * @return true if and only if the piece that has just slain can slay again!!
     */
    private boolean stillHasMoves(int sourceRow, int sourceColumn){
        int[][] jumps = {{sourceRow +2, sourceColumn +2}, {sourceRow -2 , sourceColumn + 2 },
                {sourceRow +2, sourceColumn -2},{sourceRow-2,sourceColumn-2} };
        for (int[] move:jumps){
            if (isValidMove(move[0]*board.getNumRows() + move[1])){
                    return true;}}
        board.getHighLightedTile().dehighlight();
        return false;
    }


    //should use an iterator
    public boolean gameComplete() {
        boolean redWins = true;
        boolean whiteWins = true;
        Iterator<Tile> iter = board.iterator();
        while (iter.hasNext()) {
            CheckersTile checkersTile = (CheckersTile) iter.next();
            redWins = redWins && !checkersTile.getCheckersId().contains(CheckersTile.WHITE);
            whiteWins = whiteWins && !checkersTile.getCheckersId().contains(CheckersTile.RED);
            if (!redWins && !whiteWins) {
                return false;
            }
        }
        if (redWins) {
            winner = "Red";
        } else {
            winner = "White";
        }
        return true;
    }

    String getWinner() {
        return winner;
    }

    boolean isRedsTurn() {
        return redsTurn;
    }

    void swapRedsTurn() {
        redsTurn = !redsTurn;
    }

    boolean isHasSlain() {
        return hasSlain;
    }

    static CheckersBoard getBoard() {
        return board;
    }

    /**
     * Returns the GameFile managed by this SlidingBoardManager.
     */
    public GameFile getGameFile() {
        return this.gameFile;
    }

    /**
     * Saves a new state of board to game.
     *
     * @param board a board
     */
    @SuppressWarnings("unchecked")
    public void save(CheckersBoard board) {
        super.save(board);
//        CheckersGameFile newGameFile = (CheckersGameFile) AccountManager.activeAccount.getActiveGameFile();
//        newGameFile.getGameStates().push(board);
//        AccountManager.activeAccount.addGameFile(newGameFile);
//        AccountManager.activeAccount.saveAccountGameData();
//        this.gameFile = newGameFile;
        this.gameStates = this.gameFile.getGameStates();
        this.board = board;
    }

    /**
     * Switches the board back one move, if the user has undos left
     */
    @Override
    public Board undo() {
        if (this.remainingUndos > 0) {
            this.board = (CheckersBoard) super.undo();
        }
        this.board.getHighLightedTile().dehighlight();
        setChanged();
        notifyObservers();
        swapRedsTurn();
        return this.board;
    }

    /**
     * @param maxUndoValue: Maximum number of undo tries for this file.
     *                      Also initializes the number of undo's this file currently has (denoted by <remainingUndos>)
     */
    void setMaxUndos(int maxUndoValue) {
        this.gameFile.setMaxUndos(maxUndoValue);
        this.gameFile.setRemainingUndos(0);
        this.maxUndos = maxUndoValue;
        this.remainingUndos = 0;
    }

    void setOpponentType(String opponentType) {
        this.opponentType = opponentType;
    }

    String getOpponentType() {
        return this.opponentType;
    }
}