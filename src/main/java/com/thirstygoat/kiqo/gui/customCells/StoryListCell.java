package com.thirstygoat.kiqo.gui.customCells;

import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.model.Story;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


/**
 * Created by james on 24/07/15.
 */
public class StoryListCell extends TableCell<Story, String> {
    private final BacklogDetailsPaneViewModel vm;

    public StoryListCell(BacklogDetailsPaneViewModel backlogDetailsPaneViewModel) {
        super();
        vm = backlogDetailsPaneViewModel;
    }

    public static ObjectProperty<Paint> getBadgeColor(Story story) {
        ObjectProperty<Paint> paintColor = new SimpleObjectProperty<>(getColor(story));

        ChangeListener<? super Object> listener = (observable, oldValue, newValue) -> paintColor.set(getColor(story));

        story.estimateProperty().addListener(listener);
        story.isReadyProperty().addListener(listener);
        story.observableDependencies().addListener((ListChangeListener<Story>) c -> {
            c.next();
            for (Story s : c.getAddedSubList()) {
                s.priorityProperty().addListener(listener);
            }
            for (Story s2 : c.getRemoved()) {
                s2.priorityProperty().removeListener(listener);
            }
            paintColor.set(getColor(story));
        });

        return paintColor;
    }

    private static Color getColor(Story story) {
        for (Story s : story.getDependencies()) {
            if (s.getPriority() < story.getPriority()) {
                return Color.RED;
            }
        }
        if (story.getEstimate() == 0 && !story.getAcceptanceCriteria().isEmpty()) {
            return Color.ORANGE;
        }
        if (story.getIsReady()) {
            return Color.GREEN;
        }
        return Color.TRANSPARENT;
    }


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        Story story = null;
        if (getTableRow() != null) {
            story = (Story) getTableRow().getItem();
        }
        if (!empty && story != null) {

            final HBox hbox = new HBox();
            final Rectangle badge = new Rectangle(10, 10);
            Text shortname = new Text(item);

            hbox.getChildren().addAll(badge, shortname);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(10);
            hbox.setPadding(new Insets(0, 0, 0, 5));

            badge.fillProperty().bind(getBadgeColor(story));

            badge.visibleProperty().bind(vm.highlightStoryStateProperty());
            badge.managedProperty().bind(vm.highlightStoryStateProperty());

            setGraphic(hbox);
        } else {
            // clear
            setGraphic(null);
        }
    }


}
