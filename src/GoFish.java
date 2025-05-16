import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GoFish {
    public static void main(String[] args) {
        new GoFish();
    }

    private class Card {
        private String value;
        private String suit;

        public Card(String value, String suit) {
            this.value = value;
            this.suit = suit;
        }

        @Override
        public String toString() {
            return this.value + "-" + this.suit;
        }

        public String getValue() {
            return value;
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    private class Deck {
        private ArrayList<Card> cards;

        public Deck() {
            cards = new ArrayList<>();
            String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
            String[] suits = {"C", "D", "H", "S"};

            for (String value : values) {
                for (String suit : suits) {
                    cards.add(new Card(value, suit));
                }
            }
        }

        public void shuffle() {
            Collections.shuffle(cards);
        }

        public Card deal() {
            if (!cards.isEmpty()) {
                return cards.remove(cards.size() - 1);
            }
            return null;
        }

        public boolean isEmpty() {
            return cards.isEmpty();
        }
    }

    private ArrayList<Card> playerHand;
    private ArrayList<Card> computerHand;
    private Deck deck;
    private HashMap<String, Integer> playerBooks;
    private HashMap<String, Integer> computerBooks;

    private JFrame frame;
    private JPanel gamePanel;
    private JPanel handPanel;
    private JTextArea gameLog;
    private JTextArea playerBooksDisplay;
    private JTextArea computerBooksDisplay;
    private JTextArea scoreDisplay;
    private JButton askButton;
    private JButton restartButton;

    private String lastRequestedValue = null; // Track the last card value requested by the computer
    private int playerScore = 0;
    private int computerScore = 0;

    public GoFish() {
        initializeGame();
        setupGUI();
    }

    private void initializeGame() {
        deck = new Deck();
        deck.shuffle();

        playerHand = new ArrayList<>();
        computerHand = new ArrayList<>();
        playerBooks = new HashMap<>();
        computerBooks = new HashMap<>();

        for (int i = 0; i < 7; i++) {
            playerHand.add(deck.deal());
            computerHand.add(deck.deal());
        }
    }

    private void setupGUI() {
        frame = new JFrame("Go Fish");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);

        handPanel = new JPanel();
        handPanel.setLayout(new FlowLayout());
        JScrollPane handScrollPane = new JScrollPane(handPanel);
        handScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        handScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        gamePanel.add(handScrollPane, BorderLayout.SOUTH);

        gameLog = new JTextArea(10, 50);
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Arial", Font.BOLD, 16)); // Larger font for readability
        JScrollPane scrollPane = new JScrollPane(gameLog);
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel booksPanel = new JPanel(new GridLayout(1, 3));
        playerBooksDisplay = new JTextArea("Player Books:\n");
        playerBooksDisplay.setEditable(false);
        computerBooksDisplay = new JTextArea("Computer Books:\n");
        computerBooksDisplay.setEditable(false);
        scoreDisplay = new JTextArea("Scores:\nPlayer: 0\nComputer: 0\n");
        scoreDisplay.setEditable(false);
        booksPanel.add(new JScrollPane(playerBooksDisplay));
        booksPanel.add(new JScrollPane(computerBooksDisplay));
        booksPanel.add(new JScrollPane(scoreDisplay));
        gamePanel.add(booksPanel, BorderLayout.EAST);

        askButton = new JButton("Ask for Card");
        askButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerTurn();
            }
        });

        restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(askButton);
        buttonPanel.add(restartButton);
        gamePanel.add(buttonPanel, BorderLayout.NORTH);

        updateHandDisplay();
        frame.setVisible(true);
    }

    private void updateHandDisplay() {
        handPanel.removeAll();
        int cardsPerRow = 9; // Maximum cards per row
        int cardWidth = 80;
        int cardHeight = 120;

        JPanel rowPanel = new JPanel(new FlowLayout());
        for (int i = 0; i < playerHand.size(); i++) {
            if (i % cardsPerRow == 0 && i != 0) {
                handPanel.add(rowPanel);
                rowPanel = new JPanel(new FlowLayout());
            }

            ImageIcon cardImage = new ImageIcon(getClass().getResource(playerHand.get(i).getImagePath()));
            Image scaledImage = cardImage.getImage().getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
            JLabel cardLabel = new JLabel(new ImageIcon(scaledImage));
            rowPanel.add(cardLabel);
        }
        handPanel.add(rowPanel);

        handPanel.revalidate();
        handPanel.repaint();
    }

    private void updateBooksDisplay() {
        playerBooksDisplay.setText("Player Books:\n");
        for (String book : playerBooks.keySet()) {
            playerBooksDisplay.append(book + "\n");
        }

        computerBooksDisplay.setText("Computer Books:\n");
        for (String book : computerBooks.keySet()) {
            computerBooksDisplay.append(book + "\n");
        }

        scoreDisplay.setText("Scores:\nPlayer: " + playerScore + "\nComputer: " + computerScore + "\n");
    }

    private void playerTurn() {
        if (playerHand.isEmpty()) {
            gameLog.append("You have no cards left. Drawing a card...\n");
            drawCard(playerHand);
            updateHandDisplay();
            return;
        }

        String requestedValue = JOptionPane.showInputDialog(frame, "Enter the value of the card you want to ask for (e.g., A, 2, 3, ...):");
        if (requestedValue == null || requestedValue.isEmpty() || !isValidValue(requestedValue)) {
            gameLog.append("Invalid input. Turn skipped.\n");
            computerTurn();
            return;
        }

        requestedValue = requestedValue.toUpperCase(); // Convert to uppercase for consistency
        boolean success = askForCard(playerHand, computerHand, requestedValue);
        if (success) {
            gameLog.append("You got the card(s) you asked for!\n");
        } else {
            gameLog.append("Go Fish! Drawing a card...\n");
            drawCard(playerHand);
        }

        checkForBooks(playerHand, playerBooks);
        updateHandDisplay();
        updateBooksDisplay();

        if (deck.isEmpty() && playerHand.isEmpty() && computerHand.isEmpty()) {
            endGame();
        } else {
            computerTurn();
        }
    }

    private boolean isValidValue(String value) {
        String[] validValues = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        for (String validValue : validValues) {
            if (validValue.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private void computerTurn() {
        if (computerHand.isEmpty()) {
            gameLog.append("Computer has no cards left. Drawing a card...\n");
            drawCard(computerHand);
            return;
        }

        HashMap<String, Integer> countMap = new HashMap<>();
        for (Card card : computerHand) {
            countMap.put(card.getValue(), countMap.getOrDefault(card.getValue(), 0) + 1);
        }

        String requestedValue = null;
        for (String value : countMap.keySet()) {
            if (countMap.get(value) > 0 && countMap.get(value) < 4 && !value.equals(lastRequestedValue)) {
                requestedValue = value;
                break;
            }
        }

        if (requestedValue == null) {
            requestedValue = computerHand.get((int) (Math.random() * computerHand.size())).getValue();
        }

        lastRequestedValue = requestedValue; // Update the last requested value
        gameLog.append("Computer asks for: " + requestedValue + "\n");

        boolean success = askForCard(computerHand, playerHand, requestedValue);
        if (success) {
            gameLog.append("Computer got the card(s) it asked for!\n");
        } else {
            gameLog.append("Computer goes fishing...\n");
            drawCard(computerHand);
        }

        checkForBooks(computerHand, computerBooks);
        updateBooksDisplay();

        if (deck.isEmpty() && playerHand.isEmpty() && computerHand.isEmpty()) {
            endGame();
        }
    }

    private boolean askForCard(ArrayList<Card> askerHand, ArrayList<Card> targetHand, String value) {
        boolean found = false;
        ArrayList<Card> toRemove = new ArrayList<>();
        for (Card card : targetHand) {
            if (card.getValue().equals(value)) {
                askerHand.add(card);
                toRemove.add(card);
                found = true;
            }
        }
        targetHand.removeAll(toRemove);
        return found;
    }

    private void drawCard(ArrayList<Card> hand) {
        if (!deck.isEmpty()) {
            hand.add(deck.deal());
        }
    }

    private void checkForBooks(ArrayList<Card> hand, HashMap<String, Integer> books) {
        HashMap<String, Integer> countMap = new HashMap<>();
        for (Card card : hand) {
            countMap.put(card.getValue(), countMap.getOrDefault(card.getValue(), 0) + 1);
        }

        ArrayList<Card> toRemove = new ArrayList<>();
        for (String value : countMap.keySet()) {
            if (countMap.get(value) == 4) {
                books.put(value, books.getOrDefault(value, 0) + 1);
                gameLog.append("Book completed: " + value + "\n");
                for (Card card : hand) {
                    if (card.getValue().equals(value)) {
                        toRemove.add(card);
                    }
                }
                if (books == playerBooks) {
                    playerScore++;
                } else {
                    computerScore++;
                }
            }
        }
        hand.removeAll(toRemove);
    }

    private void restartGame() {
        initializeGame();
        playerScore = 0;
        computerScore = 0;
        lastRequestedValue = null;
        gameLog.setText("");
        updateHandDisplay();
        updateBooksDisplay();
    }

    private void endGame() {
        int playerBooksCount = playerBooks.values().stream().mapToInt(Integer::intValue).sum();
        int computerBooksCount = computerBooks.values().stream().mapToInt(Integer::intValue).sum();

        String result;
        if (playerBooksCount > computerBooksCount) {
            result = "You win!";
        } else if (playerBooksCount < computerBooksCount) {
            result = "Computer wins!";
        } else {
            result = "It's a tie!";
        }

        JOptionPane.showMessageDialog(frame, "Game Over!\nPlayer Books: " + playerBooksCount + "\nComputer Books: " + computerBooksCount + "\n" + result);
        restartGame();
    }
}