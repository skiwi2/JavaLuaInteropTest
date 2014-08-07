
package com.skiwi.javaluainteroptest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.luaj.vm2.Globals;
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
    private TextArea outputTextArea;
    
    @FXML
    private TextField valueTextField;
    
    @FXML
    private TextField resultTextField;
    
    private Globals globals;
    
    private ExecutorService luaService;
    
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
        luaService = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        
        globals = JsePlatform.debugGlobals();
        LuaJC.install(globals);
        
        globals.STDOUT = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                String text = new String(new int[]{b}, 0, 1);
                Platform.runLater(() -> outputTextArea.appendText(text));
            }
        });
        globals.STDERR = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                String text = new String(new int[]{b}, 0, 1);
                Platform.runLater(() -> outputTextArea.appendText(text));
            }
        });
    }
    
    @FXML
    private void handleRunButtonAction(final ActionEvent actionEvent) {
        Platform.runLater(outputTextArea::clear);
        luaService.submit(this::runLuaCode);
    }
    
    private void runLuaCode() {
        try {
            LuaValue chunk = globals.load(new StringReader(codeTextArea.getText()), "interopTest");
            chunk.call();
            LuaValue applyFunction = globals.get("applyFunction");
            Varargs applyFunctionResult = applyFunction.invoke(LuaValue.valueOf(valueTextField.getText()));
            Platform.runLater(() -> resultTextField.setText(applyFunctionResult.tojstring(1)));
        } catch (Exception ex) {
            Dialogs.create()
                .title("Lua Exception")
                .style(DialogStyle.NATIVE)
                .showException(ex);
        }
    }
}
