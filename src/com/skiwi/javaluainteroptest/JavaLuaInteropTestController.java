
package com.skiwi.javaluainteroptest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * FXML Controller class
 *
 * @author Frank van Heeswijk
 */
public class JavaLuaInteropTestController implements Initializable {
    @FXML
    private Tab luaFunctionTab;
    
    @FXML
    private Tab callbackFunctionsTab;
    
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LuaFunctionContentController.class.getResource("LuaFunctionContent.fxml"));
            Node luaFunction = fxmlLoader.load();
            luaFunctionTab.setContent(luaFunction);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CallbackFunctionsContentController.class.getResource("CallbackFunctionsContent.fxml"));
            Node luaFunction = fxmlLoader.load();
            callbackFunctionsTab.setContent(luaFunction);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
