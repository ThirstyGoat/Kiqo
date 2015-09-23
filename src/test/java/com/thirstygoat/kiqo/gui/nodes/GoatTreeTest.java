//package com.thirstygoat.kiqo.gui.nodes;
//
//import com.thirstygoat.kiqo.gui.nodes.GoatTree.DirectedData;
//import com.thirstygoat.kiqo.gui.nodes.GoatTree.GoatTree;
//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.stage.Stage;
//import org.junit.Ignore;
//
//import java.util.List;
//
///**
// * Created by bradley on 22/09/15.
// */
//
//public class GoatTreeTest extends Application {
//    public static void main(String[] args) {
//        GoatTree<Point> goatTree = new GoatTree<>();
//        goatTree.run();
//    }
//
//    public DirectedData<Point> getData() {
//        Point pointA = new Point("A");
//        Point pointB = new Point("B");
//        Point pointC = new Point("C");
//        Point pointD = new Point("D");
//
//        pointA.getDescendants().add(pointB);
//        pointA.getDescendants().add(pointC);
//        pointA.getDescendants().add(pointD);
//        pointB.getDescendants().add(pointD);
//        pointC.getDescendants().add(pointD);
//
//        return pointA;
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Create vertex list
//
//        GoatTree<Point> goatTree = new GoatTree<>();
//        goatTree.run();
//
//
////        GoatTree<Point> goatTree = new GoatTree<>();
////
////        goatTree.setNodeFactory(point -> {
////            return new Rectangle(100, 100, Color.GAINSBORO);
////        });
////
////        List<HierarchicalData<Point>> points = new ArrayList<>();
////        points.add(getData());
////        points.add(getData());
////        points.add(getData());
////        goatTree.setRoot(points);
////
////        Scene scene = new Scene(goatTree);
////        primaryStage.setScene(scene);
////        primaryStage.setWidth(800);
////        primaryStage.setHeight(600);
////        primaryStage.show();
//    }
//
//}
//
//class Point implements DirectedData<Point> {
//    String label;
//    ObservableList<DirectedData<Point>> children = FXCollections.observableArrayList();
//
//    public Point(String label) {
//        this.label = label;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//
//    @Override
//    public Point get() {
//        return this;
//    }
//
//    @Override
//    public List<DirectedData<Point>> getDescendants() {
//        return children;
//    }
//}
