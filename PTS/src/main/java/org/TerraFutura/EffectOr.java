package org.TerraFutura;


import java.util.ArrayList;
import java.util.List;

//Composite
//we use the class if a certain card has a possibility to choose from 2 or more Lower or upper effects
public class EffectOr implements Effect{
    private final ArrayList<Effect> effects = new ArrayList<>();

    //addChild method so no constructors, so you don't have to create a List of Effects to create an
    //instance of EffectOr, you just simply add the Effects
    public void addEffect(Effect child){
        effects.add(child);
    }

    //if any of the effects can execute the desired trade, then return true
    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        boolean hasTrue = false;
        for(Effect e : effects){
            if (e.check(input,output,pollution)){
                hasTrue = true;
            }
        }
        return hasTrue;
    }

    //return simple textual information of the cards effects
    @Override
    public String state() {
        StringBuilder sb = new StringBuilder();
        for(Effect e : effects){
            sb.append(e.state());
            sb.append('\n');
        }
        return sb.toString();
    }
}
