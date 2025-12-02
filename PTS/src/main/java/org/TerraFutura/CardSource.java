package org.TerraFutura;

public class CardSource {
    private Deck deck;
    private int index;

    public CardSource(Deck deck,int index){
        this.deck = deck;
        this.index = index;
    }

    public Deck getDeck() {
        return deck;
    }

    public int getIndex() {
        return index;
    }
}
