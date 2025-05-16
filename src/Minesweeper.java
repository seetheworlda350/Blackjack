import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Minesweeper {
    private final int rows = 10;
    private final int cols = 10;
    private final int totalMines = 20;
    private JButton[][] buttons;
    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private boolean gameOver = false;
    private boolean flagMode = false;
    private int remainingFlags;
    private JLabel flagCountLabel;

    public Minesweeper() {
        JFrame frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 650);
        frame.setLayout(new BorderLayout());

        buttons = new JButton[rows][cols];
        mines = new boolean[rows][cols];
        revealed = new boolean[rows][cols];
        flagged = new boolean[rows][cols];
        remainingFlags = totalMines;

        initializeMines();
        initializeButtons(frame);

        JPanel controlPanel = new JPanel();
        JButton flagButton = new JButton("Flag Mode: OFF");
        flagButton.addActionListener(e -> {
            flagMode = !flagMode;
            flagButton.setText(flagMode ? "Flag Mode: ON" : "Flag Mode: OFF");
        });
        flagCountLabel = new JLabel("Flags Remaining: " + remainingFlags);
        controlPanel.add(flagButton);
        controlPanel.add(flagCountLabel);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private void initializeMines() {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < totalMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (!mines[r][c]) {
                mines[r][c] = true;
                placedMines++;
            }
        }
    }

    private void initializeButtons(JFrame frame) {
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                buttons[r][c] = new JButton();
                buttons[r][c].setFont(new Font("Arial", Font.BOLD, 16));
                buttons[r][c].setBackground(Color.GREEN); // Default green color
                int finalR = r;
                int finalC = c;
                buttons[r][c].addActionListener(e -> handleButtonClick(finalR, finalC));
                gridPanel.add(buttons[r][c]);
            }
        }
        frame.add(gridPanel, BorderLayout.CENTER);
    }

    private void handleButtonClick(int row, int col) {
        if (gameOver || revealed[row][col]) {
            return;
        }

        if (flagMode) {
            if (flagged[row][col]) {
                flagged[row][col] = false;
                buttons[row][col].setText("");
                remainingFlags++;
            } else if (remainingFlags > 0) {
                flagged[row][col] = true;
                buttons[row][col].setText("F");
                buttons[row][col].setForeground(Color.BLUE);
                remainingFlags--;
            }
            flagCountLabel.setText("Flags Remaining: " + remainingFlags); // Update flag count label
        } else {
            if (flagged[row][col]) {
                return; // Cannot dig a flagged cell
            }
            revealed[row][col] = true;

            if (mines[row][col]) {
                buttons[row][col].setText("M");
                buttons[row][col].setBackground(Color.RED);
                gameOver = true;
                revealAllMines();
                JOptionPane.showMessageDialog(null, "Game Over! You hit a mine.");
            } else {
                int adjacentMines = countAdjacentMines(row, col);
                buttons[row][col].setText(adjacentMines == 0 ? "" : String.valueOf(adjacentMines));
                buttons[row][col].setEnabled(false);
                buttons[row][col].setBackground(new Color(210, 180, 140)); // Light brown color

                if (adjacentMines == 0) {
                    revealAdjacentCells(row, col);
                }

                if (checkWin()) {
                    gameOver = true;
                    JOptionPane.showMessageDialog(null, "Congratulations! You win!");
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int newRow = row + dr;
                int newCol = col + dc;

                if (isValidCell(newRow, newCol) && mines[newRow][newCol]) {
                    count++;
                }
            }
        }

        return count;
    }

    private void revealAdjacentCells(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int newRow = row + dr;
                int newCol = col + dc;

                if (isValidCell(newRow, newCol) && !revealed[newRow][newCol] && !flagged[newRow][newCol]) {
                    handleButtonClick(newRow, newCol);
                }
            }
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mines[r][c]) {
                    buttons[r][c].setText("M");
                    buttons[r][c].setBackground(Color.RED);
                }
            }
        }
    }

    private boolean checkWin() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!mines[r][c] && !revealed[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new Minesweeper();
    }
}