package model.ai;

import model.game.Board;
import model.game.Square;

import java.util.ArrayList;

/**
 * Implements agent to solve a Minesweeper board.
 *
 * Requires a board.
 */
public class Agent {

    /**
     * Deterministically solve a Minesweeper board.
     *
     * @param board to be solved
     */
    public void solve(Board board) {
        ArrayList<Clue> clues;
        // keep gathering clues and solve them
        // while solving produces partial solutions
        do {
            clues = getClues(board.getSquares());
            unravel(clues);
        } while (eval(clues, board));
        //printClues(clues);
    }

    /**
     * Gather information from revealed squares with hidden unflagged neighbors.
     *
     * @param squares list of all squares
     * @return list of gathered clues
     */
    private ArrayList<Clue> getClues(ArrayList<Square> squares) {
        ArrayList<Clue> clues = new ArrayList<>();
        // filter for squares which provide information
        for (Square s: squares) {
            if (s.hasInfo()) {
                Clue c = new Clue();
                // add all hidden unflagged neighbor ids
                for (Square n : s.getNeighbors()) {
                    if (n.isHiddenUnflagged())
                        c.add(n.getId());
                }
                // number of unflagged neighboring bombs
                c.setBombs(s.getRemainingValue());
                clues.add(c);
            }
        }
        return clues;
    }

    /**
     * Produce new information by combining clues.
     *
     * @param clues list of clues
     */
    private void unravel(ArrayList<Clue> clues) {
        int minBombs;   // minimum number of bombs that have to be in the intersection
        int maxBombs;   // maximum number of bombs that can be in the intersection

        int i = 0;  // index of current clue
        int j;      // index of other clue being considered

        /*
         * iterate over clues while also updating them
         * when this loop terminates no new information can be produced
         */
        while (i < clues.size()) {
            j = -1;
            /*
             * check next clues that intersect with current clue
             */
            while ((j = getNextIntersecting(clues, i, j)) != -1) {
                Clue x = clues.get(i);
                Clue y = clues.get(j);
                Clue z = x.intersect(y);

                /*
                 * minimum number of bombs that have to be in the intersection
                 * = max (number of bombs - (size of clue - size of intersection))
                 */
                minBombs = Math.max(x.getBombs() - x.size() + z.size(), y.getBombs() - y.size() + z.size());
                /*
                 * maximum number of bombs that can be in the intersection
                 * = min (number of bombs)
                 */
                maxBombs = Math.min(x.getBombs(), y.getBombs());

                /*
                 * if min and max bombs are equal
                 * new information can be produced
                 */
                if (maxBombs == minBombs) {
                    /*
                     * check if x equals the intersection
                     *
                     * if not
                     *  remove all ids contained in
                     *  reduce number of bombs by number of bombs in intersection
                     *
                     * else
                     *  x is now redundant
                     *  remove x
                     *  if x is positioned before y
                     *   adjust pointer to y
                     *
                     * hold index in place because
                     *  clue was updated and needs to be considered again
                     *  or clue was removed so next clue is now at index
                     */
                    x.removeAll(z);
                    if (x.isNotEmpty()) {
                        x.setBombs(x.getBombs() - maxBombs);
                    } else {
                        clues.remove(i);
                        if (i < j)
                            j--;
                    }
                    i--;
                    /*
                     * same as for x
                     */
                    y.removeAll(z);
                    if (y.isNotEmpty()) {
                        y.setBombs(y.getBombs() - maxBombs);
                    } else {
                        clues.remove(j);
                        if (j < i)
                            i--;
                    }
                    /*
                     * append new information
                     */
                    z.setBombs(maxBombs);
                    clues.add(z);
                    break;
                }
            }
            i++;
        }
    }

    /**
     * Determine index of next intersecting clue.
     *
     * @param clues list of clues
     * @param i index of current clue
     * @param j minimum index of next intersecting clue
     * @return index of next intersecting clue
     */
    private int getNextIntersecting(ArrayList<Clue> clues, int i, int j) {
        Clue x = clues.get(i);
        for (int k = j + 1; k < clues.size(); k++)
            if (k != i && x.anyIntersection(clues.get(k)))
                return k;
        return -1;
    }

    /**
     * Evaluate clues and take possible actions.
     *
     * @param clues list of clues
     * @param board Minesweeper board
     * @return if any actions were taken
     */
    private boolean eval(ArrayList<Clue> clues, Board board) {
        boolean success = false;
        for (Clue clue: clues)
            // saturated clues can be flagged
            if (clue.isSaturated()) {
                board.flagAll(clue.getIds());
                success = true;
            // safe clues can be revealed
            } else if (clue.isSafe()) {
                board.revealAll(clue.getIds());
                success = true;
            }
        return success;
    }

    public void printClues(ArrayList<Clue> clues) {
        clues.forEach(c -> System.out.println(c.toString()));
        System.out.println("#clues = " + clues.size());
        clues.forEach(c1 -> clues.forEach(c2 -> {
            if (c1.anyIntersection(c2) && !c1.equals(c2)) {
                System.out.println(c1.toString() + " - " + c2.toString());
            }
        }));
    }

}
