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
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardView implements FxmlView<ScrumBoardViewModel>, Initializable {

    public static Node currentlyDraggingStoryRow;
    public static Integer currentlyDraggingStoryInitialIndex = null;
    public static Integer currentlyDraggingStoryFinalIndex = null;

    @InjectViewModel
    private ScrumBoardViewModel viewModel;

    @FXML
    private VBox scrumBoardVBox;
    @FXML
    private ScrollPane scrollPane;

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

                if (currentlyDraggingStoryInitialIndex == null) {
                    currentlyDraggingStoryInitialIndex = scrumBoardVBox.getChildren().indexOf(currentlyDraggingStoryRow);
                }

                // Below is WIP adding auto scroll support when dragging in the top/bottom 10%
                // Check if the user is dragging in the bottom/top 10 percent
                // If it is, then we need to scroll down accordingly
//                double scrumBoardTopPos = 0;
//                double scrumBoardBottomPos = scrollPane.getBoundsInParent().getMaxY() - scrollPane.getBoundsInParent().getMinY();
//                double scrumBoardHeight = scrumBoardBottomPos - scrumBoardTopPos;
//
//                System.out.println(scrumBoardTopPos + " " + scrumBoardBottomPos + ", " + scrumBoardHeight);
//
//                double cursorPos = event.getY();
//                System.out.println("Cursor pos: " + cursorPos);
//
//                boolean inTop = cursorPos >= scrumBoardTopPos && cursorPos <= (scrumBoardTopPos + 0.1*scrumBoardHeight);
//                boolean inBottom = cursorPos <= scrumBoardBottomPos && cursorPos >= (scrumBoardBottomPos - 0.1*scrumBoardHeight);
//                System.out.println("In top? " + inTop);
//                System.out.println("In bottom? " + inBottom);
//
//                double scrollPos = scrollPane.getVvalue();
//                if (inBottom) {
//                    scrollPane.setVvalue(Math.max(1, scrollPos*1.1));
//                } else if (inTop) {
//                    scrollPane.setVvalue(scrollPos*0.9);
//                }

                int index = scrumBoardVBox.getChildren().indexOf(currentlyDraggingStoryRow);
                for (Node storyRow : scrumBoardVBox.getChildren()) {
                    double top = storyRow.getBoundsInParent().getMinY();
                    double bottom = storyRow.getBoundsInParent().getMaxY();
                    double mid = ((bottom-top)/2) + top;

                    boolean inRange = event.getY() >= top && event.getY() <= bottom;
                    if (inRange) {
                        if (event.getY() <= mid) {
                            // Dragged node should appear AT this index
                            index = scrumBoardVBox.getChildren().indexOf(storyRow);
                        } else {
                            // Dragged node should appear AFTER this index
                            index = scrumBoardVBox.getChildren().indexOf(storyRow) + 1;
                        }
                        break;
                    }
                }
                moveStoryRow(currentlyDraggingStoryRow, index);
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
            currentlyDraggingStoryFinalIndex = scrumBoardVBox.getChildren().indexOf(currentlyDraggingStoryRow);
            if (!currentlyDraggingStoryInitialIndex.equals(currentlyDraggingStoryFinalIndex))
                getViewModel().updateStoryOrder();

            currentlyDraggingStoryRow = null;
            currentlyDraggingStoryInitialIndex = null;
            currentlyDraggingStoryFinalIndex = null;
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