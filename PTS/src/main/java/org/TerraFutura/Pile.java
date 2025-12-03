package org.TerraFutura;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    public Optional<Card> getCard(int index){
        if((index >=0 && index <=3)){
            return Optional.ofNullable(visibleCards.get(index));
        }
        else if(index == -1){
            return Optional.ofNullable(hiddenCards.getLast());
        }
        return Optional.empty();
    }

    public void takeCard(int index){
        if(index == -1){
            hiddenCards.removeLast();
        } else if(index >=0 && index <=3){
            visibleCards.remove(index);
            visibleCards.addFirst(hiddenCards.removeLast());
        }
    }

    //discard function from the visible cards
    public void removeLastCard(){
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
