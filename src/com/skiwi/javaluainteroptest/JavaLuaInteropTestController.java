
package com.skiwi.javaluainteroptest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

/**
 * FXML Controller class
 *
 * @author Frank van Heeswijk
 */
public class JavaLuaInteropTestController implements Initializable {
    @FXML
    private TextArea codeTextArea;
    
    @FXML
    private TextField valueTextField;
    
    @FXML
    private TextField resultTextField;
    
    private Globals globals;
    
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        setupLayout();
        setupLua();
    }
    
    private void setupLayout() {
        codeTextArea.appendText("-- Always name this function \"applyFunction\"");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("function applyFunction(value)");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("    -- your implementation here");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("end");
    }
    
    private void setupLua() {
        globals = JsePlatform.debugGlobals();
        LuaJC.install(globals);
    }
    
    @FXML
    private void handleRunButtonAction(final ActionEvent actionEvent) {
        runLuaCode();
    }
    
    private void runLuaCode() {
        try {
            LuaValue chunk = globals.load(new StringReader(codeTextArea.getText()), "interopTest");
            chunk.call();
            LuaValue applyFunction = globals.get("applyFunction");
            Varargs applyFunctionResult = applyFunction.invoke(LuaValue.valueOf(valueTextField.getText()));
            resultTextField.setText(applyFunctionResult.tojstring(1));
        } catch (Exception ex) {
            Dialogs.create()
                .title("Lua Exception")
                .style(DialogStyle.NATIVE)
                .showException(ex);
        }
    }
}
