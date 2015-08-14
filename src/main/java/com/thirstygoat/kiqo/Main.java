package com.thirstygoat.kiqo;

import java.io.File;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatDialog;

/**
 * Main entry point for application
 */
public class Main extends Application {
    private static File file = null;
    private Stage primaryStage;
    private BorderPane root;
    private MainController mainController;

    public static void main(String[] args) {
        Level level = Level.OFF;
        if (args.length > 0 ) {
            for (String arg : args) {
                if (arg.equals("-v")) {
                    level = Level.ALL;
                } else if (arg.matches("(.*)\\.json")) {
                    file = new File(arg);
                    if (!file.exists()) {
                        System.err.println("Invalid file path");
                        System.exit(1);
                    }
                }
            }
        }
        Main.setupLogging(level);

        Application.launch(args);
    }

    /**
     * Configure default logging behaviour for all classes in **this package** to print to stdout with a custom formatter, including intelligent string formatting.
     * @param level minimum logging level displayed
     */
    private static void setupLogging(Level level) {
        // set up logging for this package
        final Logger logger = Logger.getLogger(Main.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        final Formatter formatter = new SimpleFormatter() {
            /**
             * If record has parameters, uses record's original message as a format string.
             * If not, default behaviour.
             */
            @Override
            public String formatMessage(LogRecord record) {
                final String message;
                if (record.getParameters() == null) {
                    message = super.formatMessage(record);
                } else {
                    message = String.format(record.getMessage(), record.getParameters());
                }
                return message;
            }

            /**
             * Format on one-line (except throwable, which is on next)
             */
            @Override
            public String format(LogRecord record) {
                final Object thrown = (record.getThrown() == null) ? "" : record.getThrown();
                return record.getLevel() + ": " +
                        formatMessage(record) +
                        " (from " + record.getSourceClassName() + "#" + record.getSourceMethodName() + " " + new Date(record.getMillis()).toString() + ")\n" +
                        thrown;
            }
        };
        final Handler handler = new StreamHandler(System.out, formatter) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                super.flush(); // display immediately
            }
        };
        handler.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);

        logger.log(Level.INFO, "Logger %s successfully started", logger.getName());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kiqo");
        this.primaryStage.setMinWidth(1100);
        this.primaryStage.setMinHeight(650);
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource("main.fxml"));
        root = loader.load();
        final Scene scene = new Scene(root);

        scene.setOnDragOver(event -> {
            final Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        scene.setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();
            final boolean success = false;
            if (db.getFiles().size() > 1) {
                GoatDialog.showAlertDialog(primaryStage, "Prohibited Operation", "Not allowed.",
                        "Drag and drop only supports individual files.");
            } else {
                final File file = db.getFiles().get(0);
                mainController.openOrganisation(file);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
        mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);
        if (file != null && file.exists()) {
            mainController.openOrganisation(file);
        }
    }
}
