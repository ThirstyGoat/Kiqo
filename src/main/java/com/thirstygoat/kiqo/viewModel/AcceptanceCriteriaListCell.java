package com.thirstygoat.kiqo.viewModel;


import java.util.Map;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;

public class AcceptanceCriteriaListCell extends ListCell<AcceptanceCriteria> {
    private final Map<State, Image> images;
    private Point2D dragOffset = new Point2D(0, 0);
    private ListView<AcceptanceCriteria> listView;
    private UndoManager undoManager = UndoManager.getUndoManager();
    public AcceptanceCriteriaListCell(ListView<AcceptanceCriteria> listView, Map<State, Image> images) {
        this.listView = listView;
        this.images = images;
    }
    
    private static AcceptanceCriteria getAcceptanceCriteria(DragEvent event) {
        AcceptanceCriteria acceptanceCriteria = new AcceptanceCriteria(
                ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("criteria")
        );
        AcceptanceCriteria.State state = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("state");
        acceptanceCriteria.setState(state);
        return acceptanceCriteria;
    }

    /**
     * Determines if an event contains an AC (to prevent dragging of files etc into the listview)
     */
    private static boolean sourceIsAcceptanceCriteria(DragEvent event) {
        try {
            ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("criteria");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected void updateItem(final AcceptanceCriteria item, final boolean empty) {
        // calling super here is very important
        if (!empty) {
            initialiseDragAndDrop(item);
            
            final BorderPane borderPane = new BorderPane();
            Text criteria = new Text();
            criteria.textProperty().bind(item.criteria);
            criteria.wrappingWidthProperty().bind(listView.widthProperty().subtract(130));
            borderPane.setLeft(criteria);
            BorderPane.setAlignment(criteria, Pos.CENTER_LEFT);

            final ImageView imageView = new ImageView();
            Button stateButton = new Button("", imageView);
            
            stateButton.setOnAction(new StateButtonHandler(imageView, item.state, images));
            borderPane.setRight(stateButton);

            setGraphic(borderPane);
        } else {
            // clear
            setGraphic(null);
        }
        super.updateItem(item, empty);
    }
    
    private void initialiseDragAndDrop(AcceptanceCriteria ac) {

        // Called when the dragged item is over another cell
        EventHandler<DragEvent> mContextDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            int buffer = 20;
            double yPos = getParent().sceneToLocal(event.getSceneX(), event.getSceneY()).getY();
            if (yPos < buffer) {
                listView.scrollTo(getIndex() - 2);
            } else if (yPos > listView.getHeight() - buffer) {
                listView.scrollTo(getIndex() - 3);
            }
            event.consume();
        };

        // Called when the dragged item enters another cell
        EventHandler<DragEvent> mContextDragEntered = event -> {
//            System.out.println("enter");
            if (sourceIsAcceptanceCriteria(event)) {
                ((AcceptanceCriteriaListCell) event.getSource()).setStyle("-fx-background-color: greenyellow");
                event.acceptTransferModes(TransferMode.ANY);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                if (getIndex() < listSize) {
                    listView.getItems().add(getIndex(), acceptanceCriteria);
                } else {
                    listView.getItems().add(acceptanceCriteria);
                }
            }
            event.consume();
        };

        // Called when the dragged item leaves another cell
        EventHandler<DragEvent> mContextDragExit = event -> {
            if (sourceIsAcceptanceCriteria(event)) {
//            System.out.println("exit");
                ((AcceptanceCriteriaListCell) event.getSource()).setStyle(null);
                event.acceptTransferModes(TransferMode.ANY);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                listView.getItems().remove(acceptanceCriteria);
            }
            event.consume();
        };

        // Called when the item is dropped
        EventHandler<DragEvent> mContextDragDropped = event -> {
            if (sourceIsAcceptanceCriteria(event)) {
//            System.out.println("drop");
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                if (getIndex() < listSize) {
//                listView.getItems().add(getIndex(), acceptanceCriteria);
                    if (prevIndex != getIndex()) {
                        undoManager.doCommand(new MoveItemCommand<>(acceptanceCriteria, listView.getItems(), prevIndex, listView.getItems(), getIndex()));
                    }
                } else {
                    undoManager.doCommand(new MoveItemCommand<>(acceptanceCriteria, listView.getItems(), prevIndex,
                            listView.getItems(), listView.getItems().size() - 1));
                }
                event.setDropCompleted(true);
            }
            event.consume();
        };

        // Called when the drag and drop is complete
        EventHandler<DragEvent> mContextDragDone = event -> {
            // When the drag and drop is done, check if it is in the list, if it isn't put it back at its old position
            if (sourceIsAcceptanceCriteria(event)) {
//            System.out.println("done");
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);

                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");

                if (listSize > listView.getItems().size()) {
                    listView.getItems().add(prevIndex, acceptanceCriteria);
                }
            }
            event.consume();
        };

        this.setOnDragDropped(mContextDragDropped);
        this.setOnDragOver(mContextDragOver);
        this.setOnDragEntered(mContextDragEntered);
        this.setOnDragExited(mContextDragExit);
        setCursor(Cursor.HAND);

        this.setOnDragDetected(event -> {
            // We do need this one or onDragDone wont be called
            setCursor(Cursor.CLOSED_HAND);
            getParent().setOnDragDone(mContextDragDone);

            // begin drag ops
            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();
            container.addData("criteria", ac.getCriteria());
            container.addData("state", ac.getState());
            container.addData("listSize", listView.getItems().size());
            content.put(DragContainer.DATA_FORMAT, container);

            if (getIndex() == listView.getSelectionModel().getSelectedIndex()) {
                container.addData("index", listView.getSelectionModel().getSelectedIndex());
                listView.getItems().remove(getIndex());
                listView.getSelectionModel().clearSelection();
                getParent().startDragAndDrop(TransferMode.MOVE).setContent(content);
            }
            event.consume();
        });
    }

    public final class StateButtonHandler implements EventHandler<ActionEvent> {
        private final ObjectProperty<State> state;

        private StateButtonHandler(ImageView imageView, ObjectProperty<State> state, Map<State, Image> images) {
            this.state = state;
            this.state.addListener((observable, oldValue, newValue) -> imageView.setImage(images.get(newValue)));
            imageView.setImage(images.get(state.get()));
        }

        @Override
        public void handle(ActionEvent event) {
            // increment state
            int newIndex = (state.get().ordinal() + 1) % State.values().length;
            final EditCommand<StateButtonHandler, State> command = new EditCommand<>(this, "state", State.values()[newIndex]);
            undoManager.doCommand(command);
        }

        public State getState() {
            return state.get();
        }

        public void setState(State state) {
            this.state.set(state);
        }
    }
}