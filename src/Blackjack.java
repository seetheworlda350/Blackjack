import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.print.DocFlavor;
import javax.swing.*;

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
    }
    private ArrayList<Card> deck;
    public Blackjack(){
        startGame();
    }

    public void startGame(){
        //deck
        buildDeck();
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

}
