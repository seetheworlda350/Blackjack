import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.print.DocFlavor;
import javax.swing.*;
import java.util.Collections;

public class Blackjack {
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

    public Blackjack(){
        startGame();
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
