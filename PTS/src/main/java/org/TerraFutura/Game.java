package org.TerraFutura;

import java.util.*;

public class Game implements TerraFuturaInterface {
    private GameState state;
    private final int[] players;
    private int onTurn;
    private int startingPlayer;
    private int turnNumber;

    public Game(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5");
        }

        this.players = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = i;
        }

        this.startingPlayer = 0;
        this.onTurn = 0;
        this.turnNumber = 0;
        this.state = GameState.TakeCardNoCardDiscarded;
    }

    public GameState getState() {
        return state;
    }

    public int[] getPlayers() {
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

        state = GameState.ActivateCard;
        return true;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        if (!isValidPlayer(playerId)) {
            return false;
        }

        if (state != GameState.TakeCardNoCardDiscarded) {
            return false;
        }

        state = GameState.TakeCardCardDiscarded;
        return true;
    }

    @Override
    public boolean activateCard(
            int playerId,
            GridPosition card,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Integer otherPlayerId,
            GridPosition otherCard) {
      
        if (!isValidPlayer(playerId)) {
            return false;
        }

        if (state != GameState.ActivateCard) {
            return false;
        }

        state = GameState.SelectReward;
        return true;
    }

    @Override
    public void selectReward(int playerId, Resource resource) {
        if (!isValidPlayer(playerId)) {
            return;
        }

        if (state != GameState.SelectReward) {
            return;
        }

        state = GameState.TakeCardNoCardDiscarded;
    }

    @Override
    public boolean turnFinished(int playerId) {
        if (!isValidPlayer(playerId)) {
            return false;
        }

        turnNumber++;
        onTurn = (startingPlayer + turnNumber) % players.length;
        if (state != GameState.Finish) {
            state = GameState.TakeCardNoCardDiscarded;
        }

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
