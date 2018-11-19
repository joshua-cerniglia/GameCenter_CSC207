package fall2018.csc2017.slidingtiles;
import java.util.ArrayList;

public class TwentyBoard extends Board {

    /*
     * @param row1 the first tile row
     * @param col1 the first tile col
     * @param row2 the second tile row
     * @param col2 the second tile col
     * preconditions: the tile at row1, col1 has the same id as the tile at row2, col2
     */
    public TwentyTile mergeTiles(int row1, int col1, int row2, int col2){
        int tile1Id = this.tiles[row1][col1].id, tile2Id = this.tiles[row1][col1].id;

        // Verify preconditions.
        if(tile1Id == tile2Id){
            // TODO: Insert a real background image
            return new TwentyTile(tile1Id*tile2Id, tile1Id*tile2Id);
        }else{
            // Return null if preconditions are not met.
            return null;
        }
    }

    /* Generate a random tile in place of an empty tile in the Board. */
    public void generateRandomTile(){
        int position[] = getRanEmptyPos();
        int tileRow = position[0], tileCol = position[1];

        // Pick a random value from (2^1, 2^2, 2^3)
        int ranExp = (int)(Math.random() * 4 + 1);
        int ranNum = (int)(Math.pow(2, ranExp));

        // TODO: Insert a background image instead of ranNum
        TwentyTile randomTile = new TwentyTile(ranNum, ranNum);

        this.tiles[tileRow][tileCol] = randomTile;

    }

    /* Get the positions of a random empty tile in this Board. */
    private int[] getRanEmptyPos(){
        int[] ranEmptyPos = new int[2];
        ArrayList<int[]> emptyPositions = getAllEmptyPos();
        if(emptyPositions.isEmpty()){
            // There are no empty tiles. Having a position of '-1' will denote that no empty tile exists.
            ranEmptyPos[0] = -1;
        }else{
            int ranIndex = (int)(Math.random() * emptyPositions.size());
            ranEmptyPos = emptyPositions.get(ranIndex);
        }
        return ranEmptyPos;

    }

    /* Get the positions of all the empty tiles in this Board. */
    private ArrayList<int[]> getAllEmptyPos(){
        ArrayList<int[]> emptyPositions = new ArrayList<>();
        // Iterate through this TwentyBoard to find & retrieve the position of all the empty tiles
        TwentyTile currentTile;
        for(int row = 0; row<this.tiles.length; row++){
            for(int col = 0; col<this.tiles.length; col++){
                currentTile = (TwentyTile)this.tiles[row][col];
                if(currentTile.isBlank()){
                    int emptyPosition[] = {row, col};
                    emptyPositions.add(emptyPosition);
                }
            }
        }
        return emptyPositions;
    }
}