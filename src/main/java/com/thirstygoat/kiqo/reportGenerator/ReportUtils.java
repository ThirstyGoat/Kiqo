package com.thirstygoat.kiqo.reportGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by james on 6/05/15.
 * <p>
 * The purpose of this class is to provide utility functions that will be used during the construction of the report.
 */
public class ReportUtils {

    /**
     * Generates and returns three '-'
     *
     * @return a string consisting of ---
     */
    public static String dashes() {
        return "---";
    }

    /**
     * Generates whitespace/indentation using spaces.
     * The amount of spaces depends on the 'size' parameter passed.
     *
     * @param size the number of spaces to be indented by
     * @return a string consisting of whitespace indentation
     */
    public static String indent(int size) {
        return String.join("", Collections.nCopies(size, " "));
    }

    /**
     * Takes a list of strings and indents each element by 'size' amount of space characters (whitespace).
     *
     * @param size    the number/amount of indentation.
     * @param strings the list of strings to be indented.
     * @return a list of strings, indented to required size.
     */
    public static List<String> indentArray(int size, List<String> strings) {
        final List<String> sb = new ArrayList<String>();
        final String indent = ReportUtils.indent(size);
        for (final String str : strings) {
            sb.add(indent + str);
        }
        return sb;
    }

    /**
     * Generates a "valueName: value" string.
     * This method invokes the toString method of value if available or replaces an empty string with '~'.
     *
     * @param valueName the string to be placed before the colon.
     * @param value     the object whose toString value will be placed after the colon.
     * @return a string of "valueName: value".
     */
    public static String valueLine(String valueName, Object value) {
        return valueName + ": " + (value == null || (value.getClass() == String.class && value.toString().equals("")) ? "~" : value.toString());
    }

    /**
     * Generates a list of strings representing a literal string which may contain newline characters
     * and other special characters.
     *
     * @param valueName the string to be placed before the colon.
     * @param value     the object whose toString value will be placed after the colon in an indented literal block.
     * @return a list of strings.
     */
    public static List<String> valueLiteral(String valueName, Object value) {
        final List<String> lines = new ArrayList<>();
        lines.add(valueName + ": |");
        for (String line : value.toString().split(System.getProperty("line.separator"))) {
            lines.add("  " + line);
        }
        return lines;
    }

    /**
     * Generates a "str: ~" string if isEmpty, otherwise "str: ".
     *
     * @param str     the string to be placed before the colon.
     * @param isEmpty the boolean value to determine if the "~" should be added or not.
     * @return "str: ~" or "str: "
     */
    public static String collectionLine(String str, Boolean isEmpty) {
        return str + ": " + (isEmpty ? "~" : "");
    }
}
