package org.TerraFutura;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Grid {

    ArrayList<GridPosition> neighboringNotActivated = new ArrayList<>();
    ArrayList<GridPosition> pattern = new ArrayList<>();
    
    Map<GridPosition,Card> board = new HashMap<>();


    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.empty();
    }

    public boolean canPutCard(GridPosition coordinate){
        return false;
    }

    public void putCard(GridPosition coordinate, Card card){

    }

    public boolean canBeActivated(GridPosition coordinate){
        return false;
    }

    public void setActivated(GridPosition coordinate){

    }

    public void setActivationPattern(List<SimpleEntry<Integer,Integer>> pattern){

    }

    public void endTurn(){

    }

    public String state(){
        return "";
    }
}
