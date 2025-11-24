package org.TerraFutura;

import java.util.*;

public class GameObserver {
    private Map<Integer, TerraFuturaObserverInterface> observers= new HashMap<>();

    public void addObserver(int pId,TerraFuturaObserverInterface observer) {
        observers.put(pId, observer);
    }

    public void notifyAll(Map<Integer,String> newState) {
        for(Map.Entry<Integer, TerraFuturaObserverInterface> entry : observers.entrySet()){
            int pId = entry.getKey();
            TerraFuturaObserverInterface observer = entry.getValue();
            String state= newState.get(pId);
            if(observer != null) observer.notify(state);
        }
    }

}
