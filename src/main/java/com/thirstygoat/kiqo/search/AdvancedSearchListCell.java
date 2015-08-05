package com.thirstygoat.kiqo.search;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.Comparator;

/**
 * Created by bradley on 1/08/15.
 */
public class AdvancedSearchListCell extends ListCell<SearchResult> {

    public static final int MAX_MATCH_DISPLAY_LENGTH = 100;

    @Override
    protected void updateItem(SearchResult item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            setGraphic(generateCell());
        } else {
            setGraphic(null);
        }
        setText("");
    }

    private Node generateCell() {
        final VBox vBox = new VBox();
        final Label titleLabel = new Label(getItem().getResultText());
        vBox.getChildren().add(new Label(getItem().getResultText()));

        // Sort each match by level of similarity
        getItem().getMatches().sort((o1, o2) -> Double.compare(o1.getSimilarity(), o2.getSimilarity()));

        // For each string match, show it in the search results
        for (Match match : getItem().getMatches()) {
            vBox.getChildren().add(getHighlightedMatchNode(match));
        }
        return vBox;
    }

    private Node getHighlightedMatchNode(Match match) {
        FlowPane flowPane = new FlowPane();
        int lastIndex = 0;
        for (int[] matchPos : match.getMatchPositions()) {
            System.out.println(matchPos[1]);
            if (matchPos[1] > 100)
                break;

            flowPane.getChildren().add(new Label(match.getMatchedString().substring(lastIndex, matchPos[0])));
            Label highlightedLabel = new Label(match.getMatchedString().substring(matchPos[0], matchPos[1]));
            highlightedLabel.setStyle("-fx-background-color: yellow");
            flowPane.getChildren().add(highlightedLabel);
            lastIndex = matchPos[1];
        }
        flowPane.getChildren().add(new Label(match.getMatchedString().substring(lastIndex, Math.min(MAX_MATCH_DISPLAY_LENGTH, match.getMatchedString().length()))));

        return flowPane;
    }
}