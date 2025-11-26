package org.TerraFutura;

import  java.util.*;

public class ScoringMethod {
    private final List<Resource> resources;
    private final Points pointsPerCombination;
    private Optional<Points> calculatedTotal;

    public ScoringMethod(List<Resource> resources, Points pointsPerCombination, Optional<Points> calculatedTotal) {
        this.resources = new ArrayList<>(resources);
        this.pointsPerCombination = pointsPerCombination;
        this.calculatedTotal = calculatedTotal;
    }

    public void selectThisMethodAndCalculate() {
        int combos = resources.size();
        int total = pointsPerCombination.getValue() * combos;
        this.calculatedTotal = Optional.of(new Points(total));
    }

    public String state() {
        StringBuilder builder = new StringBuilder();
        builder.append("ScoringMethod(");
        builder.append("resources = ").append(resources);
        builder.append(", pointsPerCombination = ").append(pointsPerCombination);
        builder.append(", calculatedTotal = ");
        if (calculatedTotal.isPresent()) {
            builder.append(calculatedTotal.get());
        } else {
            builder.append("none");
        }
        builder.append(")");
        return builder.toString();
    }
}
