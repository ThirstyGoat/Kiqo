package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.gui.nodes.GoatTree.DirectedData;
import com.thirstygoat.kiqo.gui.nodes.GoatTree.HierarchicalData;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by bradley on 22/09/15.
 */
public class GoatTreeTest extends Application {
    public DirectedData<Point> getData() {
        Point point1 = new Point("1");
        Point point2 = new Point("2");
        Point point3 = new Point("3");
        Point point4 = new Point("4");
        Point point5 = new Point("5");
        Point point6 = new Point("6");

        point1.getDescendants().add(point2);
        point1.getDescendants().add(point3);

        point2.getDescendants().add(point4);
        point3.getDescendants().add(point5);
        point3.getDescendants().add(point6);

        return point1;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create vertex list




//        GoatTree<Point> goatTree = new GoatTree<>();
//
//        goatTree.setNodeFactory(point -> {
//            return new Rectangle(100, 100, Color.GAINSBORO);
//        });
//
//        List<HierarchicalData<Point>> points = new ArrayList<>();
//        points.add(getData());
//        points.add(getData());
//        points.add(getData());
//        goatTree.setRoot(points);
//
//        Scene scene = new Scene(goatTree);
//        primaryStage.setScene(scene);
//        primaryStage.setWidth(800);
//        primaryStage.setHeight(600);
//        primaryStage.show();
    }

}

class Point implements DirectedData<Point> {
    String label;
    ObservableList<DirectedData<Point>> children = FXCollections.observableArrayList();

    public Point(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public Point get() {
        return this;
    }

    @Override
    public List<DirectedData<Point>> getDescendants() {
        return children;
    }
}
