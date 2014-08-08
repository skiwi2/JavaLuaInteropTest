
package com.skiwi.javaluainteroptest.integration;

import com.skiwi.javaluainteroptest.JavaLuaInteropTestController;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

/**
 *
 * @author Frank van Heeswijk
 */
public class RunLuaExceptionWindowTest extends GuiTest {
    @Override
    protected Parent getRootNode() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaLuaInteropTestController.class.getResource("JavaLuaInteropTest.fxml"));
            return fxmlLoader.load();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    @Test(timeout = 3000)
    public void testRunLuaExceptionWindow() {
        TextArea codeTextArea = lookup(find("#splitPane"), "#codeTextArea", TextArea.class);
        Platform.runLater(() -> {
            codeTextArea.clear();
            codeTextArea.appendText("function applyFunction(value)");
            codeTextArea.appendText(System.lineSeparator());
            codeTextArea.appendText("return value + value");
            codeTextArea.appendText(System.lineSeparator());
            codeTextArea.appendText("end");
        });
        
        TextField valueTextField = find("#valueTextField");
        Platform.runLater(() -> valueTextField.setText("test"));
        
        click("#runButton");
        
        waitUntil((Callable<List<String>>)(() -> getWindows().stream().map(window -> ((Stage)window).getTitle()).collect(Collectors.toList())), hasItem("Lua Exception"));
    }
    
    @SuppressWarnings("unchecked")
    public <T> T lookup(final Node parent, final String id, final Class<T> clazz) {
        for (Node node : parent.lookupAll(id)) {
            if (node.getClass().isAssignableFrom(clazz)) {
                return (T)node;
            }
        }
        throw new IllegalArgumentException("Parent " + parent + " doesn't contain node with id " + id);
    }
}
