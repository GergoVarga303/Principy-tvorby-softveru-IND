package org.TerraFutura;

import java.util.*;

// Calculates the total score for a player based on their resources
// It counts points for a specific combination and individual resource values 

public class ScoringMethod {

    private List<Resource> resources;
    private final List<Resource> combination;
    private final int combinationValue;
    private int calculatedTotal = 0;

    public ScoringMethod(Pair<List<Resource>, Integer> combination) {
        this.combination = combination.getFirst();
        this.combinationValue = combination.getSecond();
    }

    public void selectThisMethodAndCalculate() {
        this.calculatedTotal = 0;

        if (this.resources == null) {
            return;
        }

        if (this.combination != null && !this.combination.isEmpty() && this.combinationValue != 0) {

            // Count how many of each resource the player has
            Map<Resource, Integer> inventory = new HashMap<>();
            for (Resource r : this.resources) {
                inventory.put(r, inventory.getOrDefault(r, 0) + 1);
            }

            // Count how many of each resource the recipe needs
            Map<Resource, Integer> neededCounts = new HashMap<>();
            for (Resource r : this.combination) {
                neededCounts.put(r, neededCounts.getOrDefault(r, 0) + 1);
            }

            int maxPossibleCombos = Integer.MAX_VALUE;

            // Find out how many full sets we can make
            for (Resource r : neededCounts.keySet()) {
                int needed = neededCounts.get(r);
                int available = inventory.getOrDefault(r, 0);

                int possibleForThisResource = available / needed;

                if (possibleForThisResource < maxPossibleCombos) {
                    maxPossibleCombos = possibleForThisResource;
                }
            }

            if (maxPossibleCombos == Integer.MAX_VALUE) {
                maxPossibleCombos = 0;
            }

            // Add the combination points to the total
            this.calculatedTotal += (maxPossibleCombos * this.combinationValue);
        }

        // Add points for every single item in the inventory based on its type
        for (Resource resource : this.resources) {
            calculatedTotal += individualValue(resource);
        }
    }

    // Returns the point value for a single resource type
    private int individualValue(Resource resource) {
        if (resource == null) return 0;

        switch (resource) {
            case Green, Red, Yellow:
                return 1;
            case Bulb, Gear:
                return 5;
            case Car:
                return 6;
            case Pollution:
                return -1;
            default:
                return 0;
        }
    }

    public void setAllResources(List<Resource> resources) {
        this.resources = resources;
    }

    public int getTotal() {
        return calculatedTotal;
    }

    public String state() {
        return "ScoringMethod(Total points = " + calculatedTotal + ")";
    }
}
