package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Scale for estimating story size.
 */
public enum Scale {
    FIBONACCI("Fibonacci", new String[]{"0", "1", "2", "3", "5", "8", "13", "20", "40", "100", "?", "∞"}),
    TSHIRT_SIZE("T-Shirt Size", new String[]{"XS", "S", "M", "L", "XL", "XXL", "?", "∞"}),
    DOG_BREEDS("Dog Breeds", new String[]{"Chihuahua", "Jack Russell", "Beagle", "Labrador", "German Shepherd", "Great Dane", "?", "∞"});

    private String label;
    private String[] estimates;

    private Scale(String label, String[] estimates) {
        this.label = label;
        this.estimates = estimates;
    }

    /**
     * Return the scale with the given label
     *
     * @param label label of a Scale
     * @return scale with the given label
     * @throws RuntimeException if label does not belong to any Scale
     */
    public static Scale getEnum(String label) {
        for (Scale scale : Scale.values()) {
            if (scale.label.equals(label)) {
                return scale;
            }
        }
        throw new RuntimeException("Scale " + label + " does not exist.");
    }

    /**
     * @return list of the labels for each enum
     */
    public static List<String> getStrings() {
        List<String> strs = new ArrayList<>();
        for (Scale scale : Scale.values()) {
            strs.add(scale.toString());
        }
        return strs;
    }

    @Override
    public String toString() {
        return label;
    }

    public String[] getEstimates() {
        return estimates;
    }
}
