import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

public class Blackjack {
    public static void main(String[] args) {
        new Blackjack();
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

        public int getValue() {
            if ("KQJ".contains(value)) {
                return 10;
            } else if (value.equals("A")) {
                return 11;
            }
            return Integer.parseInt(this.value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    private ArrayList<Card> deck;

    // Dealer variables
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // Player variables
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // Scoring system
    private int playerScore = 0;
    private int dealerScore = 0;

    // Window variables
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110; // 1:1.4 ratio
    int cardHeight = 154;

    JFrame frame = new JFrame("Blackjack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // Draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                // Draw dealer's hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // Draw player's hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
                }

                // New Improvement: Display scores
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.WHITE);
                g.drawString("Player Score: " + playerScore, 20, boardHeight - 10);
                g.drawString("Dealer Score: " + dealerScore, boardWidth - 200, boardHeight - 10);

                // Display outcome message
                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();

                    String message;
                    if (playerSum > 21) {
                        message = "You Busted! Dealer Wins!";
                        dealerScore++;
                    } else if (dealerSum > 21) {
                        message = "Dealer Busted! You Win!";
                        playerScore++;
                    } else if (playerSum > dealerSum) {
                        message = "You Win!";
                        playerScore++;
                    } else if (playerSum < dealerSum) {
                        message = "Dealer Wins!";
                        dealerScore++;
                    } else {
                        message = "Push! It's a Tie!";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.WHITE);
                    g.drawString(message, 220, 250);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    // New Improvement: Restart button
    JButton restartButton = new JButton("Restart");

    public Blackjack() {
        startGame();

        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(null);
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel, BorderLayout.CENTER);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        // New Improvement: Restart button
        restartButton.setFocusable(false);
        buttonPanel.add(restartButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerHand.add(card);
                playerSum += card.getValue();
                if (card.isAce()) {
                    playerAceCount++;
                }

                while (playerSum > 21 && playerAceCount > 0) {
                    playerSum -= 10;
                    playerAceCount--;
                }

                gamePanel.repaint();

                if (playerSum > 21) {
                    JOptionPane.showMessageDialog(frame, "You Busted! Dealer Wins!");
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                }
            }
        });

        stayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerHand.add(card);
                    dealerSum += card.getValue();
                    if (card.isAce()) {
                        dealerAceCount++;
                    }

                    while (dealerSum > 21 && dealerAceCount > 0) {
                        dealerSum -= 10;
                        dealerAceCount--;
                    }
                }

                gamePanel.repaint();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
                gamePanel.repaint();
            }
        });
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        if (hiddenCard.isAce()) {
            dealerAceCount++;
        }

        Card card = deck.remove(deck.size() - 1);
        dealerHand.add(card);
        dealerSum += card.getValue();
        if (card.isAce()) {
            dealerAceCount++;
        }

        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerHand.add(card);
            playerSum += card.getValue();
            if (card.isAce()) {
                playerAceCount++;
            }
        }
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = {"C", "D", "H", "S"};

        for (String value : values) {
            for (String suit : suits) {
                deck.add(new Card(value, suit));
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void resetGame() {
        startGame();
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }
}
