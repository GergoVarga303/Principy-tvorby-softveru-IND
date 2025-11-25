package org.TerraFutura;

import java.util.List;

//wip
//Component
public interface Effect {
    boolean check(List<Resource> input, List<Resource> output, int pollution);
    boolean hasAssistance();
    String state();
}
