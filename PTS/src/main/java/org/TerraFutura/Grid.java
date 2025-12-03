package org.TerraFutura;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Grid {
    ArrayList<GridPosition> neighbouringNotActivated = new ArrayList<>();
    ArrayList<SimpleEntry<Integer,Integer>> pattern = new ArrayList<>();

    Map<GridPosition,Card> board = new HashMap<>();

    int minX = -2;
    int maxX = 2;
    int minY = -2;
    int maxY = 2;


    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.empty();
    }

    //you can put cards only on neighbouring, not occupied positions, also you cannot put more than three cards in one
    //row or one column
    public boolean canPutCard(GridPosition coordinate){
        if(board.containsKey(coordinate)){
           return false;
        }
        //can only be placed next to another card, so must have a neighbour
        boolean hasNeighbour = false;
        for(GridPosition pos : board.keySet()){  //we find the right card
           if (pos.getX() == coordinate.getX()){    //if they have the same x coordinate
               if(pos.getY() == coordinate.getY()-1 || pos.getY() == coordinate.getY()+1){  //is upper or lower neighbour
                   hasNeighbour =  true;
                   break;
               }
           }
           else if (pos.getY() == coordinate.getY()){
               if (pos.getX() == coordinate.getX()-1 || pos.getX() == coordinate.getX()+1){  //is left or right neighbour
                   hasNeighbour =  true;
                   break;
               }
           }
        }
        if(!hasNeighbour){
            return false;
        }

        //has to be between max and min to ensure the grid will be a 3x3
        return (coordinate.getX() >= minX && coordinate.getX() <= maxX
                && coordinate.getY() >= minY && coordinate.getY() <= maxY);

    }

    public void putCard(GridPosition coordinate, Card card){
        board.put(coordinate,card);
        //we also add the neighbouring cards to the correct array
        for(GridPosition pos : board.keySet()){
            if(pos.getX() == coordinate.getX() || pos.getY() == coordinate.getY()){     //all the cards in that row and column
                neighbouringNotActivated.add(pos);
            }
        }
        //we calculate the new max and min values
        maxX = Math.abs(maxX - coordinate.getX()) > 2 ? coordinate.getX() + 2 : maxX;
        maxY = Math.abs(maxY - coordinate.getY()) > 2 ? coordinate.getY() + 2 : maxY;
        minX = Math.abs(minX - coordinate.getX()) > 2 ? coordinate.getX() - 2 : minX;
        minY = Math.abs(minY - coordinate.getY()) > 2 ? coordinate.getY() - 2 : minY;
    }

    public boolean canBeActivated(GridPosition coordinate){
        SimpleEntry<Integer,Integer> pair = new SimpleEntry<>(coordinate.getX(),coordinate.getY());
        if(neighbouringNotActivated.contains(coordinate) || pattern.contains(pair)){
            return true;
        }
        return false;
    }

    public void setActivated(GridPosition coordinate){
        neighbouringNotActivated.remove(coordinate);
    }

    public void setActivationPattern(List<SimpleEntry<Integer,Integer>> pattern){
        this.pattern.addAll(pattern);
    }

    public void endTurn(){
        neighbouringNotActivated.clear();
    }

    public String state(){
        return "";
    }
}
