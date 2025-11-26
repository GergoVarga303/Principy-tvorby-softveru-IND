package org.TerraFutura;

public class Points {
    private final int value;

    public Points(int value) {
        if (value < 0) {
           throw new IllegalArgumentException("The value is less then 0");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
