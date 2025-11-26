package org.TerraFutura;

/**
 * Represents a single player in the Terra Futura game.
 * A player owns a personal grid and chooses both an activation pattern
 * and a scoring method during the game. Player objects are immutable
 * except for the chosen scoring method and activation pattern.
 */

public class Player {

    private final int id;
    private final Grid grid;
    private ScoringMethod scoringMethod=null;
    private ActivationPattern activationPattern=null;

    public Player(int id, Grid grid) {
        this.id = id;
        this.grid = grid;
    }

    public int getId() {
        return id;
    }

    public Grid getGrid(){
        return grid;
    }

    public ScoringMethod getScoringMethod() {
        return scoringMethod;
    }

    public void setScoringMethod(ScoringMethod scoringMethod) {
        this.scoringMethod = scoringMethod;
    }

    public ActivationPattern getActivationPattern() {
        return activationPattern;
    }

    public void setActivationPattern(ActivationPattern activationPattern) {
        this.activationPattern = activationPattern;
    }

}
