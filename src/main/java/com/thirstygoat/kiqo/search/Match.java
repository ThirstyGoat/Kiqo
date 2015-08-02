package com.thirstygoat.kiqo.search;

/**
 * Created by Bradley on 31/07/15.
 */
public class Match {
    private String matchedString;
    private double similarity;

    public Match(String matchedString, double similarity) {
        this.matchedString = matchedString;
        this.similarity = similarity;
    }

    public String getMatchedString() {
        return matchedString;
    }

    public double getSimilarity() {
        return similarity;
    }
}
