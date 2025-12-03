package org.TerraFutura;

import java.util.List;
import java.util.Optional;

public interface TerraFuturaInterface {
    boolean takeCard(int playerId, CardSource source, GridPosition destination);
    boolean discardLastCardFromDeck(int playerId, Deck deck);
    boolean activateCard(
            int playerId,
            GridPosition card,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Optional<Integer> otherPlayerId,
            Optional<GridPosition> otherCard);
    //no selectReward, simplified rules
    boolean turnFinished(int playerId);
    boolean selectActivationPattern(int playerId, int card);
    boolean selectScoring(int playerId, int card);
}
