package com.thirstygoat.kiqo.viewModel;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;

public class AcceptanceCriteriaListCell extends ListCell<AcceptanceCriteria> {
    private final class StateButtonHandler implements EventHandler<ActionEvent> {
        private static final int IMAGE_SIZE = 20;
        private final ObjectProperty<State> state;
        private final Map<State, Image> images = new HashMap<>();

        private StateButtonHandler(ImageView imageView, ObjectProperty<State> state) {            
            final ClassLoader classLoader = getClass().getClassLoader();
            images.put(State.ACCEPTED, new Image(classLoader.getResourceAsStream("images/acceptedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
            images.put(State.REJECTED, new Image(classLoader.getResourceAsStream("images/rejectedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
            images.put(State.NEITHER, new Image(classLoader.getResourceAsStream("images/noState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
            
            this.state = state;
            this.state.addListener((observable, oldValue, newValue) -> imageView.setImage(images.get(newValue)));
            imageView.setImage(images.get(state.get()));
        }

        @Override
        public void handle(ActionEvent event) {
            // increment state
            int newIndex = (state.get().ordinal() + 1) % State.values().length;
            state.set(State.values()[newIndex]);
        }
    }

    private Point2D dragOffset = new Point2D(0, 0);
    private ListView<AcceptanceCriteria> listView;
    
    public AcceptanceCriteriaListCell(ListView<AcceptanceCriteria> listView) {
        this.listView = listView;
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
            borderPane.setCenter(criteria);

            final ImageView imageView = new ImageView();
            Button stateButton = new Button("", imageView);
            
            stateButton.setOnAction(new StateButtonHandler(imageView, item.state));
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
        EventHandler<DragEvent> mContextDragOver = new EventHandler<DragEvent>() {
            // dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {
//                System.out.println("drag over");
                event.acceptTransferModes(TransferMode.ANY);
                int buffer = 20;
                double yPos = getParent().sceneToLocal(event.getSceneX(), event.getSceneY()).getY();

                if (getIndex() > 0 && getIndex() < listView.getItems().size() - 1) {
                    if (yPos < buffer) {
                        listView.scrollTo(getIndex() - 2);
                    } else if (yPos > listView.getHeight() - buffer) {
                        listView.scrollTo(getIndex() - 3);
                    }
                }
                event.consume();
            }
        };

        // Called when the dragged item enters another cell
        EventHandler<DragEvent> mContextDragEntered = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
//                System.out.println("enter");
                ((AcceptanceCriteriaListCell) event.getSource()).setStyle("-fx-background-color: greenyellow");
                event.acceptTransferModes(TransferMode.ANY);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                if (getIndex() < listSize) {
                    listView.getItems().add(getIndex(), acceptanceCriteria);
                }
                event.consume();
            }
        };

        // Called when the dragged item leaves another cell
        EventHandler<DragEvent> mContextDragExit = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
//                System.out.println("exit");
                ((AcceptanceCriteriaListCell) event.getSource()).setStyle(null);
                event.acceptTransferModes(TransferMode.ANY);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                listView.getItems().remove(acceptanceCriteria);
                event.consume();
            }
        };

        // Called when the item is dropped
        EventHandler<DragEvent> mContextDragDropped = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
//                System.out.println("drop");
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                if (getIndex() < listSize) {
                    listView.getItems().add(getIndex(), acceptanceCriteria);
                }
                event.setDropCompleted(true);
                event.consume();
            }
        };

        // Called when the drag and drop is complete
        EventHandler<DragEvent> mContextDragDone = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                // When the drag and drop is done, check if it is in the list, if it isn't put it back at its old position
//                System.out.println("done");
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);

                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                if (listSize > listView.getItems().size()) {
                    listView.getItems().add(prevIndex, acceptanceCriteria);
                }
            }
        };
        this.setOnDragDropped(mContextDragDropped);
        this.setOnDragOver(mContextDragOver);
        this.setOnDragEntered(mContextDragEntered);
        this.setOnDragExited(mContextDragExit);
        this.setOnDragDetected(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                getParent().setOnDragOver(mContextDragOver);
                getParent().setOnDragDropped(mContextDragDropped);
                getParent().setOnDragDone(mContextDragDone);

                // begin drag ops
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();
                container.addData("criteria", ac.getCriteria());
                container.addData("state", ac.getState());

//                container.addData("index", getIndex());
                container.addData("listSize", listView.getItems().size());
                content.put(DragContainer.DATA_FORMAT, container);
//                listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());

                if (getIndex() == listView.getSelectionModel().getSelectedIndex()) {
                    container.addData("index", listView.getSelectionModel().getSelectedIndex());
                    listView.getItems().remove(getIndex());
                    getParent().startDragAndDrop(TransferMode.ANY).setContent(content);
                }

                event.consume();
            }
        });
    }

    private static AcceptanceCriteria getAcceptanceCriteria(DragEvent event) {
        AcceptanceCriteria acceptanceCriteria = new AcceptanceCriteria(
                ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("criteria")
        );
        AcceptanceCriteria.State state = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("state");
        acceptanceCriteria.setState(state);
        return acceptanceCriteria;
    }
}
