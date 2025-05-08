import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.util.Collections;

public class Blackjack {
    public static void main(String[] args) {
        new Blackjack();
    }
    private class Card{
        private String value;
        private String suit;

        public Card(String value, String suit){
            this.value = value;
            this.suit = suit;
        }

        @Override
        public String toString(){
            return this.value +"-"+this.suit;
        }

        public int getValue(){
            if("KQJ".contains(value)){
                return 10;
            } else if(value.equals("A")){
                return 11;
            }
            return Integer.parseInt(this.value);
        }

        public boolean isAce(){
            return value.equals("A");
        }

        public String getImagePath(){
            return "./cards/"+toString()+".png";
        }
    }

    private ArrayList<Card> deck;

    //dealer stuff
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player stuff
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110; //1:1.4 ratio 
    int cardHeight = 154;

    JFrame frame = new JFrame("Blackjack");
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);


            try{
                //draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                g.drawImage(hiddenCardImg,20,20,cardWidth,cardHeight,null);

                //draw dealer's hand
                for(int i = 0; i<dealerHand.size();i++){
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg,cardWidth+ 25,20,cardWidth,cardHeight,null);
                }

                // Draw player's hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (i * (cardWidth + 10)), boardHeight - cardHeight - 40, cardWidth, cardHeight, null);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    public Blackjack() {
        startGame();

        frame.setLayout(new BorderLayout()); // Set layout for the frame
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight + 100); // Add extra height for buttons
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(null); // No layout for custom drawing
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel, BorderLayout.CENTER); // Add gamePanel to the center

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH); // Add buttonPanel to the bottom
    }

    public void startGame(){
        //deck
        buildDeck();
        shuffleDeck();

        //dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.removeLast();
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1:0;

        Card card = deck.removeLast();
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1:0;
        dealerHand.add(card);

        System.out.println("DEALER HAND");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);


        //player stuff
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        for(int i = 0; i < 2; i++){
            card = deck.removeLast();
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1:0;
            playerHand.add(card);
        }

        System.out.println("PLAYER HAND: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);


    }

    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] suits = {"C","D","H","S"};

        // use some loop structure to make a deck of cards
        // 1) make Card object with (value, suit)
        // 2) add Card object to deck
        // do this for all value suit combos
        for(String value: values){
            for(String suit: suits){
                Card card = new Card(value,suit);
                deck.add(card);
            }
        }

        System.out.println("BUILD DECK: ");
        System.out.println(deck);
    }
    public void shuffleDeck(){
        Collections.shuffle(deck);
        /*
        Random random = new Random();
        for(int i = 0; i<deck.size(); i++){
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i,randomCard);
            deck.set(j,currCard);
        }*/
        System.out.println("AFTER SHUFFLE");
        System.out.println(deck);

    }

}
