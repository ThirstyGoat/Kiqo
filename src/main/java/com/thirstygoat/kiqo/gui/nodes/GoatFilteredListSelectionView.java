package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.SegmentedButton;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Bradley Kirwan on 7/08/15.
 */
public class GoatFilteredListSelectionView<T> extends ListView<T> { // TODO this doesn't actually act like a listview
    private VBox mainView;
    private final GoatFilteredListSelectionViewSkin skin;
    private final ListProperty<T> sourceItems;
    private final ListProperty<T> targetItems;
    private final ObservableList<T> allItems;
    private ObjectProperty<Callback<T, Node>> targetCellGraphicFactory = new SimpleObjectProperty<>();
    private ObjectProperty<Callback<T, Node>> sourceCellGraphicFactory = new SimpleObjectProperty<>();
    private TextField textField;
    private ListView<T> listView;
    private ObjectProperty<Node> header = new SimpleObjectProperty<>();
    private ObjectProperty<Node> footer = new SimpleObjectProperty<>();

    private ObjectProperty<SHOWING> showing = new SimpleObjectProperty<>();

    public GoatFilteredListSelectionView() {
        super();
        skin = new GoatFilteredListSelectionViewSkin(this) {
            {
                mainView = getMainView();
                textField = getTextField();
                listView = getListView();
            }
        };

        setSkin(skin);
        mainView = new VBox();

        sourceItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        targetItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        allItems = FXCollections.observableArrayList();

        createSkin();
        setDefaultCellFactory();
        bindShownItems();

        allItems.addListener((ListChangeListener<T>) c -> {
            String value = textField.getText();
            textField.setText(value + " ");
            textField.setText(value);
        });

        Predicate<List<? extends Item>> newItems =
                list -> (sourceItems.isEmpty() || targetItems.isEmpty())
                        && list.containsAll(sourceItems)
                        && list.containsAll(targetItems);

        ListChangeListener<T> refresh = c -> {
            SHOWING initial = showing.get();
            showing.set(SHOWING.Unselected);
            showing.set(SHOWING.Selected);
            showing.set(initial);
        };

        sourceItems.addListener(refresh);
        targetItems.addListener(refresh);
    }

    public Node getFooter() {
        return footer.get();
    }

    public void setFooter(Node footer) {
        this.footer.set(footer);
    }

    public ObjectProperty<Node> footerProperty() {
        return footer;
    }

    public Node getHeader() {
        return header.get();
    }

    public void setHeader(Node header) {
        this.header.set(header);
    }

    public ObjectProperty<Node> headerProperty() {
        return header;
    }

    public Callback<T, Node> getTargetCellGraphicFactory() {
        return targetCellGraphicFactory.get();
    }

    public void setTargetCellGraphicFactory(Callback<T, Node> cellFactory) {
        targetCellGraphicFactory.set(cellFactory);
    }

    private void bindShownItems() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear out all items first
            ObservableList<T> shownItems = FXCollections.observableArrayList();

            String regex = Pattern.quote(newValue.toLowerCase());
            allItems.forEach(t -> {
                if (t.toString().toLowerCase().matches(".*" + regex + ".*")) { // TODO toString
                    shownItems.add(t);
                }
            });
            listView.setItems(shownItems);
        });
    }

    public ObservableList<T> getTargetItems() {
        return targetItems.get();
    }

    public void setTargetItems(ObservableList<T> targetItems) {
        allItems.removeAll(getTargetItems());
        allItems.addAll(targetItems);
        targetItemsProperty().set(targetItems);
    }

    public ListProperty<T> targetItemsProperty() {
        return targetItems;
    }

    public ObservableList<T> getSourceItems() {
        return sourceItems.get();
    }

    public void setSourceItems(ObservableList<T> sourceItems) {
        allItems.removeAll(getSourceItems());
        allItems.addAll(sourceItems);
        sourceItemsProperty().set(sourceItems);
    }

    public ListProperty<T> sourceItemsProperty() {
        return sourceItems;
    }

    /**
     * Creates the default layout for the GoatFilteredListSelectionView
     */
    private void createSkin() {
        textField = new TextField();
        textField.setMinHeight(30);
        textField.getStyleClass().add("filtered-list-view-text-field");
        textField.setPromptText("Type here to filter list...");

        listView = new ListView<>();
        listView.getStyleClass().add("filtered-list-view");
        listView.setItems(allItems);

        mainView.setVgrow(listView, Priority.ALWAYS);
        ToggleButton allToggleButton = new ToggleButton(SHOWING.All.toString());
        ToggleButton selectedToggleButton = new ToggleButton(SHOWING.Selected.toString());
        ToggleButton unSelectedToggleButton = new ToggleButton(SHOWING.Unselected.toString());
        SegmentedButton showingSegmentedButton =
                new SegmentedButton(allToggleButton, selectedToggleButton, unSelectedToggleButton);

        HBox headerContainer = new HBox();
        headerContainer.setPadding(new Insets(0, 0, 5, 0));

        BorderPane footerContainer = new BorderPane();
        footerContainer.setPadding(new Insets(5, 0, 0, 0));
        footerContainer.setLeft(showingSegmentedButton);

        mainView.getChildren().addAll(headerContainer, textField, listView, footerContainer);
        getChildren().add(mainView);

        showingSegmentedButton.setStyle("-fx-font-size: 11px");
        showingSegmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == allToggleButton) {
                showing.set(SHOWING.All);
            } else if (newValue == selectedToggleButton) {
                showing.set(SHOWING.Selected);
            } else if (newValue == unSelectedToggleButton) {
                showing.set(SHOWING.Unselected);
            } else {
                oldValue.setSelected(true);
            }
        });
        allToggleButton.setSelected(true);

        // Set up binding so that allItems contains only items depending on the showingSegmentedButton
        showing.addListener((observable, oldValue, newValue) -> {
            allItems.clear();
            if (newValue == SHOWING.All) {
                allItems.addAll(targetItems.get());
                allItems.addAll(sourceItems.get());
            } else if (newValue == SHOWING.Selected) {
                allItems.addAll(targetItems.get());
            } else if (newValue == SHOWING.Unselected) {
                allItems.addAll(sourceItems);
            }
            allItems.sort((i1, i2) -> i1.toString().compareTo(i2.toString())); //TODO
            // Trigger refresh of shown items by firing change event on the filter text field
            String value = textField.getText();
            textField.setText(value + " ");
            textField.setText(value);
        });

        headerProperty().addListener((observable, oldValue, newValue) -> {
            headerContainer.getChildren().clear();
            headerContainer.getChildren().add(newValue);
        });

        footerProperty().addListener((observable, oldValue, newValue) -> {
            footerContainer.setRight(newValue);
        });
//
//        mainView.setStyle("-fx-border-color: orange");
//        setStyle("-fx-border-color: pink");
//        headerContainer.setStyle("-fx-border-color: red");
//        footerContainer.setStyle("-fx-border-color: blue");
//        textField.setStyle("-fx-border-color: black");
//        listView.setStyle("-fx-border-color: yellow");
    }

    private void setDefaultCellFactory() {
        Callback<T, Node> defaultCellFactory = param -> {
            Label label = new Label();
            label.textProperty().bind(Bindings.createStringBinding(param::toString)); //TODO toString stringProperty
            return label;
        };

        setSourceCellGraphicFactory(defaultCellFactory);
        setTargetCellGraphicFactory(defaultCellFactory);

        listView.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                if (item != null && !empty) {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(isInTargetList(item));

                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            // If the item is now checked, we have to add it to the target items, remove from source
                            getSourceItems().remove(item);
                            getTargetItems().add(item);
                        } else {
                            getTargetItems().remove(item);
                            getSourceItems().add(item);
                        }
                    });

                    HBox hBox = new HBox();
                    hBox.getChildren().add(checkBox);

                    ObjectProperty<Node> innerCellGraphicProperty = getInnerCellGraphic(item, checkBox.selectedProperty());
                    Node innerCellGraphic = innerCellGraphicProperty.get();
                    HBox.setHgrow(innerCellGraphic, Priority.ALWAYS);
                    hBox.getChildren().add(innerCellGraphic);

                    innerCellGraphicProperty.addListener((observable, oldValue, newValue) -> {
                        HBox.setHgrow(newValue, Priority.ALWAYS);
                        hBox.getChildren().remove(oldValue);
                        hBox.getChildren().add(newValue);
                    });

                    hBox.setOnMousePressed(event -> {
                        if (event.getClickCount() == 2) {
                            checkBox.setSelected(!checkBox.isSelected());
                        }
                    });

                    setText("");
                    setGraphic(hBox);
                } else {
                    setGraphic(null);
                    textProperty().unbind();
                    setText("");
                }
                super.updateItem(item, empty);
            }
        });
    }

    private ObjectProperty<Node> getInnerCellGraphic(T item, BooleanProperty isTarget) {
        ObjectProperty<Node> innerCellGraphic = new SimpleObjectProperty<>();

        innerCellGraphic.bind(Bindings.createObjectBinding(() -> {
            if (isTarget.get()) {
                return getTargetCellGraphicFactory().call(item);
            }
            return getSourceCellGraphicFactory().call(item);
        }, isTarget));

        return innerCellGraphic;
    }

    private boolean isInTargetList(T item) {
        return getTargetItems().contains(item);
    }

    public Callback<T, Node> getSourceCellGraphicFactory() {
        return sourceCellGraphicFactory.get();
    }

    public void setSourceCellGraphicFactory(Callback<T, Node> sourceCellGraphicFactory) {
        this.sourceCellGraphicFactory.set(sourceCellGraphicFactory);
    }

    /**
     * 
     * @return Control which semantically symbolises the input (to be used for validation visualisation).
     */
    public Control getControl() {
        return textField;
    }

    private enum SHOWING {
        All,
        Selected,
        Unselected
    }
}
