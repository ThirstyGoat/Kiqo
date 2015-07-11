package com.thirstygoat.kiqo.viewModel;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
            final BorderPane borderPane = new BorderPane();
//
            final Node handle = new Label("-");
            borderPane.setLeft(handle);
            
            initialiseDragAndDrop(item);

            Text criteria = new Text();
            criteria.textProperty().bind(item.criteria);
            criteria.wrappingWidthProperty().bind(listView.widthProperty().subtract(130));
            borderPane.setCenter(criteria);

            final ImageView imageView = new ImageView();
            Button stateButton = new Button("", imageView);
            
            stateButton.setOnAction(new StateButtonHandler(imageView, item.state));
            borderPane.setRight(stateButton);

            /* Combobox
            ObservableList<String> states = FXCollections.observableArrayList(AcceptanceCriteria.State.getStringValues());
            ChoiceBox<String> state = new ChoiceBox<>(states);
            state.setMaxWidth(85);
            state.valueProperty().addListener((observable, oldValue, newValue) -> {
                item.setState(AcceptanceCriteria.State.getEnum(newValue));
            });
            state.valueProperty().setValue(item.getState().toString());
            borderPane.setRight(state);
            */

            /* Toggle group
//            ToggleGroup state = new ToggleGroup();
//
//            ToggleButton accept = new ToggleButton("Y");
//            accept.setToggleGroup(state);
//
//            ToggleButton reject = new ToggleButton("N");
//            reject.setToggleGroup(state);
//
//            ToggleButton none = new ToggleButton("-");
//            none.setToggleGroup(state);
//            none.setSelected(true);
//
//            HBox p = new HBox();
//            p.getChildren().addAll(accept, none, reject);
//            borderPane.setRight(p);
*/
            /* Toggle group radio buttons
//            ToggleGroup state = new ToggleGroup();
//
//            RadioButton accept = new RadioButton();
//            accept.setToggleGroup(state);
//
//            RadioButton reject = new RadioButton();
//            reject.setToggleGroup(state);
//
//            RadioButton none = new RadioButton();
//            none.setToggleGroup(state);
//            none.setSelected(true);
//
//            HBox p = new HBox();
//            p.getChildren().addAll(accept, none, reject);
//            borderPane.setRight(p);
*/
            setGraphic(borderPane);
        } else {
            // clear
            setGraphic(null);
        }
        super.updateItem(item, empty);
    }
    
    private Node initialiseDragAndDrop(AcceptanceCriteria ac) {
        Label handle = new Label("-");

        // Called when the dragged item is over another cell
        EventHandler<DragEvent> mContextDragOver = new EventHandler<DragEvent>() {
            // dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                // hard coding the auto-scroll
                // TODO get rid of hard coding 178 and 375 values
                int buffer = 20;
                if (event.getSceneY() < 178 + buffer) {
                    int scrollTo = Integer.max(0, getIndex() - 1);
                    listView.scrollTo(scrollTo);
                } else if (event.getSceneY() > 375 - buffer) {
                    int scrollTo = Integer.max(0, getIndex() + 1);
                    listView.scrollTo(scrollTo);
                }
                event.consume();
            }
        };

        // Called when the dragged item enters another cell
        EventHandler<DragEvent> mContextDragEntered = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                listView.getItems().add(getIndex(), acceptanceCriteria);
                event.consume();
            }
        };

        // Called when the dragged item leaves another cell
        EventHandler<DragEvent> mContextDragExit = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);
                final int index = Math.min(getIndex(), listView.getItems().size() - 1);
                listView.getItems().add(index, acceptanceCriteria);
                event.setDropCompleted(true);
                event.consume();
            }
        };

        // Called when the drag and drop is complete
        EventHandler<DragEvent> mContextDragDone = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                // When the drag and drop is done, check if it is in the list, if it isn't put it back at its old position
                AcceptanceCriteria acceptanceCriteria = getAcceptanceCriteria(event);

                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                if (!listView.getItems().contains(acceptanceCriteria)) {
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
                container.addData("index", listView.getSelectionModel().getSelectedIndex());
                content.put(DragContainer.DATA_FORMAT, container);
                listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
                getParent().startDragAndDrop(TransferMode.ANY).setContent(content);
                event.consume();
            }
        });
        
        return handle;
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
