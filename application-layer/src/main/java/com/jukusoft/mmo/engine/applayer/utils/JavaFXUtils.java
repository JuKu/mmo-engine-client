package com.jukusoft.mmo.engine.applayer.utils;

import com.jukusoft.mmo.engine.applayer.config.Config;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Justin on 30.08.2017.
 */
public class JavaFXUtils {

    protected JavaFXUtils () {
        //
    }

    public static void showErrorDialog (String title, String headerText, String content) {
        JavaFXUtils.startJavaFX();

        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(content);

            alert.showAndWait();
        });
    }

    public static void showErrorDialog (String title, String content) {
        JavaFXUtils.startJavaFX();

        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            alert.showAndWait();
        });
    }

    public static void showErrorDialog (String content) {
        String title = Config.get("Error", "windowTitle");

        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            alert.showAndWait();
        });
    }

    public static void showInfoDialog (String title, String content) {
        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            alert.showAndWait();
        });
    }

    public static boolean showConfirmationDialog (String title, String content) {
        AtomicBoolean b = new AtomicBoolean(false);

        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent()) {
                return;
            }

            b.set(result.get() == ButtonType.OK);
        });

        return b.get();
    }

    public static void showExceptionDialog (String title, String headerText, String content, Throwable e) {
        //Source: http://code.makery.ch/blog/javafx-dialogs-official/

        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            showExceptionDlg(alert, content, e);
        });
    }

    public static void showExceptionDialog (String title, String content, Throwable e) {
        //Source: http://code.makery.ch/blog/javafx-dialogs-official/

        ThreadUtils.executeInJavaFXThreadAndWait(() -> {
            JavaFXUtils.startJavaFX();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            showExceptionDlg(alert, content, e);
        });
    }

    private static void showExceptionDlg (Alert alert, String content, Throwable e) {
        JavaFXUtils.startJavaFX();

        alert.setContentText(content);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        //set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void startJavaFX () {
        JFXPanel fxPanel = new JFXPanel();
        //Application.launch(JavaFXApplication.class);
    }

    public static class JavaFXApplication extends Application {

        public JavaFXApplication () {
            //
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            //
        }

    }

}
