package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardView implements FxmlView<ScrumBoardViewModel>, Initializable {

    public static Node currentlyDraggingStoryRow;

    @InjectViewModel
    private ScrumBoardViewModel viewModel;

    @FXML
    private VBox scrumBoardVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseDragAndDrop();
        Bindings.bindContentBidirectional(scrumBoardVBox.getChildren(), getViewModel().storyRowsProperty());
    }

    private void initialiseDragAndDrop() {
        scrumBoardVBox.setOnDragOver(event -> {
            if (currentlyDraggingStoryRow != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();

                // Now let the magic begin
                Node closestNode = null;
                double closestNodePosition = Double.MAX_VALUE;
                double cursorPosition = event.getY();

                for (Node node : scrumBoardVBox.getChildren()) {
                    // If cursor position falls within x bounds of node then check if should appear to left or right of node
                    double thisNodeY = node.localToScene(Point2D.ZERO).getY();
                    if (Math.abs(thisNodeY - cursorPosition) < closestNodePosition) {
                        closestNode = node;
                        closestNodePosition = Math.abs(thisNodeY - cursorPosition);
                    }
                }
                if (closestNode != null && cursorPosition <= closestNode.localToScene(Point2D.ZERO).getY()) {
                    // Then the dragged node should appear above closest node
                    moveStoryRow(currentlyDraggingStoryRow, scrumBoardVBox.getChildren().indexOf(closestNode));
                } else {
                    // Then the dragged node should appear below closest node
                    moveStoryRow(currentlyDraggingStoryRow, scrumBoardVBox.getChildren().indexOf(closestNode) + 1);
                }
            }
        });

        scrumBoardVBox.setOnDragDropped(event -> {
            if (currentlyDraggingStoryRow != null && !scrumBoardVBox.getChildren().contains(currentlyDraggingStoryRow)) {
                scrumBoardVBox.getChildren().add(currentlyDraggingStoryRow);
                currentlyDraggingStoryRow = null;
            }
            event.setDropCompleted(true);
        });

        scrumBoardVBox.setOnDragDone(event -> {
            currentlyDraggingStoryRow = null;
            getViewModel().updateStoryOrder();
        });
    }

    private void moveStoryRow(Node node, int index) {
        int initialIndex = scrumBoardVBox.getChildren().indexOf(node);
        // Check to make sure the node doesn't already appear at the index before removing/adding it
        if (initialIndex != index) {
            scrumBoardVBox.getChildren().remove(node);
            // If we are moving a story row after its initial position, then removing the story row from the list will
            // off set al indices by 1 that appears AFTER -> Therefore, we subtract 1 from the index to avoid an
            // IndexOutOfBoundsException and subsequent catastrophe
            index = index > initialIndex ? index-1 : index;
            scrumBoardVBox.getChildren().add(index, node);
        }
    }

    public ScrumBoardViewModel getViewModel() {
        return viewModel;
    }
}
