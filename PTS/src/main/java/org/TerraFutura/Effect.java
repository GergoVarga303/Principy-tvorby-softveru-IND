package org.TerraFutura;

import java.util.List;

//Component
public interface Effect {
    boolean check(List<Resource> input, List<Resource> output, int pollution);
//    boolean hasAssistance(); Simplyfied rules, we do not use assistance
    String state();
}
