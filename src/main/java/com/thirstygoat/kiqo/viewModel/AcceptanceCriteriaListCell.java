package com.thirstygoat.kiqo.viewModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AcceptanceCriteriaListCell extends ListCell<AcceptanceCriteria> {

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
            final Node handle = createHandle(item);
            borderPane.setLeft(handle);

            Text criteria = new Text();
            criteria.textProperty().bind(item.criteria);
            criteria.wrappingWidthProperty().bind(listView.widthProperty().subtract(130));
            borderPane.setCenter(criteria);

            // Place holder
//            Text state = new Text("PASS");
//            state.setTextAlignment(TextAlignment.RIGHT);
//            borderPane.setRight(state);

            // Combobox
            ObservableList<String> states = FXCollections.observableArrayList(AcceptanceCriteria.State.getStringValues());
            ChoiceBox<String> state = new ChoiceBox<>(states);
            state.setMaxWidth(85);
            state.valueProperty().addListener((observable, oldValue, newValue) -> {
                item.setState(AcceptanceCriteria.State.getEnum(newValue));
            });
            state.valueProperty().setValue(item.getState().toString());
            borderPane.setRight(state);


            // Toggle group
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

            // Toggle group radio buttons
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

            setGraphic(borderPane);
        } else {
            // clear
            setGraphic(null);
        }
        super.updateItem(item, empty);
    }

    private Node createHandle(AcceptanceCriteria ac) {
        Label handle = new Label("-");

        // Called when the dragged item is over another cell
        EventHandler<DragEvent> mContextDragOver = new EventHandler<DragEvent>() {
            // dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
//                relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));
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
                listView.getItems().add(getIndex(), acceptanceCriteria);
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
//                listView.getItems().remove(getUserData())); // TODO remove AC
//                dragOffset = new Point2D(event.getX(), event.getY());
//                relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

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

    private AcceptanceCriteria getAcceptanceCriteria(DragEvent event) {
        AcceptanceCriteria acceptanceCriteria = new AcceptanceCriteria(
                ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("criteria")
        );
        AcceptanceCriteria.State state = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("state");
        acceptanceCriteria.setState(state);
        return acceptanceCriteria;
    }

    private void relocateToPoint(Point2D p) {
        // relocates the object to a point that has been converted to scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);
        relocate ((int) (localCoords.getX() - dragOffset .getX()), (int) (localCoords.getY() - dragOffset.getY()));
    }
    
    private void doStuff(AcceptanceCriteria ac, Point2D p) {
        System.out.println(ac.getCriteria() + " moved to " + p.toString());
    }
}
