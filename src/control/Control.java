package control;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.ai.Agent;
import model.game.Board;

import java.util.Random;

/**
 * Connects model and view.
 */
public class Control {

    public TextField rowsField;
    public TextField colsField;
    public TextField bombsField;
    public TextField simulationsField;
    public TextField startRowField;
    public TextField startColField;
    public TextField seedField;
    public ProgressBar progressBar;
    public Button simulateButton;
    public TextArea outputArea;

    /**
     * Runner function.
     * 
     * Get parameters from UI input
     * generate a board and solve it
     * output the result
     */
    public void simulate() {
        try {
            // get parameters
            int rows = Integer.parseInt(rowsField.getText());
            int cols = Integer.parseInt(colsField.getText());
            int bombs = Integer.parseInt(bombsField.getText());
            int simulations = Integer.parseInt(simulationsField.getText());
            int startRow = Integer.parseInt(startRowField.getText());
            int startCol = Integer.parseInt(startColField.getText());
            int seed = Integer.parseInt(seedField.getText());
            int start = startRow * cols + startCol;

            // generate and solve board
            Board board = new Board(rows, cols, bombs);
            Agent agent = new Agent();
            Random random = new Random(seed);

            // run simulation in new thread to not block UI
            Task<Double> runSim = new Task<Double>() {
                /**
                 * Randomly generate #simulations new boards
                 * and attempt to solve them.
                 *
                 * @return win rate
                 */
                @Override
                protected Double call() {
                    int wins = 0;
                    for (int i = 1; i < simulations + 1; i++) {
                        board.generate(start, random);
                        agent.solve(board);
                        if (board.won())
                            wins++;
                        updateProgress(i, simulations);
                    }
                    return wins * 100.0 / simulations;
                }
            };

            // lock button and show progress when simulation is running
            runSim.setOnRunning(event -> {
                simulateButton.setDisable(true);
                progressBar.setVisible(true);
                outputArea.setText("...simulating");
            });

            // rudimentary progress and performance tracking
            progressBar.progressProperty().bind(runSim.progressProperty());
            final long time = System.currentTimeMillis();

            // output result and unlock UI when task is finished
            runSim.setOnSucceeded(event -> {
                simulateButton.setDisable(false);
                progressBar.setVisible(false);
                double winRate = runSim.getValue();
                double duration = (1.0 * System.currentTimeMillis() - time) / 1000;
                outputArea.setText("solved " + winRate + "% of boards in " + duration + " s");
            });

            new Thread(runSim).start();

        } catch (NumberFormatException e) {
            outputArea.setText("All text fields only accept integers.");
        }
    }

}
