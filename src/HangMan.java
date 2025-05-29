import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HangMan {
    private JFrame frame;
    private JPanel gamePanel;
    private JTextField guessField;
    private JLabel wordLabel, wrongGuessesLabel, timerLabel, guessesLeftLabel;
    private String wordToGuess;
    private char[] guessedWord;
    private ArrayList<Character> wrongGuesses;
    private Set<Character> allGuessedLetters;
    private int wrongGuessCount = 0;
    private int guessesLeft = 6; // Maximum allowed wrong guesses
    private Timer timer;
    private int timeRemaining = 60; // 60 seconds time limit

    public HangMan() {
        loadRandomWord();
        guessedWord = new char[wordToGuess.length()];
        for (int i = 0; i < guessedWord.length; i++) {
            guessedWord[i] = '_';
        }
        wrongGuesses = new ArrayList<>();
        allGuessedLetters = new HashSet<>();
        setupGUI();
        startTimer();
    }

    private void loadRandomWord() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/WordList.txt"))) {
            ArrayList<String> words = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
            Random random = new Random();
            wordToGuess = words.get(random.nextInt(words.size())).toUpperCase();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading word list: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupGUI() {
        frame = new JFrame("Hangman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHangman(g);
            }
        };
        gamePanel.setBackground(Color.WHITE);
        frame.add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 1)); // 5 rows for all labels

        wordLabel = new JLabel(formatGuessedWord(), SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        controlPanel.add(wordLabel);

        guessField = new JTextField();
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setFont(new Font("Arial", Font.PLAIN, 18));
        controlPanel.add(guessField);

        wrongGuessesLabel = new JLabel("Wrong Guesses: ", SwingConstants.CENTER);
        wrongGuessesLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        controlPanel.add(wrongGuessesLabel);

        guessesLeftLabel = new JLabel("Guesses Left: " + guessesLeft, SwingConstants.CENTER);
        guessesLeftLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        controlPanel.add(guessesLeftLabel);

        timerLabel = new JLabel("Time Remaining: " + timeRemaining + "s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        controlPanel.add(timerLabel);

        frame.add(controlPanel, BorderLayout.SOUTH);

        guessField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGuess(guessField.getText().toUpperCase());
                guessField.setText("");
            }
        });

        frame.setVisible(true);
    }

    private void handleGuess(String input) {
        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            timer.stop();
            JOptionPane.showMessageDialog(frame, "Please enter a single valid letter.");
            timer.start();
            return;
        }

        char guess = input.charAt(0);
        if (allGuessedLetters.contains(guess)) {
            timer.stop();
            JOptionPane.showMessageDialog(frame, "You already guessed that letter.");
            timer.start();
            return;
        }
        allGuessedLetters.add(guess);

        boolean found = false;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                guessedWord[i] = guess;
                found = true;
            }
        }

        if (!found) {
            wrongGuesses.add(guess);
            wrongGuessCount++;
            guessesLeft--;
            guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
        }

        updateGameState();
    }

    private void updateGameState() {
        wordLabel.setText(formatGuessedWord());
        wrongGuessesLabel.setText("Wrong Guesses: " + wrongGuesses);

        gamePanel.repaint();

        if (wrongGuessCount >= 6 || guessesLeft <= 0) {
            timer.stop();
            JOptionPane.showMessageDialog(frame, "Game Over! The word was: " + wordToGuess);
            resetGame();
        } else if (new String(guessedWord).equals(wordToGuess)) {
            timer.stop();
            JOptionPane.showMessageDialog(frame, "Congratulations! You guessed the word!");
            resetGame();
        }
    }

    private String formatGuessedWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : guessedWord) {
            sb.append(c).append(' ');
        }
        return sb.toString().trim();
    }

    private void drawHangman(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(50, 300, 200, 300); // Base
        g.drawLine(125, 300, 125, 50); // Pole
        g.drawLine(125, 50, 250, 50); // Top bar
        g.drawLine(250, 50, 250, 100); // Rope

        if (wrongGuessCount > 0) {
            g.drawOval(225, 100, 50, 50); // Head
        }
        if (wrongGuessCount > 1) {
            g.drawLine(250, 150, 250, 220); // Body
        }
        if (wrongGuessCount > 2) {
            g.drawLine(250, 170, 220, 200); // Left arm
        }
        if (wrongGuessCount > 3) {
            g.drawLine(250, 170, 280, 200); // Right arm
        }
        if (wrongGuessCount > 4) {
            g.drawLine(250, 220, 220, 270); // Left leg
        }
        if (wrongGuessCount > 5) {
            g.drawLine(250, 220, 280, 270); // Right leg
        }
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Time Remaining: " + timeRemaining + "s");
                if (timeRemaining <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(frame, "Time's up! The word was: " + wordToGuess);
                    resetGame();
                }
            }
        });
        timer.start();
    }

    private void resetGame() {
        loadRandomWord();
        guessedWord = new char[wordToGuess.length()];
        for (int i = 0; i < guessedWord.length; i++) {
            guessedWord[i] = '_';
        }
        wrongGuesses.clear();
        allGuessedLetters.clear();
        wrongGuessCount = 0;
        guessesLeft = 6;
        timeRemaining = 60;
        guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
        timerLabel.setText("Time Remaining: " + timeRemaining + "s");
        wordLabel.setText(formatGuessedWord());
        wrongGuessesLabel.setText("Wrong Guesses: ");
        gamePanel.repaint();
        timer.restart();
    }

    public static void main(String[] args) {
        new HangMan();
    }
}