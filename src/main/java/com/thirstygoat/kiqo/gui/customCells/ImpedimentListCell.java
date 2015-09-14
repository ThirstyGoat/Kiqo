package com.thirstygoat.kiqo.gui.customCells;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Impediment;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Created by james on 14/09/15.
 */
public class ImpedimentListCell extends ListCell<Impediment> {
    UndoManager undoManager = UndoManager.getUndoManager();

    public ImpedimentListCell() {
    }

    @Override protected void updateItem(Impediment item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            Text text = new Text(item.getImpediment().toString());
            Rectangle rectangle = new Rectangle(15, 15);
            rectangle.setFill((item.getResolved() ? Color.GREEN : Color.RED));

            rectangle.setOnMouseClicked(e -> {
                EditCommand command = new EditCommand<>(item, "resolved", !item.getResolved());
                undoManager.doCommand(command);
            });


            item.resolvedProperty().addListener((observable, oldValue, newValue) -> {

                if (newValue) {
                    rectangle.setFill(Color.GREEN);
                } else {
                    rectangle.setFill(Color.RED);
                }
            });

            hBox.getChildren().addAll(rectangle, text);
            setGraphic(hBox);
        } else {
            setGraphic(null);
        }
    }
}
