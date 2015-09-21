package com.thirstygoat.kiqo.gui.customCells;


import com.thirstygoat.kiqo.gui.effort.EffortViewModel;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelDatePicker;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.util.FxUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EffortListCell extends ListCell<Effort> {

    private EffortViewModel viewModel;
    private Organisation organisation;

    public EffortListCell(EffortViewModel viewModel) {
        organisation = viewModel.organisationProperty().get();
        this.viewModel = new EffortViewModel();
    }

    @Override
    protected void updateItem(final Effort effort, final boolean empty) {
        if (!empty) {
            viewModel.load(effort, organisation);

            HBox row = new HBox();
            row.setFillHeight(true);
            row.setSpacing(10);

            VBox infoCol = new VBox();
            infoCol.setSpacing(5);
            infoCol.setAlignment(Pos.TOP_RIGHT);

            GoatLabelDatePicker dateLabel = new GoatLabelDatePicker();
            FxUtils.initGoatLabel(dateLabel, viewModel, viewModel.endDateProperty(), viewModel.endDateStringProperty(), null);
            infoCol.getChildren().add(dateLabel);

            Label timeLabel = new Label();
//            timeLabel.textProperty().bind(Bindings.createStringBinding(() -> Utilities.DATE_FORMATTER.format(effort.endTimeProperty().getValue())));
            infoCol.getChildren().add(timeLabel);

            Label duration = new Label();
            duration.textProperty().bind(Bindings.createStringBinding(() ->
                    effort.durationProperty().getValue().toHours()
                            + "h "
                            + effort.durationProperty().getValue().toMinutes()
                            + "m"));
            infoCol.getChildren().add(duration);

            VBox commentCol = new VBox();
            commentCol.setSpacing(5);
            Label nameLabel = new Label();
            HBox.setHgrow(commentCol, Priority.NEVER);
            nameLabel.textProperty().bind(effort.personProperty().get().shortNameProperty());
            commentCol.getChildren().add(nameLabel);

            GoatLabelTextArea commentLabel = new GoatLabelTextArea();
            FxUtils.initGoatLabel(commentLabel, viewModel, viewModel.commentProperty(), null, "");
            commentCol.getChildren().add(commentLabel);
            commentLabel.maxWidthProperty().bind(commentCol.widthProperty());
            HBox.setHgrow(commentCol, Priority.ALWAYS);

            row.getChildren().addAll(infoCol, commentCol);
            HBox.setHgrow(row, Priority.NEVER);


            setGraphic(row);
        } else {
            // clear
            setGraphic(null);
        }
        super.updateItem(effort, empty);
    }
    



}
