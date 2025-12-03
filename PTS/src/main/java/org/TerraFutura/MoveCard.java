package org.TerraFutura;

import java.util.*;

public class MoveCard {

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
