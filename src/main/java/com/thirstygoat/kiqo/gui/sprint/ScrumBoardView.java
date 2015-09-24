package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardView implements FxmlView<ScrumBoardViewModel>, Initializable {
    @InjectViewModel
    private ScrumBoardViewModel viewModel;

    @FXML
    private VBox scrumBoardVBox;
    @FXML
    private VBox tasksWithoutStoryVBox;
    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseDragAndDrop();
        Bindings.bindContentBidirectional(scrumBoardVBox.getChildren(), getViewModel().storyRowsProperty());
        viewModel.setView(this);
    }

    public void addTasksWithoutStoryRow(Node node) {
        tasksWithoutStoryVBox.getChildren().add(node);
    }

    private void initialiseDragAndDrop() {
        scrumBoardVBox.setOnDragOver(event -> {
            if (viewModel.currentlyDraggingStoryRow != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();

                if (viewModel.currentlyDraggingStoryInitialIndex == null) {
                    viewModel.currentlyDraggingStoryInitialIndex = scrumBoardVBox.getChildren().indexOf(viewModel.currentlyDraggingStoryRow);
                }

                int index = scrumBoardVBox.getChildren().indexOf(viewModel.currentlyDraggingStoryRow);
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
                moveStoryRow(viewModel.currentlyDraggingStoryRow, index);
            }
        });

        scrumBoardVBox.setOnDragDropped(event -> {
            if (viewModel.currentlyDraggingStoryRow != null && !scrumBoardVBox.getChildren().contains(viewModel.currentlyDraggingStoryRow)) {
                scrumBoardVBox.getChildren().add(viewModel.currentlyDraggingStoryRow);
                viewModel.currentlyDraggingStoryRow = null;
            }
            event.setDropCompleted(true);
        });

        scrumBoardVBox.setOnDragDone(event -> {
            viewModel.currentlyDraggingStoryFinalIndex = scrumBoardVBox.getChildren().indexOf(viewModel.currentlyDraggingStoryRow);
            if (viewModel.currentlyDraggingStoryInitialIndex != null &&
                    !viewModel.currentlyDraggingStoryInitialIndex.equals(viewModel.currentlyDraggingStoryFinalIndex))
                getViewModel().updateStoryOrder();

            viewModel.currentlyDraggingStoryRow = null;
            viewModel.currentlyDraggingStoryInitialIndex = null;
            viewModel.currentlyDraggingStoryFinalIndex = null;
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