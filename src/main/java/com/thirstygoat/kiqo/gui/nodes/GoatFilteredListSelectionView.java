package com.thirstygoat.kiqo.gui.nodes;

import java.util.regex.Pattern;

import org.controlsfx.control.SegmentedButton;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

/**
 * NB: This doesn't actually act like a ListView, beware.
 * Created by Bradley Kirwan on 7/08/15.
 * @param <T> type of list elements
 */
public class GoatFilteredListSelectionView<T> extends ListView<T> {
    private final GoatFilteredListSelectionViewSkin<T> skin;
    
    /** All items regardless of their state (availability, filtering or selectedness). */
    private final ListProperty<T> allItems;
    /** Selected items (i.e. those shown in DisplayMode.SELECTED with no filter). */
    private final ListProperty<T> selectedItems;
    /** Items available to be filtered (depends on DisplayMode). */
    private final ListProperty<T> availableItems;
    
    private ObjectProperty<Callback<T, Node>> targetCellGraphicFactory = new SimpleObjectProperty<>();
    private ObjectProperty<Callback<T, Node>> sourceCellGraphicFactory = new SimpleObjectProperty<>();
    private TextField textField;
    private ListView<T> listView;
    private SegmentedButton showingSegmentedButton;
    private ObjectProperty<Node> header = new SimpleObjectProperty<>();
    private ObjectProperty<Node> footer = new SimpleObjectProperty<>();
    private ObjectProperty<DisplayMode> showing = new SimpleObjectProperty<>();
    private Callback<T, StringProperty> stringPropertyCallback;
    private BooleanProperty focusedProperty = new SimpleBooleanProperty();

    public GoatFilteredListSelectionView() {
        super();
        setFocusTraversable(false);
        
        stringPropertyCallback = t -> new SimpleStringProperty(t != null ? t.toString() : "");
        skin = new GoatFilteredListSelectionViewSkin<T>(this);

        setSkin(skin);
        textField = skin.getTextField();
        listView = skin.getListView();

        allItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
        availableItems = new SimpleListProperty<>(FXCollections.observableArrayList());

    	ObservableList<T> items = FXCollections.observableArrayList();
        availableItems.bind(Bindings.createObjectBinding(() -> {
        	items.clear();
        	items.addAll(allItems);
        	items.removeAll(selectedItems);
        	return items;
        }, allItems, selectedItems));

        createSkin();
        setDefaultCellFactory();
        bindShownItems();

        availableItems.addListener((ListChangeListener<T>) c -> {
            refilter();
        });

        ListChangeListener<T> refresh = c -> {
            DisplayMode initial = showing.get();
            showing.set(DisplayMode.UNSELECTED);
            showing.set(DisplayMode.SELECTED);
            showing.set(initial);
        };

        allItems.addListener(refresh);
        selectedItems.addListener(refresh);
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

    public void setStringPropertyCallback(Callback<T, StringProperty> stringPropertyCallback) {
        this.stringPropertyCallback = stringPropertyCallback;
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
            availableItems.forEach(t -> {
                if (stringPropertyCallback.call(t).get().toLowerCase().matches(".*" + regex + ".*")) {
                	shownItems.add(t);
                }
            });
            listView.setItems(shownItems);
        });
    }
    
    public void resetFilter() {
        textField.textProperty().set("");
        Platform.runLater(textField::requestFocus);
    }

    @Deprecated
    public ObservableList<T> getTargetItems() {
        return selectedItems.get();
    }

    @Deprecated
    public void setTargetItems(ObservableList<T> targetItems) {
        targetItemsProperty().set(targetItems);
    }

    @Deprecated
    public ListProperty<T> targetItemsProperty() {
        return selectedItems;
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
        listView.setItems(availableItems);

        VBox.setVgrow(listView, Priority.ALWAYS);
        ToggleButton allToggleButton = new ToggleButton(DisplayMode.ALL.toString());
        ToggleButton selectedToggleButton = new ToggleButton(DisplayMode.SELECTED.toString());
        ToggleButton unSelectedToggleButton = new ToggleButton(DisplayMode.UNSELECTED.toString());
        showingSegmentedButton = new SegmentedButton(allToggleButton, selectedToggleButton, unSelectedToggleButton);

        HBox headerContainer = new HBox();

        BorderPane footerContainer = new BorderPane();
        footerContainer.setPadding(new Insets(5, 0, 0, 0));
        footerContainer.setLeft(showingSegmentedButton);

        Pane mainView = skin.getMainView();
        mainView.getChildren().addAll(headerContainer, textField, listView, footerContainer);
        getChildren().add(mainView);

        showingSegmentedButton.setStyle("-fx-font-size: 11px");
        showingSegmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == allToggleButton) {
                showing.set(DisplayMode.ALL);
            } else if (newValue == selectedToggleButton) {
                showing.set(DisplayMode.SELECTED);
            } else if (newValue == unSelectedToggleButton) {
                showing.set(DisplayMode.UNSELECTED);
            } else {
                oldValue.setSelected(true);
            }
        });
        allToggleButton.setSelected(true);

//         Set up binding so that allItems contains only items depending on the showingSegmentedButton
        showing.addListener((observable, oldValue, newValue) -> {
            availableItems.clear();
            if (newValue == DisplayMode.ALL) {
                availableItems.addAll(allItems);
            } else if (newValue == DisplayMode.SELECTED) {
                availableItems.addAll(selectedItems);
            } else if (newValue == DisplayMode.UNSELECTED) {
                availableItems.addAll(allItems);
                availableItems.removeAll(selectedItems);
            }
            availableItems.sort((i1, i2) -> stringPropertyCallback.call(i1).get().compareTo(stringPropertyCallback.call(i2).get()));
            refilter();
        });

        headerProperty().addListener((observable, oldValue, newValue) -> {
            headerContainer.getChildren().clear();
            headerContainer.getChildren().add(newValue);
        });

        footerProperty().addListener((observable, oldValue, newValue) -> {
            footerContainer.setRight(newValue);
        });

        Platform.runLater(() -> {
            focusedProperty.bind(
                    textField.focusedProperty()
                            .or(listView.focusedProperty())
                            .or(allToggleButton.focusedProperty())
                            .or(selectedToggleButton.focusedProperty())
                            .or(unSelectedToggleButton.focusedProperty())
                            .or(mainView.focusedProperty()));
        });
    }

    /**
     * Trigger refresh of shown items by firing change event on the filter text field.
     */
	private void refilter() {
		String value = textField.getText();
		textField.setText(value + " ");
		textField.setText(value);
	}

    private void setDefaultCellFactory() {
        Callback<T, Node> defaultCellFactory = t -> {
            Label label = new Label();
            label.textProperty().bind(stringPropertyCallback.call(t));
            return label;
        };

        setSourceCellGraphicFactory(defaultCellFactory);
        setTargetCellGraphicFactory(defaultCellFactory);

        listView.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(isInTargetList(item));
                    checkBox.setFocusTraversable(false);

                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            // If the item is now checked, we have to add it to the target items
                            selectedItems.add(item);
                        } else {
                            selectedItems.remove(item);
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

    public ReadOnlyBooleanProperty _focusedProperty() {
        return focusedProperty;
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

    public void bindSelectedItems(ListProperty<T> selectedItems) {
		this.selectedItems.bindBidirectional(selectedItems);
	}
	
	public void bindAllItems(ListProperty<T> allItems) {
		this.allItems.bind(allItems);
	}

	private enum DisplayMode {
	    ALL("All"),
	    SELECTED("Selected"),
	    UNSELECTED("Unselected");
		
		private String label;
		
		DisplayMode(String label) {
			this.label = label;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
}
