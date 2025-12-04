package org.TerraFutura;

import java.util.*;

public class Pile {
    private ArrayList<Card> hiddenCards;
    private ArrayList<Card> visibleCards;
    //Random random = new Random();

    public Pile(List<Card> cards){
        hiddenCards = new ArrayList<>(cards);
        visibleCards = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            Card card = hiddenCards.removeLast();
            visibleCards.addFirst(card);
        }
    }

    //player can choose, which card he would like to get from the Visible cards of a deck, so index
    // 0 to 3, or he can draw from the top of the hidden pile with the index -1
    public Optional<Card> getCard(int index){
        if((index >=0 && index <=3)){
            return Optional.ofNullable(visibleCards.get(index));
        }
        else if(index == -1){
            if(hiddenCards.isEmpty()){
                return Optional.empty();
            }
            return Optional.ofNullable(hiddenCards.getLast());
        }
        return Optional.empty();
    }

    public void takeCard(int index){
        if (visibleCards.isEmpty()){
            throw new NoSuchElementException("Out of Cards!");
        }
        if(index == -1){
            if(hiddenCards.isEmpty()){
                throw new NoSuchElementException("Out of Cards");
            }
            hiddenCards.removeLast();
        } else if(index >=0 && index <=3){
            visibleCards.remove(index);
            if (!hiddenCards.isEmpty()) {
                visibleCards.addFirst(hiddenCards.removeLast());
            }
        }
    }

    //discard function from the visible cards
    public void removeLastCard(){
        if (visibleCards.isEmpty() || hiddenCards.isEmpty()){
            throw new NoSuchElementException("Out of Cards, cant discard and draw a new one.");
        }
        visibleCards.removeLast();
        visibleCards.addFirst(hiddenCards.removeLast());

    }

    public String state(){
        StringBuilder sb = new StringBuilder();

        sb.append("Visible cards: ");
        for (int i = 0; i < visibleCards.size(); i++) {
            sb.append("\n[").append(i).append("] ").append(visibleCards.get(i).state());
            if (i < visibleCards.size() - 1) sb.append(", ");
        }

        sb.append(" | Hidden cards: ").append(hiddenCards.size());

        return sb.toString();
    }
}
