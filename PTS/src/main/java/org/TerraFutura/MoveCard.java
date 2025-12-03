package org.TerraFutura;

import java.util.*;

public class MoveCard {

    //player can choose, which card he would like to get from the Visible cards of a deck, so index
    // 0 to 3, or he can draw from the top of the hidden pile with the index -1
    public boolean moveCard(Pile pile, GridPosition gridCoordinate, Grid grid, int cardIndex) {

        if (pile == null || gridCoordinate == null || grid == null || cardIndex < -1 || cardIndex > 3) {
            return false;
        }

        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }

        Optional<Card> card = pile.getCard(cardIndex);

        pile.takeCard(cardIndex);
        grid.putCard(gridCoordinate, card.orElse(null));

        return true;
    }
}
