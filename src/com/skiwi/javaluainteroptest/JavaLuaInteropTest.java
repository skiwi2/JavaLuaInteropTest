
package com.skiwi.javaluainteroptest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Frank van Heeswijk
 */
public class JavaLuaInteropTest extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaLuaInteropTestController.class.getResource("JavaLuaInteropTest.fxml"));
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root, 1024, 768);
        
        primaryStage.setTitle("Java Lua Interopability Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
