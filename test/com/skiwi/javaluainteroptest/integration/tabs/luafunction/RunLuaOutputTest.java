
package com.skiwi.javaluainteroptest.integration.tabs.luafunction;

import com.skiwi.javaluainteroptest.JavaLuaInteropTestController;
import static com.skiwi.javaluainteroptest.TestFXUtils.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import static org.loadui.testfx.GuiTest.*;

/**
 *
 * @author Frank van Heeswijk
 */
public class RunLuaOutputTest extends GuiTest {
    @Override
    protected Parent getRootNode() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaLuaInteropTestController.class.getResource("JavaLuaInteropTest.fxml"));
            return fxmlLoader.load();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    @Test
    public void testRunLuaOutput() {
        TabPane tabPane = find("#tabPane");
        tabPane.getSelectionModel().select(lookupTab(tabPane, "Lua Function"));
        
        TextArea codeTextArea = lookupWithinNode(find("#splitPane"), "#codeTextArea", TextArea.class);
        Platform.runLater(() -> {
            codeTextArea.clear();
            codeTextArea.appendText("function applyFunction(value)");
            codeTextArea.appendText(System.lineSeparator());
            codeTextArea.appendText("    print(value + 1)");
            codeTextArea.appendText(System.lineSeparator());
            codeTextArea.appendText("end");
        });
        
        TextField valueTextField = find("#valueTextField");
        Platform.runLater(() -> valueTextField.setText("5"));
        
        click("#runButton");
        
        TextArea outputTextArea = lookupWithinNode(find("#splitPane"), "#outputTextArea", TextArea.class);
        outputTextArea.clear();
        waitUntil(outputTextArea, textArea -> textArea.getText().contains("6"), 3);
    }
}
