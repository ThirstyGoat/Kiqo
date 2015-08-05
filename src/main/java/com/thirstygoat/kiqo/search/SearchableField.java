package com.thirstygoat.kiqo.search;

/**
 * Created by james on 5/08/15.
 */
public class SearchableField {
    private final String fieldName;
    private final String fieldValue;

    public SearchableField(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
