package Checkers;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class CheckersBoardManagerTest {

    private CheckersBoardManager testCheckersBoardManager;

    private final int size = 8;


    /**
     * Setup with a blank board
     */
    @Before
    public void setup() {
        testCheckersBoardManager = new CheckersBoardManager(size, true);
        CheckersBoard board;
        CheckersTile[][] tiles = new CheckersTile[size][size];
        String id;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ((row + col) % 2 == 0) {
                    id = CheckersTile.BLACK_TILE;
                } else {
                    id = CheckersTile.EMPTY_WHITE_TILE;
                }
                tiles[row][col] = new CheckersTile(id);
            }
        }
        testCheckersBoardManager.board = new CheckersBoard(tiles, size);
    }

    @Test
    public void testIsValidSelect() {
        assertFalse(testCheckersBoardManager.isValidSelect(1));

        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 0, 1, CheckersTile.RED_KING);
        assertTrue(testCheckersBoardManager.isValidSelect(1));

        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 0, 3, CheckersTile.WHITE_PAWN);
        assertFalse(testCheckersBoardManager.isValidSelect(3));
    }

    /**
     * Confirm that a tile can jump an enemy piece
     */
    @Test
    public void testIsValidMoveCanJumpEnemy() {
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 1, 2, CheckersTile.WHITE_PAWN);
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 2, 3, CheckersTile.RED_PAWN);
        testCheckersBoardManager.isValidSelect(2 * size + 3);
        assertTrue(testCheckersBoardManager.isValidMove(1));
    }

    @Test
    public void testIsValidMoveCantJumpFriend() {
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 1, 2, CheckersTile.RED_PAWN);
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 2, 3, CheckersTile.RED_PAWN);
        testCheckersBoardManager.isValidSelect(2 * size + 3);
        assertFalse(testCheckersBoardManager.isValidMove(1));
    }

    @Test
    public void testIsValidMovePawnSteps() {
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 1, 2, CheckersTile.RED_PAWN);
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 0, 3, CheckersTile.WHITE_PAWN);
        testCheckersBoardManager.isValidSelect(size + 2);
        assertFalse(testCheckersBoardManager.isValidMove(2 * size + 1));
        assertTrue(testCheckersBoardManager.isValidMove(1));

        testCheckersBoardManager.swapRedsTurn();
        testCheckersBoardManager.isValidSelect(3);
        assertFalse(testCheckersBoardManager.isValidMove(4));
        assertTrue(testCheckersBoardManager.isValidMove(size + 4));
    }

    @Test
    public void testTouchMoveSlayingEnemy() {
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 2, 3, CheckersTile.RED_PAWN);
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 1, 2, CheckersTile.WHITE_PAWN);
        testCheckersBoardManager.isValidSelect(size * 2 + 3);
        testCheckersBoardManager.touchMove(1);
        assertTrue(testCheckersBoardManager.board.getCheckersTile(
                2, 3).getCheckersId().equals(CheckersTile.EMPTY_WHITE_TILE));
        assertTrue(testCheckersBoardManager.board.getCheckersTile(
                1, 2).getCheckersId().equals(CheckersTile.EMPTY_WHITE_TILE));

    }

    @Test
    public void testTouchMoveStepping() {
        CheckersBoardTest.addPiece(
                testCheckersBoardManager.board, 1, 2, CheckersTile.RED_PAWN);
        testCheckersBoardManager.isValidSelect(size + 2);
        testCheckersBoardManager.touchMove(1);
        assertTrue(testCheckersBoardManager.board.getCheckersTile(0, 1).getCheckersId().equals(CheckersTile.RED_KING));
        testCheckersBoardManager.swapRedsTurn();
        testCheckersBoardManager.isValidSelect(1);
        assertFalse(testCheckersBoardManager.isValidMove(0));
        assertFalse(testCheckersBoardManager.isValidMove(22));
        assertFalse(testCheckersBoardManager.isValidMove(size + 1));
        assertTrue(testCheckersBoardManager.isValidMove(size));
    }

    @Test
    public void testGameComplete() {
        CheckersBoardTest.addPiece(testCheckersBoardManager.board, 0, 1, CheckersTile.WHITE_PAWN);
        assertTrue(testCheckersBoardManager.gameComplete());
    }

    @Test
    public void getWinner() {
    }


    @Test
    public void testUndo() {
        CheckersBoardTest.addPiece(testCheckersBoardManager.board, size-1, 0, CheckersTile.RED_PAWN);
        testCheckersBoardManager.setMaxUndos(1);
        testCheckersBoardManager.isValidSelect((size-1)*size);
        testCheckersBoardManager.touchMove((size-2)*size+1);
        testCheckersBoardManager.isValidSelect((size-2)*size+1);
        testCheckersBoardManager.touchMove((size-3)*size);

    }

}