package game;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Represents a Minesweeper board.
 * Requires rows, columns, bombs and start square to operate.
 */
public class Board {

    private int rows;                   // number of rows
    private int cols;                   // number of columns
    private int bombs;                  // number of bombs
    private int size;                   // number of squares
    private ArrayList<Square> squares;  // list of squares in board

    /**
     * Construct empty board.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param bombs number of bombs
     */
    public Board(int rows, int cols, int bombs) {
        this.rows = rows;
        this.cols = cols;
        this.size = rows * cols;
        this.bombs = bombs;

        // initialize squares
        this.squares = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
           squares.add(new Square(i));
        for (Square s: squares)
            s.setNeighbors(getNeighbors(s));
    }

    /**
     * Construct board from file.
     *
     * File format should be:
     * rows,cols,bombs,start,bomb1,bomb2, etc.
     *
     * @param filepath filepath to be constructed from
     */
    public Board(String filepath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));

            String[] line = br.readLine().split(",");
            br.close();
            this.rows = Integer.parseInt(line[0]);
            this.cols = Integer.parseInt(line[1]);
            this.size = rows * cols;
            this.bombs = Integer.parseInt(line[2]);

            //initialize squares
            this.squares =  new ArrayList<>(size);
            for (int i = 0; i < size; i++)
                squares.add(new Square(i));
            for (Square s: squares)
                s.setNeighbors(getNeighbors(s));

            // distribute bombs
            for (int i = 4; i < line.length; i++)
                squares.get(Integer.parseInt(line[i])).setBomb();

            // reveal start square
            squares.get(Integer.parseInt(line[3])).reveal();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Randomly generate new values for every square.
     *
     * @param start id of first square to be revealed
     */
    public void generate(int start) {
        squares.forEach(Square::reset);
        distributeBombs(start);
        squares.get(start).reveal();
    }

    /**
     * Randomly generate new values for every square.
     *
     * @param start  id of first square to be revealed
     * @param random enables seeded random generation
     */
    public void generate(int start, Random random) {
        squares.forEach(Square::reset);
        distributeBombs(start, random);
        squares.get(start).reveal();
    }

    /**
     * Randomly distribute bombs across viable squares.
     * Start square and its neighbors can not be bombs.
     *
     * @param start id of first square to be revealed
     */
    private void distributeBombs(int start) {
        // determine list of viable squares
        ArrayList<Square> potentialBombs = new ArrayList<>(squares);
        potentialBombs.removeAll(getNeighbors(start));
        potentialBombs.remove(squares.get(start));

        // shuffle list for random distribution
        Collections.shuffle(potentialBombs);
        // pull squares until bombs are distributed
        for(int b = 0; b < bombs; b++) {
            Square bomb = potentialBombs.get(b);
            bomb.setBomb();
        }
    }

    /**
     * Randomly distribute bombs across viable squares.
     * Start square and its neighbors can not be bombs.
     *
     * @param start  id of first square to be revealed
     * @param random enables seeded random generation
     */
    private void distributeBombs(int start, Random random) {
        // determine list of viable squares
        ArrayList<Square> potentialBombs = new ArrayList<>(squares);
        potentialBombs.removeAll(getNeighbors(start));
        potentialBombs.remove(squares.get(start));

        // shuffle list for random distribution
        Collections.shuffle(potentialBombs, random);
        // pull squares until bombs are distributed
        for(int b = 0; b < bombs; b++) {
            Square bomb = potentialBombs.get(b);
            bomb.setBomb();
        }
    }

    /**
     * Gather neighbors of a square.
     *
     * @param s square
     * @return list of neighbors
     */
    private ArrayList<Square> getNeighbors(Square s) {
        return getNeighbors(s.getId());
    }

    /**
     * Gather neighbors of a square
     *
     * @param center id of square
     * @return list of neighbors
     */
    private ArrayList<Square> getNeighbors(int center) {
        ArrayList<Square> neighbors = new ArrayList<>();
        // get row, col of square from id
        int row = center / cols;
        int col = center % cols;
        // iterate over all neighbors and gather them
        // check min/max in case square is on an edge
        for (int i = Math.max(0, row - 1); i < Math.min(rows, row + 2); i++)
            for (int j = Math.max(0, col - 1); j < Math.min(cols, col + 2); j++) {
                int id = i * cols + j;
                if (id != center)
                    neighbors.add(squares.get(id));
            }
        return neighbors;
    }

    /**
     * Check if all non-bomb squares are revealed.
     *
     * @return if game state is a win
     */
    public boolean won() {
        for (Square s: squares)
            if (s.isHiddenUnflagged())
                return false;
        return true;
    }

    /**
     * Reveal all squares.
     *
     * @param ids list of ids of squares to be revealed
     */
    public void revealAll(ArrayList<Integer> ids) {
        for (int id: ids)
            squares.get(id).reveal();
    }

    /**
     * Flag all squares.
     *
     * @param ids list of ids of squares to be flagged
     */
    public void flagAll(ArrayList<Integer> ids) {
        for (int id: ids)
            squares.get(id).flag();
    }

    public ArrayList<Square> getSquares() {
        return squares;
    }

    public void printV() {
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < size; i++) {
            if (i % cols == 0) output.append("\n");
            Square s = squares.get(i);
            if (s.isFlagged())
                output.append("-");
            else if (s.isHidden())
                output.append(".");
            else {
                if (s.getValue() == -1)
                    output.append("x");
                else
                    output.append(s.getValue());
            }
        }
        System.out.println(output);
    }

    public void printRV() {
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < size; i++) {
            if (i % cols == 0) output.append("\n");
            Square s = squares.get(i);
            if (s.isFlagged())
                output.append("-");
            else if (s.isHidden())
                output.append(".");
            else {
                if (s.getValue() == -1)
                    output.append("x");
                else
                    output.append(s.getRemainingValue());
            }
        }
        System.out.println(output);
    }

    public void printRI() {
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < size; i++) {
            if (i % cols == 0) output.append("\n");
            Square s = squares.get(i);
            if (s.isFlagged())
                output.append("-");
            else if (s.isHidden())
                output.append(".");
            else {
                if (s.getValue() == -1)
                    output.append("x");
                else
                    output.append(s.getRemainingInfo());
            }
        }
        System.out.println(output);
    }

    public void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("hard.txt"));
            StringBuilder sb = new StringBuilder();
            sb.append(rows).append(",");
            sb.append(cols).append(",");
            sb.append(bombs).append(",");
            sb.append(255).append(",");
            for (Square s: squares)
                if (s.getValue() == -1)
                    sb.append(s.getId()).append(",");
            bw.write(sb.deleteCharAt(sb.length() - 1).toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
