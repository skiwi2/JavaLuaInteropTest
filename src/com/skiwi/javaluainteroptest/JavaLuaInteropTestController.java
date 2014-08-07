
package com.skiwi.javaluainteroptest;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        codeTextArea.appendText("-- Always name this function \"applyFunction\"");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("function applyFunction(value)");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("    -- your implementation here");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("end");
    }    
}
