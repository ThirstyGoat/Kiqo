package com.thirstygoat.kiqo.reportGenerator;

import java.util.Collections;
import java.util.Date;

/**
 * Created by leroy on 17/04/15.
 */
public class HeadingBuilder {

    // String used to denote a comment. Must be set to '#' for YAML.
    private static final String COMMENT_STR = "#";
    // Width of the circumfix (something which is both a prefix and suffix)
    // For example, given the heading ### PROJECT 1 ###, "###" is the circumfix.
    private static final int CIRCUMFIX_WIDTH = 3;
    // Define the properties of the report here
    private static final String TIME = new Date().toString();

    // Methods for generating headings. Although they could certainly be written a more compactly,
    // I think they are easier read this way because each append line corresponds to an line in the heading.
    private static String jumboLine(String line, int headingWidth) {
        final StringBuilder jumboLine = new StringBuilder();

        final String fixedWidthLine = String.format("%1$-" + (headingWidth - 1) + "s", HeadingBuilder.COMMENT_STR + "  " + line);
        jumboLine.append(fixedWidthLine);
        jumboLine.append(HeadingBuilder.COMMENT_STR);
        jumboLine.append("\n");

        return jumboLine.toString();
    }

    private static String jumboHeading(String[] lines, int headingWidth) {
        final StringBuilder heading = new StringBuilder();
        final String divider = (String.join("", Collections.nCopies(headingWidth , HeadingBuilder.COMMENT_STR)));

        heading.append("\n");
        heading.append(divider);
        heading.append("\n");
        heading.append(HeadingBuilder.jumboLine("", headingWidth));
        for (final String line : lines) {
            heading.append(HeadingBuilder.jumboLine(line, headingWidth));
        }
        heading.append(HeadingBuilder.jumboLine("", headingWidth));
        heading.append(divider);
        heading.append("\n");

        return heading.toString();
    }

    private static String regularHeading(String[] lines, int headingWidth) {
        final StringBuilder heading = new StringBuilder();
        final String circumfix = (String.join("", Collections.nCopies(HeadingBuilder.CIRCUMFIX_WIDTH, HeadingBuilder.COMMENT_STR)));

        heading.append("\n");
        for (final String line : lines) {
            heading.append(circumfix + " " + line + " " + circumfix);
        }
        heading.append('\n');

        return heading.toString();
    }

    public static String makeHeading (String[] lines, int headingWidth, Style style) {
        String heading = null;
        switch (style) {
            case JUMBO:
                heading = HeadingBuilder.jumboHeading(lines, headingWidth);
                break;
            case REGULAR:
                heading = HeadingBuilder.regularHeading(lines, headingWidth);
                break;
        }
        return heading;
    }

    // Generate the Report Heading


    public enum Style {REGULAR, JUMBO}
}