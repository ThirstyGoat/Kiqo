package com.thirstygoat.kiqo;

import java.io.File;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.viewModel.MainController;

/**
 * Main entry point for application
 */
public class Main extends Application {
    private Stage primaryStage;
    private BorderPane root;
    private MainController mainController;

    public static void main( String[] args ) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kiqo");
        this.primaryStage.setMinWidth(900);
        this.primaryStage.setMinHeight(600);
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource("main.fxml"));
        root = loader.load();
        final Scene scene = new Scene(root);


        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });


        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard db = event.getDragboard();
                final boolean success = false;
                for (final File file : db.getFiles()) {
                    mainController.openOrganisation(file);
                }
                // if you want single files
//                File file = db.getFiles().get(0);
//                mainController.dragAndDrop(file);
                event.setDropCompleted(success);
                event.consume();
            }
        });


        primaryStage.setScene(scene);
        primaryStage.show();
        mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);
//        mainController.openOrganisation(new File("/Users/james/Desktop/Kiqo.json"));

    }
}
