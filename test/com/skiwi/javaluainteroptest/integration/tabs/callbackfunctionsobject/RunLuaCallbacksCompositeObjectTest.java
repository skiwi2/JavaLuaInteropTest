
package com.skiwi.javaluainteroptest.integration.tabs.callbackfunctionsobject;

import com.skiwi.javaluainteroptest.CallbackFunctionsCompositeObjectContentController.TableEntry;
import com.skiwi.javaluainteroptest.JavaLuaInteropTestController;
import static com.skiwi.javaluainteroptest.TestFXUtils.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

/**
 *
 * @author Frank van Heeswijk
 */
public class RunLuaCallbacksCompositeObjectTest extends GuiTest {
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
    public void testRunLuaCallbacksCompositeObject() {
        TabPane tabPane = find("#tabPane");
        Tab expectedTab = lookupTab(tabPane, "Callback Functions Composite Object");
        Platform.runLater(() -> tabPane.getSelectionModel().select(expectedTab));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        TextArea codeTextArea = lookupWithinNode(find("#splitPane"), "#codeTextArea", TextArea.class);
        Platform.runLater(() -> {
            codeTextArea.clear();
            codeTextArea.appendText("function initCallbacks()");
            codeTextArea.appendText(System.lineSeparator());
            codeTextArea.appendText("    callbackStore:addCallback(\"test\", function(supplier) return supplier:get() end)");
            codeTextArea.appendText(System.lineSeparator());
            codeTextArea.appendText("end");
        });
        
        TextField keyTextField = find("#keyTextField");
        TextField valueTextField = find("#valueTextField");
        Platform.runLater(() -> {
            keyTextField.setText("test");
            valueTextField.setText("random");
        });
        click("#addButton");
        
        TableView<TableEntry> callbackTableView = find("#callbackTableView");
        waitUntil(callbackTableView, tableView -> tableView.getItems().size() == 1 && tableView.getItems().get(0).getResult().equals("[random]"), 3);
    }
}
