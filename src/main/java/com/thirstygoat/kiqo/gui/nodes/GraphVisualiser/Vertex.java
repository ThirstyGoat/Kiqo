package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by bradley on 23/09/15.
 */
public class Vertex<T> {
    private T object;
    private DoubleProperty xPos = new SimpleDoubleProperty(0);
    private DoubleProperty yPos = new SimpleDoubleProperty(0);

    private boolean visited = false;

    public Vertex(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public DoubleProperty yPosProperty() {
        return yPos;
    }
    public DoubleProperty xPosProperty() {
        return xPos;
    }

    public void setVisited() {
        visited = true;
    }

    public boolean isVisited() {
        return visited;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "object=" + object +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                '}';
    }
}
