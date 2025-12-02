package org.TerraFutura;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Game implements TerraFuturaInterface {
    private GameState state;
    private final Player[] players;
    private int onTurn;
    private int startingPlayer;
    private int turnNumber;

    private final Pile pileI;
    private final Pile pileII;
    private final MoveCard moveCard;
    private final ProcessAction processAction;
    private final SelectReward selectReward;
    private final GameObserver gameObserver;

    public Game(int numberOfPlayers,Pile pileI, Pile pileII, GameObserver gameObserver) {
        if (numberOfPlayers < 2 || numberOfPlayers > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }

        this.players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(i,new Grid());
        }

        this.pileI = pileI;
        this.pileII = pileII;
        this.moveCard = new MoveCard();
        this.processAction = new ProcessAction();
        this.selectReward = new SelectReward();
        this.gameObserver = gameObserver;
        this.startingPlayer = 0;
        this.onTurn = 0;
        this.turnNumber = 0;
        this.state = GameState.TakeCardNoCardDiscarded;
    }

    public GameState getState() {
        return state;
    }

    public Player[] getPlayers() {
        return players.clone();
    }

    public int getOnTurn() {
        return onTurn;
    }

    public int getStartingPlayer() {
        return startingPlayer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setStartingPlayer(int startingPlayer) {
        if (startingPlayer < 0 || startingPlayer >= players.length) {
            throw new IllegalArgumentException("Invalid Player");
        }

        this.startingPlayer = startingPlayer;
        this.onTurn = startingPlayer;
        this.turnNumber = 0;
        this.state = GameState.TakeCardNoCardDiscarded;
        notifyObservers();
    }

    private String buildState(Player player) {
        JSONObject result = new JSONObject();

        result.put("state", state.toString());
        result.put("player", player.getId());
        result.put("onTurn", turnNumber);
        result.put("turnNumber", turnNumber);
        result.put("myGrid", new JSONObject(player.getGrid().state()));


/*        JSONArray others = new JSONArray();
        for(Player otherPlayer : players) {
            if(otherPlayer.getId() != player.getId()) {
                JSONObject other = new JSONObject();
                other.put("playerId", otherPlayer.getId());
                other.put("gridOverview", new JSONObject(otherPlayer.getGrid().state()));
                others.put(other);
            }
        }
        result.put("otherPlayers", others);*/
        result.put("pileI", new JSONObject(pileI.state()));
        result.put("pileII", new JSONObject(pileII.state()));
        result.put("selectReward", new JSONObject(selectReward.state()));
        return result.toString();
    }

    private void notifyObservers(){
        Map<Integer,String> states= new HashMap<>();
        for(Player player : players) states.put(player.getId(),buildState(player));
        gameObserver.notifyAll(states);
    }

    private Pile getPile(Deck deck){
        if(deck == Deck.I) return pileI;
        else if(deck == Deck.II) return pileII;
        return null;
    }

    private boolean isValidPlayer(int playerId) {
        return ((playerId >= 0 && playerId < players.length) && playerId == onTurn && state != GameState.Finish);
    }

    @Override
    public boolean takeCard(int playerId, CardSource source, GridPosition destination) {
        if (!isValidPlayer(playerId)) {
            return false;
        }

        if (state != GameState.TakeCardNoCardDiscarded &&
                state != GameState.TakeCardCardDiscarded) {
            return false;
        }
        Pile pile= getPile(source.deck);
        if (pile == null) return false;

        Grid grid= players[playerId].getGrid();
        if(grid == null) return false;

        boolean result = moveCard.moveCard(pile,destination,grid, source.index);

        if(result){
            state = GameState.ActivateCard;
            notifyObservers();
        }
        return result;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        if (!isValidPlayer(playerId)) {
            return false;
        }

        if (state != GameState.TakeCardNoCardDiscarded) {
            return false;
        }

        Pile pile= getPile(deck);
        if(pile == null) return false;

        try{
            pile.removeLastCard();
            state = GameState.TakeCardCardDiscarded;
            notifyObservers();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void activateCard(
            int playerId,
            GridPosition card,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Optional<Integer> otherPlayerId,
            Optional<GridPosition> otherCard) {

        if (!isValidPlayer(playerId)) {
            return;
        }
        if (state != GameState.ActivateCard) {
            return;
        }

        state = GameState.SelectReward;
    }

    @Override
    public void selectReward(int playerId, Resource resource) {
        if (!isValidPlayer(playerId)) {
            return;
        }

        if (state != GameState.SelectReward) {
            return;
        }
        if(!selectReward.canSelectReward(resource)) return;

        selectReward.selectReward(resource);
        state = GameState.TakeCardNoCardDiscarded;
        notifyObservers();
    }

    @Override
    public boolean turnFinished(int playerId) {
        if (!isValidPlayer(playerId)) {
            return false;
        }
        Grid grid= players[playerId].getGrid();
        if(grid != null) grid.endTurn();
        turnNumber++;
        onTurn = (startingPlayer + turnNumber) % players.length;
        if (state != GameState.Finish) {
            state = GameState.TakeCardNoCardDiscarded;
        }

        notifyObservers();
        return true;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int card) {
        if (!isValidPlayer(playerId)) {
            return false;
        }

        if (state != GameState.SelectActivationPattern) {
            return false;
        }


        state = GameState.SelectScoringMethod;

        return true;
    }

    @Override
    public boolean selectScoring(int playerId, int card) {
        if (!isValidPlayer(playerId)) {
            return false;
        }

        if (state != GameState.SelectScoringMethod) {
            return false;
        }

        state = GameState.Finish;
        return true;
    }

}
