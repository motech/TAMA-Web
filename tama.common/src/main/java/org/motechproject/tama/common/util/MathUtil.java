package org.motechproject.tama.common.util;


public class MathUtil {
    public static double roundOffTo(double number, int numberOfPlaces) {
        double weighingFactor = Math.pow(10, numberOfPlaces);
        return (double) Math.round(number * weighingFactor) / weighingFactor;
    }
}