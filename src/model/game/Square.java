package model.game;

import java.util.ArrayList;

/**
 * Represents a square on a Minesweeper board.
 * Implements board updates on a low level.
 */
public class Square {

    private final int id;                   // square id
    private int value;                      // number of neighboring bombs or -1 if square is bomb
    private int remainingValue;             // number of unflagged neighboring bombs
    private int remainingInfo;              // number of hidden unflagged neighbors
    private boolean hidden;                 // square is hidden or revealed
    private boolean flagged;                // square is flagged or unflagged
    private ArrayList<Square> neighbors;    // list of neighboring squares

    /**
     * Construct empty square.
     *
     * @param id of square
     */
    public Square(int id) {
        this.id = id;
        this.value = 0;
        this.remainingValue = 0;
        this.hidden = true;
        this.flagged = false;
    }

    /**
     * Reset to an non-generated state.
     */
    public void reset() {
        value = 0;
        remainingValue = 0;
        remainingInfo = neighbors.size();
        hidden = true;
        flagged = false;
    }

    /**
     * Reveal square and update its neighbors.
     */
    public void reveal() {
        // can only reveal if hidden unflagged
        if (hidden && !flagged) {
            // reveal
            hidden = false;
            // update neighbors
            for (Square n: neighbors) {
                n.decrementRemainingInfo();
                // if square has value 0
                // recursively reveal its neighbors
                if (value == 0)
                    n.reveal();
            }
        }
    }

    /**
     * Flag square and update its neighbors.
     */
    public void flag() {
        // can only flag if hidden unflagged
        if (hidden && !flagged) {
            // flag
            flagged = true;
            // update neighbors
            for (Square n: neighbors) {
                n.decrementRemainingInfo();
                n.decrementRemainingValue();
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getRemainingValue() {
        return remainingValue;
    }

    public int getRemainingInfo() {
        return remainingInfo;
    }

    /**
     * Check if square is revealed and has hidden unflagged neighbors.
     *
     * @return if square provides information
     */
    public boolean hasInfo() {
        return !hidden && remainingInfo > 0;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Check if square is hidden unflagged
     *
     * @return if square is hidden unflagged
     */
    public boolean isHiddenUnflagged() {
        return hidden && !flagged;
    }

    public ArrayList<Square> getNeighbors() {
        return neighbors;
    }

    /**
     * Increment value if square is non-bomb
     */
    public void incrementValue() {
        if (value > -1){
            value++;
            remainingValue++;
        }
    }

    /**
     * Set square to bomb.
     * Update values of neighbors.
     */
    public void setBomb() {
        value = -1;
        neighbors.forEach(Square::incrementValue);
    }

    public void decrementRemainingInfo() {
        remainingInfo--;
    }

    public void decrementRemainingValue() {
        remainingValue--;
    }

    public void setNeighbors(ArrayList<Square> neighbors) {
        this.neighbors = neighbors;
        remainingInfo = neighbors.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return id == square.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
