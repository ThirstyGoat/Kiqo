package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

/**
 * Custom arrow node which has the same functionality as line but looks like an arrow
 */
public class Arrow extends Group {
    private static final double ARROW_HEIGHT = 10;

    private Line line;
    private Polygon arrowHead;
    private DoubleProperty rotation;

    public Arrow() {
        super();
        rotation = new SimpleDoubleProperty();
        line = new Line();
        line.setStartX(0);
        line.setStartY(0);

        arrowHead = new Polygon(0, 0, ARROW_HEIGHT, ARROW_HEIGHT / 2, ARROW_HEIGHT / 2, ARROW_HEIGHT);
        arrowHead.layoutXProperty().bind(line.endXProperty());
        arrowHead.layoutYProperty().bind(line.endYProperty());

        rotation.bind(Bindings.createDoubleBinding(() -> {
            if (line.getEndX() < 0) {
                return 135 - Math.toDegrees(Math.atan(-line.getEndY() / line.getEndX())) + 180;
            } else {
                return 135 - Math.toDegrees(Math.atan(-line.getEndY() / line.getEndX()));
            }
        }, endXProperty(), endYProperty()));

        Platform.runLater(() -> arrowHead.getTransforms().add(new Rotate(rotation.get(), 0, 0)));
        getChildren().addAll(line, arrowHead);
    }

    public DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public DoubleProperty endYProperty() {
        return line.endYProperty();
    }
}
