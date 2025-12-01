package org.TerraFutura;

import java.util.*;


public class ProcessAction {


    /**
     * latest change: added handling of upperEffects, given that they are implemented the same way as lowerEffects
     *                the function now also returns false if there is more than one card on the output, making some
     *                previously implemented checks excessive.
     * We enforce that the Pair<Resource, GridPosition> contains non-null values, we consider it invalid input.
     * It doesn't make much sense to take resource from nowhere as much as to take nothing from somewhere, and then add
     * it to the list of inputs or outputs, so we count on it not happening and return false if it would.
     * @param activatingCard this processes it's lowerEffect if possible. //now also it's upperEffect.
     * @param grid this is a concrete player's grid used to access player's cards and resources.
     * @param inputs these pairs contain position mapped to a resource that effect requires on input.
     * @param outputs these pairs contain position mapped to a resource that effect requires on output.
     * @param pollution this contains concrete card positions on which we put pollution.
     * We count on pollution being separated from inputs and outputs on function call!!!
     * @return false if the effect cannot be executed.
     * @return true if we executed the effect.
     * **/
    public boolean activateCard(
            Card activatingCard,
            Grid grid,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution
    ) {
        //This protects against function's null parameters
        if (activatingCard == null || grid == null) {
            return false;
        }
        //we cannot return false when outputs = null, simply because some effect may produc pollution only
        inputs = (inputs == null) ? Collections.emptyList() : inputs;
        outputs = (outputs == null) ? Collections.emptyList() : outputs;
        pollution = (pollution == null) ? Collections.emptyList() : pollution;

        // we accumulate resources to later check for their validity and effects validity
        List<Resource> inputResources = new ArrayList<>();
        for (Pair<Resource, GridPosition> p : inputs) {
            if(p.getFirst() == null || p.getSecond() == null) return false;
            inputResources.add(p.getFirst());
        }
        //in this accumulation we also check whether output all goes to the same card, otherwise we return false
        //be wary, because null could get dereferenced, if altered incorrectly.
        //these are relevant null checks & it's ugly, because it's refactoring.
        GridPosition outputCardUnique = null;
        if(!outputs.isEmpty())
        {
            if(outputs.get(0) != null) outputCardUnique = outputs.get(0).getSecond();
            else return false;
            if(!grid.getCard(outputCardUnique).isPresent() || grid.getCard(outputCardUnique).get() != activatingCard)
                return false;
        }
        List<Resource> outputResources = new ArrayList<>();
        for (Pair<Resource, GridPosition> p : outputs) {
            if(p.getSecond() == null || p.getFirst()==null) return false;
            if((outputCardUnique.getX() != p.getSecond().getX()) || (outputCardUnique.getY() != p.getSecond().getY()))
            {return false;}

            outputResources.add(p.getFirst());
        }
        int pollutionCount = pollution.size();

        // By this we check whether the given card has an effect that transforms inputResources to outputResources
        // with given pollutionCount, what it also implicitly checks for is validity of Resources.
        boolean matchesCardUpperEffect = activatingCard.check(inputResources, outputResources, pollutionCount);
        boolean matchesCardLowerEffect = activatingCard.checkLower(inputResources, outputResources, pollutionCount);
        if (!matchesCardLowerEffect && !matchesCardUpperEffect) return false;

        // We assign resources to positions, since validity of resources has been checked for.
        Map<GridPosition, List<Resource>> inputs_positionToResources = mapPositionToResources(inputs);

        Map<GridPosition, List<Resource>> outputs_positionToResources = mapPositionToResources(outputs);

        Map<GridPosition, Card> inputs_positionToCard = new HashMap<>();
        Map<GridPosition, Card> outputs_positionToCard = new HashMap<>();

        Map<Card, List<Resource>> pollution_cardToAmount = new HashMap<>();

        //This assigns pollution amount to add to each card and validates those cards
        //here if we encounter null, we just return false
        for(GridPosition coordinate : pollution) {
            if(coordinate == null) return false;
            Optional<Card> optCard = grid.getCard(coordinate);
            if (!optCard.isPresent()) return false;
            Card card = optCard.get();

            if (!pollution_cardToAmount.containsKey(card))
            {
                List<Resource> pollutionToAdd = new ArrayList<>();
                pollutionToAdd.add(Resource.Pollution);
                pollution_cardToAmount.put(card, pollutionToAdd);
            }
            else pollution_cardToAmount.get(card).add(Resource.Pollution);
        }
        //This checks whether it is possible to place given amounts of pollution on given cards
        for(Map.Entry<Card, List<Resource>> cardToAmount : pollution_cardToAmount.entrySet())
        {
            if(cardToAmount.getKey().isBlockedByPollution()) return false;
            if(cardToAmount.getKey().getPollutionSpaces() + 1 - cardToAmount.getKey().getPollutionOnCard()
                    < cardToAmount.getValue().size()) return false;
        }
        // This checks whether input cards exist and whether we can take input resources from them
        for (Map.Entry<GridPosition, List<Resource>> positionToResources : inputs_positionToResources.entrySet()) {
            GridPosition coordinate = positionToResources.getKey();
            Optional<Card> optCard = grid.getCard(coordinate);
            if (!optCard.isPresent()) return false;
            Card card = optCard.get();
            if (card.isBlockedByPollution()) return false;
            List<Resource> requiredResources = positionToResources.getValue();
            if (!card.canGetResources(requiredResources)) return false;
            inputs_positionToCard.put(coordinate, card);
        }

        // This checks whether output cards exist and whether output resources can be stored on them
        for (Map.Entry<GridPosition, List<Resource>> positionToResources : outputs_positionToResources.entrySet()) {
            GridPosition coordinate = positionToResources.getKey();
            Optional<Card> optCard = grid.getCard(coordinate);
            if (!optCard.isPresent()) return false;
            Card card = optCard.get();
            if (card.isBlockedByPollution()) return false; // cannot place outputs on blocked (inactive) cards
            List<Resource> ResourcesToPut = positionToResources.getValue();
            if (!card.canPutResources(ResourcesToPut)) return false;
            outputs_positionToCard.put(coordinate, card);
        }

        //THIS PART IS ACTION ITSELF

        //now we match our positionToResources with positionToCard lists to take input from input cards
        for(Map.Entry<GridPosition, List<Resource>> positionToResources : inputs_positionToResources.entrySet())
        {
            GridPosition coordinate = positionToResources.getKey();
            List<Resource> requiredResources = positionToResources.getValue();
            Card inputCard = inputs_positionToCard.get(coordinate);

            inputCard.getResources(requiredResources);
        }
        //now we match our positionToResources with positionToCard lists to store output on output cards
        for(Map.Entry<GridPosition, List<Resource>> positionToResources : outputs_positionToResources.entrySet())
        {
            GridPosition coordinate = positionToResources.getKey();
            //List<Resource> ResourcesToStore = positionToResources.getValue();
            Card outputCard = outputs_positionToCard.get(coordinate);
            outputCard.putResources(positionToResources.getValue());
        }
        //now we place pollution
        for(Map.Entry<Card, List<Resource>> cardToAmount : pollution_cardToAmount.entrySet())
        {
            cardToAmount.getKey().putResources(cardToAmount.getValue());
        }
        return true;
    }

    //this does what it's name suggests
    private Map<GridPosition, List<Resource>> mapPositionToResources(
            List<Pair<Resource, GridPosition>> list) {

        Map<GridPosition, List<Resource>> result = new HashMap<>();

        for (Pair<Resource, GridPosition> p : list) {
            GridPosition key = p.getSecond();
            List<Resource> resourceList = result.get(key);

            if (resourceList == null) {
                resourceList = new ArrayList<Resource>();
                result.put(key, resourceList);
            }

            resourceList.add(p.getFirst());
        }

        return result;
    }

}


