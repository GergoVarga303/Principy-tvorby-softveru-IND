package org.TerraFutura;

import  java.util.*;

public class ScoringMethod {
    private final List<Resource> resources;
    private final Points pointsPerCombination;
    private Optional<Points> calculatedTotal = Optional.empty();

    public ScoringMethod(List<Resource> resources, Points pointsPerCombination) {
        this.resources = new ArrayList<>(resources);
        this.pointsPerCombination = pointsPerCombination;
    }

    public void selectThisMethodAndCalculate() {
        int total = pointsPerCombination.getValue();

        if (calculatedTotal.isEmpty()) {
            this.calculatedTotal = Optional.of(new Points(total));
        } else {
            int value = this.calculatedTotal.get().getValue();
            this.calculatedTotal = Optional.of(new Points(value + total));
        }
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
