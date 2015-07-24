package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.viewModel.detailsPane.BacklogDetailsPaneViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
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
        ObjectProperty<Paint> paintColor = new SimpleObjectProperty<>();
        final Paint green = Paint.valueOf("GREEN");
        final Paint orange = Paint.valueOf("ORANGE");
        final Paint red = Paint.valueOf("RED");

        // dependencies are a lower priority than this story

        paintColor.set(green);

        return paintColor;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {

            final HBox hbox = new HBox();
            final Rectangle badge = new Rectangle(10, 10);
            Text shortname = new Text();
            shortname.textProperty().set(item);

            hbox.getChildren().addAll(badge, shortname);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(10);
            hbox.setPadding(new Insets(0, 0, 0, 5));


            /*
            Stories that are ready have green highlighting

            Orange highlighting is used for stories that have not yet been estimated but which

            could be (i.e. they have one or more ACs)

            Red highlighting is used for stories which depend on one or more stories which have
             */



            final Story story = (Story) getTableRow().getItem();

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
