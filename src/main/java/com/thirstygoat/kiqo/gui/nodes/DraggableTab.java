package com.thirstygoat.kiqo.gui.nodes;

import java.util.HashSet;
import java.util.Set;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.util.FxUtils;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A draggable tab that can optionally be detached from its tab pane and shown
 * in a separate window. This can be added to any normal TabPane, however a
 * TabPane with draggable tabs must *only* have DraggableTabs, normal tabs and
 * DrragableTabs mixed will cause issues!
 * <p>
 * @author Michael Berry (Modified extensively from Michael Berry's original version by Bradley Kirwan)
 */
public class DraggableTab extends Tab {

    private static final Set<TabPane> tabPanes = new HashSet<>();
    private static final Stage markerStage;
    private Label nameLabel;
    private Stage dragStage;
    private boolean detachable;

    static {
        markerStage = new Stage();
        markerStage.initStyle(StageStyle.DECORATED);
        Rectangle dummy = new Rectangle(3, 10, Color.web("#555555"));
        StackPane markerStack = new StackPane();
        markerStack.getChildren().add(dummy);
        markerStage.setScene(new Scene(markerStack));
    }

    public DraggableTab(StringProperty titleProperty) {
        nameLabel = new Label();
        nameLabel.setCursor(Cursor.OPEN_HAND);
        Text dragText = new Text();
        nameLabel.textProperty().bind(titleProperty);
        dragText.textProperty().bind(titleProperty);

        setGraphic(nameLabel);
        detachable = true;
        dragStage = new Stage();
        dragStage.initStyle(StageStyle.UNDECORATED);
        StackPane dragStagePane = new StackPane();
        dragStagePane.setStyle("-fx-background-color:#DDDDDD;");
        StackPane.setAlignment(dragText, Pos.CENTER);
        dragStagePane.getChildren().add(dragText);
        dragStage.setScene(new Scene(dragStagePane));

        setText("");

        nameLabel.setOnMouseDragged(event -> nameLabel.setCursor(Cursor.CLOSED_HAND));

        nameLabel.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                nameLabel.setCursor(Cursor.OPEN_HAND);
                markerStage.hide();
                dragStage.hide();
                if (!t.isStillSincePress()) {
                    Point2D screenPoint = new Point2D(t.getScreenX(), t.getScreenY());
                    TabPane oldTabPane = getTabPane();

                    tabPanes.add(oldTabPane);

                    int oldIndex = oldTabPane.getTabs().indexOf(DraggableTab.this);

                    InsertData insertData = getInsertData(screenPoint);
                    if (insertData != null) {
                        int addIndex = insertData.getIndex();
                        if (oldTabPane == insertData.getInsertPane() && oldTabPane.getTabs().size() == 1)
                            return;

                        oldTabPane.getTabs().remove(DraggableTab.this);
                        if (oldIndex < addIndex && oldTabPane == insertData.getInsertPane())
                            addIndex--;

                        if (addIndex > insertData.getInsertPane().getTabs().size())
                            addIndex = insertData.getInsertPane().getTabs().size();

                        insertData.getInsertPane().getTabs().add(addIndex, DraggableTab.this);
                        insertData.getInsertPane().selectionModelProperty().get().select(addIndex);
                        return;
                    }

                    if (!detachable)
                        return;

                    final Stage newStage = new Stage();
                    final TabPane pane = new TabPane();
                    tabPanes.add(pane);

                    newStage.setOnHiding(t1 -> tabPanes.remove(pane));
                    getTabPane().getTabs().remove(DraggableTab.this);
                    pane.getTabs().add(DraggableTab.this);

                    tabPaneProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != pane)
                            pane.getTabs().remove(DraggableTab.this);
                    });

                    pane.getTabs().addListener((ListChangeListener.Change<? extends Tab> change) -> {
                        if (pane.getTabs().isEmpty()) {
                            newStage.hide();
                        }
                    });

                    BorderPane borderPane = new BorderPane();
                    borderPane.setCenter(pane);
                    Scene scene = new Scene(borderPane);

                    FxUtils.attachKeyShortcuts(scene);

                    scene.getStylesheets().add(this.getClass().getResource("/css/styles.css").toExternalForm());
                    newStage.setScene(scene);
                    newStage.initStyle(StageStyle.DECORATED);
                    newStage.setHeight(getContent().getBoundsInLocal().getHeight() + 50);
                    newStage.setWidth(getContent().getBoundsInLocal().getWidth() + 50);
                    newStage.setX(t.getScreenX());
                    newStage.setY(t.getScreenY());
                    newStage.show();
                    newStage.setOnCloseRequest(event -> getTabPane().getTabs().clear());

                    pane.requestLayout();
                    pane.requestFocus();
                }
            }

        });
    }

    /**
     * Set whether it's possible to detach the tab from its pane and move it to
     * another pane or another window. Defaults to true.
     * <p>
     * @param detachable true if the tab should be detachable, false otherwise.
     */
    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
    }

    private InsertData getInsertData(Point2D screenPoint) {
        for(TabPane tabPane : tabPanes) {
            Rectangle2D tabAbsolute = getAbsoluteRect(tabPane);
            if(tabAbsolute.contains(screenPoint)) {
                int tabInsertIndex = 0;
                if(!tabPane.getTabs().isEmpty()) {
                    Rectangle2D firstTabRect = getAbsoluteRect(tabPane.getTabs().get(0));
                    if(firstTabRect.getMaxY()+60 < screenPoint.getY() || firstTabRect.getMinY() > screenPoint.getY()) {
                        return null;
                    }
                    Rectangle2D lastTabRect = getAbsoluteRect(tabPane.getTabs().get(tabPane.getTabs().size() - 1));
                    if(screenPoint.getX() < (firstTabRect.getMinX() + firstTabRect.getWidth() / 2)) {
                        tabInsertIndex = 0;
                    }
                    else if(screenPoint.getX() > (lastTabRect.getMaxX() - lastTabRect.getWidth() / 2)) {
                        tabInsertIndex = tabPane.getTabs().size();
                    }
                    else {
                        for(int i = 0; i < tabPane.getTabs().size() - 1; i++) {
                            Tab leftTab = tabPane.getTabs().get(i);
                            Tab rightTab = tabPane.getTabs().get(i + 1);
                            if(leftTab instanceof DraggableTab && rightTab instanceof DraggableTab) {
                                Rectangle2D leftTabRect = getAbsoluteRect(leftTab);
                                Rectangle2D rightTabRect = getAbsoluteRect(rightTab);
                                if(betweenX(leftTabRect, rightTabRect, screenPoint.getX())) {
                                    tabInsertIndex = i + 1;
                                    break;
                                }
                            }
                        }
                    }
                }
                return new InsertData(tabInsertIndex, tabPane);
            }
        }
        return null;
    }

    private Rectangle2D getAbsoluteRect(Control node) {
        return new Rectangle2D(node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getX() + node.getScene().getWindow().getX(),
                node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getY() + node.getScene().getWindow().getY(),
                node.getWidth(),
                node.getHeight());
    }

    private Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((DraggableTab) tab).getLabel();
        return getAbsoluteRect(node);
    }

    private Label getLabel() {
        return nameLabel;
    }

    private boolean betweenX(Rectangle2D r1, Rectangle2D r2, double xPoint) {
        double lowerBound = r1.getMinX() + r1.getWidth() / 2;
        double upperBound = r2.getMaxX() - r2.getWidth() / 2;
        return xPoint >= lowerBound && xPoint <= upperBound;
    }

    private static class InsertData {
        private final int index;
        private final TabPane insertPane;

        public InsertData(int index, TabPane insertPane) {
            this.index = index;
            this.insertPane = insertPane;
        }

        public int getIndex() {
            return index;
        }

        public TabPane getInsertPane() {
            return insertPane;
        }
    }
}