package model.ai;

import java.util.ArrayList;

/**
 * Represents a clue provided by a revealed square with hidden unflagged neighbors
 * or unravelled from other clues.
 *
 * Requires squares and how many of them are bombs.
 * Implemented with an array. Faster than ArrayList
 *
 */
public class Clue {

    private final int[] ids;    // list of square ids
    private int bombs;          // number of bombs

    /**
     * Construct an empty clue.
     *
     * Maximum number of squares is 8.
     * Non-square positions default to -1.
     * Resizing the array appears to be slower.
     */
    public Clue() {
        ids = new int[8];
        for (int i = 0; i < 8; i++)
            ids[i] = -1;
    }

    /**
     * Add square id to clue.
     *
     * @param id of square to be added
     */
    public void add(int id) {
        for (int i = 0; i < 8; i++)
            if (ids[i] == -1) {
                ids[i] = id;
                break;
            }
    }

    /**
     * Construct clue by intersecting two clues.
     *
     * @param b other clue to intersect with
     * @return intersection of two clues without bombs
     */
    public Clue intersect(Clue b) {
        Clue c = new Clue();
        // if other clue contains id
        // add to intersection
        for (int i = 0; i < 8; i++)
            if (b.contains(ids[i]))
                c.add(ids[i]);
        return c;
    }

    /**
     * Remove all ids of other clue
     *
     * @param b other clue
     */
    public void removeAll(Clue b) {
        // if other clue contains id
        // set to default
        for (int i = 0; i < 8; i++)
            if (b.contains(ids[i]))
                ids[i] = -1;
    }

    /**
     * Check if clue contains a specific square id.
     *
     * @param id of square to be checked.
     * @return if clue contains square.
     */
    public boolean contains(int id) {
        // checking default returns immediately
        if (id == -1)
            return false;
        // check if any position contains id
        for (int i = 0; i < 8; i++)
            if (ids[i] == id)
                return true;
        return false;
    }

    /**
     * Check if intersects with other clue at all.
     *
     * @param b other clue
     * @return if clue intersects with other clue
     */
    public boolean anyIntersection(Clue b) {
        // if other clue contains any id
        // there is an intersection
        for (int i = 0; i < 8; i++)
            if (b.contains(ids[i]))
                return true;
        return false;
    }

    /**
     * Check if clue contains any ids.
     *
     * @return if clue is not empty
     */
    public boolean isNotEmpty() {
        for (int i = 0; i < 8; i++)
            if (ids[i] != -1)
                return true;
        return false;
    }

    /**
     * Check if every square has to be a bomb.
     *
     * @return if squares is saturated
     */
    public boolean isSaturated() {
        return size() == bombs;
    }

    /**
     * Check if every square has to be non-bomb.
     *
     * @return if squares are safe
     */
    public boolean isSafe() {
        return bombs == 0;
    }

    /**
     * Convert clue to a list of ids.
     *
     * @return list of ids
     */
    public ArrayList<Integer> getIds() {
        ArrayList<Integer> idsList = new ArrayList<>(8);
        for (int i = 0; i < 8; i++)
            if (ids[i] != -1)
                idsList.add(ids[i]);
        return idsList;
    }

    public int getBombs() {
        return bombs;
    }

    /**
     * Count number of ids in clue
     *
     * @return number of ids in clue
     */
    public int size() {
        int size = 0;
        // count positions that are not default
        for (int i = 0; i < 8; i++)
            if (ids[i] != -1)
                size++;
        return size;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clue b = (Clue) o;
        for (int i = 0; i < 8; i++)
            if (!b.contains(ids[i]))
                return false;
        return bombs == b.getBombs();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("([");
        getIds().forEach(id -> sb.append(id).append(" "));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("], ").append(bombs).append(")");
        return sb.toString();
    }

}
