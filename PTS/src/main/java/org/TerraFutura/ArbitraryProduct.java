package org.TerraFutura;

import java.util.List;

public class ArbitraryProduct implements Effect{
    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return false;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "";
    }
}
