package org.TerraFutura;

import java.util.*;

/**
 * Card in player's territory.

 * Responsibilities:
 *  - stores resources produced by this card
 *  - tracks pollution on the card (safe pollution spaces vs. blocking cube)
 *  - checks whether a requested activation (input/output/pollution) is possible
 *    on this card according to game rules

 * Game rules (shortened):
 *  - Resources are kept directly on the cards they originated from.
 *  - Pollution (black cube) must be placed on a card:
 *     - first into the pollution space (if it is still free)
 *     - if the pollution space is full, the cube goes to the center and the card becomes
 *       inactive – its effect cannot be used and its resources cannot be spent.
 *  - There may be at most 1 pollution cube in the center.
 */

public class Card {

    private final List<Resource> resources = new ArrayList<>();

    /** Effect printed in the upper part of the card (gain). May be null. */
    private final Effect upperEffect;

    /** Effect printed in the lower part of the card (exchange). May be null. */
    private final Effect lowerEffect;

    /** How many harmless pollution spaces this card has. */
    private final int pollutionSpaces;

    /** How many of those pollution spaces are taken. */
    private int usedPollutionSpaces;

    /** True if there is a pollution cube in the center of the card — the card is inactive. */
    private boolean blockedByPollution;

    // ---------- Constructors ----------

    public Card(Effect upperEffect, Effect lowerEffect, int pollutionSpaces) {
        this.upperEffect = upperEffect;
        this.lowerEffect = lowerEffect;
        this.pollutionSpaces = pollutionSpaces;
        this.usedPollutionSpaces = 0;
        this.blockedByPollution = false;
    }


    // ---------- Basic accessors useful in tests / other classes ----------

    public List<Resource> getResourcesSnapshot() {
        return Collections.unmodifiableList(resources);
    }

    public int getPollutionSpaces() {
        return pollutionSpaces;
    }

    /** Total number of pollution cubes on this card (safe + centre). */
    public int getPollutionOnCard() {
        return usedPollutionSpaces + (blockedByPollution ? 1 : 0);
    }

    public boolean isBlockedByPollution() {
        return blockedByPollution;
    }

    /**
     * Checks if this card can provide the requested resources.
     * Returns false if the card is blocked or if pollution is requested.
     *
     * @param requested resources to take (null or empty = allowed)
     * @return true if all requested resources exist on this card
     */
    public boolean canGetResources(List<Resource> requested) {
        if (blockedByPollution) return false;
        if (requested == null || requested.isEmpty()) return true;

        Map<Resource, Integer> counts = new EnumMap<>(Resource.class);
        for (Resource r : resources) {
            counts.put(r, counts.getOrDefault(r, 0) + 1);
        }

        for (Resource r : requested) {
            if (r == Resource.Pollution) {
                // Should not ever happend, we do not pay with pollution
                return false;
            }
            Integer current = counts.get(r);
            if (current == null || current == 0) {
                return false;
            }
            counts.put(r, current - 1);
        }
        return true;
    }


    /**
     * Removes the requested resources from this card.
     * Throws IllegalArgumentException if the card is blocked or lacks resources.
     *
     * @param requested resources to take (ignored if null)
     */
    public void getResources(List<Resource> requested) {
        if (!canGetResources(requested)) {
            throw new IllegalArgumentException("Not enough resources on this card or card is blocked.");
        }
        if (requested == null) {
            return;
        }
        for (Resource r : requested) {
            resources.remove(r);
        }
    }

    /**
     * Checks if the given resources can be placed on this card.
     * Returns false if the card is blocked or the number of pollution cubes exceeds available slots.
     *
     * @param gained resources to place (null or empty = allowed), include pollution blocks
     * @return true if all gained resources, including pollution, can be stored
     */
    public boolean canPutResources(List<Resource> gained) {
        if (blockedByPollution) return false;
        if (gained == null || gained.isEmpty()) return true;

        int newPollution = 0;
        for (Resource r : gained) {
            if (r == Resource.Pollution)
                newPollution++;
        }
        if (newPollution == 0) return true;

        int freeSafe = pollutionSpaces - usedPollutionSpaces + 1;
        return newPollution <= freeSafe;
    }

    /**
     * Places the given resources on this card.
     * Throws IllegalArgumentException if pollution exceeds available capacity.
     *
     * @param gained resources to add (ignored if null or empty)
     */
    public void putResources(List<Resource> gained) {
        if (!canPutResources(gained)) {
            throw new IllegalArgumentException("Cannot put more pollutions on card.");
        }
        if (gained == null || gained.isEmpty()) return;
        resources.addAll(gained);
        for(Resource r : gained) {
            if (r == Resource.Pollution) {
                usedPollutionSpaces++;
            }
        }
        if (usedPollutionSpaces == pollutionSpaces+1){
            blockedByPollution = true;
        }
    }

    // ---------- Effect checking (card + effect together) ----------

    /**
     * Checks whether this card can perform the given action.
     * Verifies resource availability, pollution placement, and the upper effect.
     *
     * @param input     resources to take from the card
     * @param output    resources to place on the card
     * @param pollution number of pollution cubes to generate
     * @return true if all checks pass and the upper effect allows it
     */
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {

        if(!internalCommonCheck(input,output,pollution)) return false;
        return upperEffect != null && upperEffect.check(input, output, pollution);
    }

    /**
     * Special rule for Assistance — checks only the lower effect.
     * Still validates resources and pollution on the card.
     *
     * @param input     resources to take from the card
     * @param output    resources to place on the card
     * @param pollution number of pollution cubes to generate
     * @return true if all checks pass and the lower effect allows it
     */
    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        if (!internalCommonCheck(input, output,pollution)) return false;
        return lowerEffect != null && lowerEffect.check(input, output, pollution);
    }

    /**
     * Shared validation logic for both check() and checkLower().
     * Ensures input resources can be taken and output (including pollution) can be placed.
     *
     * @param input     resources to take
     * @param output    resources to place
     * @param pollution number of pollution cubes to add
     * @return true if both resource removal and placement are possible
     */
    private boolean internalCommonCheck(List<Resource> input, List<Resource> output, int pollution) {
        if(!canGetResources(input)) return false;
        List<Resource> tmpOutput = new ArrayList<>(output);
        for(int i = 0; i<pollution; i++) {
            tmpOutput.add(Resource.Pollution);
        }
        return canPutResources(tmpOutput);
    }

    /**
     * Returns a simple textual representation of the card state
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        String card =  "Card{" +
                "resources=" + resources +
                ", pollutionSpaces=" + pollutionSpaces +
                ", usedPollutionSpaces=" + usedPollutionSpaces +
                ", blockedByPollution=" + blockedByPollution +
                '}';
        sb.append(card);
        if(upperEffect != null){
            sb.append("\nUpper effect=");
            sb.append(upperEffect.state());
        }
        if(lowerEffect != null){
            sb.append("\nLower effect=");
            sb.append(lowerEffect.state());
        }
        return sb.toString();
    }
}
