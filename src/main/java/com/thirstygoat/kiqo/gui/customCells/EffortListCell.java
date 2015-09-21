package com.thirstygoat.kiqo.gui.customCells;


import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.model.Effort;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EffortListCell extends ListCell<Effort> {

    @Override
    protected void updateItem(final Effort effort, final boolean empty) {
        // calling super here is very important
        if (!empty) {
            HBox row = new HBox();
            row.setFillHeight(true);
            row.setSpacing(10);

            VBox infoCol = new VBox();
            infoCol.setSpacing(5);
            infoCol.setAlignment(Pos.TOP_RIGHT);

            Label dateLabel = new Label();
            dateLabel.setText("03/04/2015");
            infoCol.getChildren().add(dateLabel);

            Label timeLabel = new Label();
            timeLabel.setText("3:45 Pm");
            infoCol.getChildren().add(timeLabel);

            Label duration = new Label();
            duration.setText("0h 20m");
            infoCol.getChildren().add(duration);

            VBox commentCol = new VBox();
            commentCol.setSpacing(5);
            Label nameLabel = new Label();
            nameLabel.textProperty().bind(effort.personProperty().get().shortNameProperty());
            commentCol.getChildren().add(nameLabel);

            GoatLabelTextArea comment = new GoatLabelTextArea();
            comment.displayTextProperty().bind(effort.commentProperty());
            commentCol.getChildren().add(comment);

            row.getChildren().addAll(infoCol, commentCol);


            setGraphic(row);
        } else {
            // clear
            setGraphic(null);
        }
        super.updateItem(effort, empty);
    }
    



}
