package com.thirstygoat.kiqo.search;

import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
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
    private AdvancedSearchViewModel viewModel;

    public AdvancedSearchListCell(AdvancedSearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

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
        final BorderPane borderPane = new BorderPane();
        final Label titleLabel = new Label(getItem().getResultText());
        titleLabel.getStyleClass().add("advanced-search-result-title-label");
        vBox.getChildren().add(borderPane);
        borderPane.setLeft(titleLabel);
        borderPane.setPadding(new Insets(5, 0, 5, 0));
        final Label typeLabel = new Label(getItem().getType());
        typeLabel.getStyleClass().add("advanced-search-result-type-label");
        borderPane.setRight(typeLabel);

        // Sort each match by level of similarity
        getItem().getMatches().sort((o1, o2) -> Double.compare(o1.getSimilarity(), o2.getSimilarity()));

        SearchResult searchResult = getItem();
        // For each match, display the match in a label highlighting matched area
        for (Match match : searchResult.getMatches()) {
            // We only add matches if they aren't empty
            if (!match.getMatchedString().isEmpty()) {
                vBox.getChildren().add(getHighlightedMatchNode(match));
            }
        }

        vBox.setCursor(javafx.scene.Cursor.HAND);
        vBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // They've double clicked woohoo!
                viewModel.action(searchResult);
            }
        });
        return vBox;
    }

    private Node getHighlightedMatchNode(Match match) {
        FlowPane flowPane = new FlowPane();
        flowPane.getStyleClass().add("advanced-search-result-match-container");
        Label headingLabel = new Label(match.getMatchedField().getFieldName() + ": ");
        headingLabel.getStyleClass().add("advanced-search-result-heading-label");
        flowPane.getChildren().add(headingLabel);

        int lastIndex = 0;
        int shownChars = 0;

        boolean leadingEllipsis = false;
        boolean trailingEllipsis = false;

        // say shownChars = 20, and query length is 90, we only show 80 of query length

        for (int[] matchPos : match.getMatchPositions()) {
            if (shownChars < MAX_MATCH_DISPLAY_LENGTH) {
                int startPos = shownChars > 0 ? lastIndex : Math.max(0, matchPos[0]-20);
                int endPos = Math.min(MAX_MATCH_DISPLAY_LENGTH-shownChars+startPos, matchPos[0]);
                if (startPos > endPos)
                    break;
                Label noMatchLabel = new Label(match.getMatchedString().substring(startPos, endPos));
                shownChars += matchPos[0] - startPos;

                flowPane.getChildren().add(noMatchLabel);
                if (shownChars < MAX_MATCH_DISPLAY_LENGTH) {
                    endPos = Math.min(matchPos[1], matchPos[0] + MAX_MATCH_DISPLAY_LENGTH - shownChars);
                    Label matchLabel = new Label(match.getMatchedString().substring(matchPos[0], endPos));
                    matchLabel.getStyleClass().add("advanced-search-result-highlight");
                    shownChars += matchPos[1] - matchPos[0];
                    lastIndex = endPos;
                    flowPane.getChildren().add(matchLabel);
                }
            } else {
                break;
            }
        }

        // say shownChars = 80, we need to show to either the end of the string, or
        // 20 characters plus,   MAX_MATCH_DISPLAY_LENGTH - shownChars + lastIndex

        // Let's fill up the remaining char positions if there's room
        if (shownChars < MAX_MATCH_DISPLAY_LENGTH) {
            int endPos = Math.min(match.getMatchedString().length(), MAX_MATCH_DISPLAY_LENGTH - shownChars + lastIndex);
            Label noMatchLabel = new Label(match.getMatchedString().substring(lastIndex, endPos));
            flowPane.getChildren().add(noMatchLabel);
            shownChars += endPos - lastIndex;
        }

        if (shownChars >= MAX_MATCH_DISPLAY_LENGTH) {
            trailingEllipsis = true;
        }

        // Check if we actually have match positions, since we don't with a regex
        if (!match.getMatchPositions().isEmpty()) {

            // If the first match position is greater than 20, AND we are occupying MAX_MATCH_DISPLAY_LENGTH
            // then display leading ellipsis
            if (match.getMatchPositions().get(0)[0] >= 20 && shownChars >= MAX_MATCH_DISPLAY_LENGTH) {
                leadingEllipsis = true;
            }

            if (leadingEllipsis) {
                flowPane.getChildren().add(1, new Label("...")); // leading elipsis
            }
            if (trailingEllipsis) {
                flowPane.getChildren().add(new Label("...")); // trailing elipsis
            }
        }

        return flowPane;
    }
}