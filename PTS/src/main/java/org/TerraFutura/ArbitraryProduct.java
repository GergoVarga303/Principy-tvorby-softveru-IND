package org.TerraFutura;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//the effect changes any Product type Resource to any Product, so changes Product (Products = Car,Gear,Bulb)
public class ArbitraryProduct implements Effect{
    private final Set<Resource> products = new HashSet<>(Set.of(Resource.Car,Resource.Bulb,Resource.Gear));


    //returns true, if input and output are both a single Product, so it's just a simple change
    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (input.size() == 1 && output.size() == 1 && pollution == 0){
            return products.contains(input.getFirst()) && products.contains(output.getFirst());
        }
        return false;
    }

    //simplified rules
    @Override
    public boolean hasAssistance() {
        return false;
    }

    //state will always be the same, because we can use any product
    @Override
    public String state() {
        return "from = any product \nto= any product\nno pollution\n";
    }
}
