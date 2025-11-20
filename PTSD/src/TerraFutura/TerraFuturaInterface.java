package TerraFutura;

import java.util.*;

//not finished
public interface TerraFuturaInterface {

    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    boolean discardLastCardFromDeck(int playerId, Deck deck);

    //martin
    //neviem co mysli pod GridCoordinate, zistime
    //Resource position je pomocna trieda na nahradenie Pair<Resource,GridPosition> z UML
    void activateCard(int playerId, GridCoordinate card, List<ResourcePosition> inputs,
                      List<ResourcePosition> outputs,
                      List<GridPosition> pollution,
                      Optional<Integer> otherPlayerId,
                      Optional<GridPosition> otherCard);

    void selectReward(int playerId, Resource resource);

    boolean turnFinished(int playerId);

    boolean selectActivationPattern(int playerId, int card);

    boolean selectScoring(int playerId, int card);

}
