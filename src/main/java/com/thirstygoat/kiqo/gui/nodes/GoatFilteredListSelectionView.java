package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.regex.Pattern;

/**
 * Created by Bradley Kirwan on 7/08/15.
 */
public class GoatFilteredListSelectionView<T extends Item> extends VBox {
    private final ObjectProperty<ObservableList<T>> sourceItems;
    private final ObjectProperty<ObservableList<T>> targetItems;
    private final ObservableList<T> allItems;
    private ObjectProperty<Callback<T, Node>> targetCellGraphicFactory = new SimpleObjectProperty<>();
    private ObjectProperty<Callback<T, Node>> sourceCellGraphicFactory = new SimpleObjectProperty<>();
    private TextField textField;
    private ListView<T> listView;
    private ObjectProperty<Node> header = new SimpleObjectProperty<>();
    private ObjectProperty<Node> footer = new SimpleObjectProperty<>();

    public GoatFilteredListSelectionView() {
        sourceItems = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        targetItems = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        allItems = FXCollections.observableArrayList();

        createSkin();
        setDefaultCellFactory();
        bindShownItems();
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
                if (t.getShortName().toLowerCase().matches(".*" + regex + ".*")) {
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

    public ObjectProperty<ObservableList<T>> targetItemsProperty() {
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

    public ObjectProperty<ObservableList<T>> sourceItemsProperty() {
        return sourceItems;
    }

    /**
     * Creates the default layout for the GoatFilteredListSelectionView
     */
    private void createSkin() {
        textField = new TextField();
        textField.setStyle("-fx-border-radius: 4 4 0 0; -fx-background-radius: 4 4 0 0;");
        textField.setPromptText("Type here to filter list...");
        listView = new ListView<>();
        listView.setStyle("-fx-border-radius: 0 0 4 4; -fx-background-radius: 0 0 4 4;");
        listView.setItems(allItems);
        VBox.setVgrow(listView, Priority.ALWAYS);

        HBox headerContainer = new HBox();
        headerContainer.setPadding(new Insets(0, 0 ,5 ,0));
        HBox footerContainer = new HBox();
        footerContainer.setPadding(new Insets(5, 0, 0, 0));

        getChildren().addAll(headerContainer, textField, listView, footerContainer);
        setPrefHeight(USE_COMPUTED_SIZE);

        headerProperty().addListener((observable, oldValue, newValue) -> {
            headerContainer.getChildren().clear();
            headerContainer.getChildren().add(newValue);
        });

        footerProperty().addListener((observable, oldValue, newValue) -> {
            footerContainer.getChildren().clear();
            footerContainer.getChildren().add(newValue);
        });
    }

    private void setDefaultCellFactory() {
        Callback<T, Node> defaultCellFactory = param -> {
            Label label = new Label();
            label.textProperty().bind(param.shortNameProperty());
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
}
