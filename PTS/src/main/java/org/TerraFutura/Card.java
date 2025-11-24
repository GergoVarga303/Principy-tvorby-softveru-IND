package org.TerraFutura;

import java.util.ArrayList;
import java.util.List;

public class Card {
    List<Resource> resources;
    Effect upperEffects;
    Effect lowerEffects;
    int polutionSpaces;
    public Card(Effect upperEffects, Effect lowerEffects, int polutionSpaces) {
        resources = new ArrayList<Resource>();
        this.upperEffects = upperEffects;
        this.lowerEffects = lowerEffects;
        this.polutionSpaces = polutionSpaces;
    }
}
