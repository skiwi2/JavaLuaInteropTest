
package com.skiwi.javaluainteroptest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

/**
 * FXML Controller class
 *
 * @author Frank van Heeswijk
 */
public class CallbackFunctionsObjectContentController implements Initializable {
    @FXML
    private TextArea codeTextArea;
    
    @FXML
    private TextArea outputTextArea;
    
    @FXML
    private TableView<TableEntry> callbackTableView;
    
    @FXML
    private TableColumn<TableEntry, String> keyTableColumn;
    
    @FXML
    private TableColumn<TableEntry, String> valueTableColumn;
    
    @FXML
    private TableColumn<TableEntry, String> resultTableColumn;
    
    @FXML
    private TextField keyTextField;
    
    @FXML
    private TextField valueTextField;
    
    private Globals globals;
    
    private ExecutorService luaService;
    
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        setupLayout();
        setupLua();
    }
    
    private void setupLayout() {
        keyTableColumn.prefWidthProperty().bind(callbackTableView.widthProperty().multiply(0.333d));
        valueTableColumn.prefWidthProperty().bind(callbackTableView.widthProperty().multiply(0.333d));
        resultTableColumn.prefWidthProperty().bind(callbackTableView.widthProperty().multiply(0.333d));
        
        keyTableColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        valueTableColumn.setCellValueFactory(cellData -> {
            StringProperty valueProperty = cellData.getValue().valueProperty();
            valueProperty.addListener((observableValue, oldValue, newValue) -> luaService.submit(this::runLuaCode));
            return valueProperty;
        });
        resultTableColumn.setCellValueFactory(cellData -> cellData.getValue().resultProperty());
        
        valueTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueTableColumn.setOnEditCommit(cellEditEvent -> {
            TableEntry tableEntry = cellEditEvent.getTableView().getItems().get(cellEditEvent.getTablePosition().getRow());
            tableEntry.setValue(cellEditEvent.getNewValue());
            outputTextArea.clear();
        });
        
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            ObservableList<TableEntry> selectedEntriesInTableView = callbackTableView.getSelectionModel().getSelectedItems();
            ObservableList<TableEntry> selectedEntries = FXCollections.observableArrayList();
            selectedEntriesInTableView.forEach(selectedEntries::add);
            FXCollections.copy(selectedEntries, selectedEntriesInTableView);
            selectedEntries.forEach(todoEntry -> {
                callbackTableView.getItems().remove(todoEntry);
                callbackTableView.sort();
            });
            callbackTableView.getSelectionModel().clearSelection();
        });
        ContextMenu contextMenu = new ContextMenu(deleteMenuItem);
        callbackTableView.setContextMenu(contextMenu);
        
        callbackTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        codeTextArea.appendText("-- Always name this function \"initCallbacks\"");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("function initCallbacks()");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("    -- Put your callbackStore:addCallback(key:string, function:function(supplier<string> -> string)) calls here");
        codeTextArea.appendText(System.lineSeparator());
        codeTextArea.appendText("end");
    }
    
    private void setupLua() {
        luaService = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        
        globals = JsePlatform.standardGlobals();
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
        Platform.runLater(() -> {
            try {
                globals.load(new StringReader(codeTextArea.getText()), "interopTest").call();
                CallbackStore callbackStore = new CallbackStore();
                globals.set("callbackStore", CoerceJavaToLua.coerce(callbackStore));
                globals.get("initCallbacks").call();
                callbackTableView.getItems().forEach(tableEntry ->
                    tableEntry.setResult(callbackStore.<Supplier<String>>performCallback(tableEntry.getKey(), () -> tableEntry.getValue()).toString()));
            } catch (Throwable ex) {
                Dialogs.create()
                    .style(DialogStyle.NATIVE)
                    .lightweight()
                    .title("Lua Exception")
                    .masthead((ex instanceof Error) ? "An error has occured" : "An exception has occured")
                    .showException(ex);
            }
        });
    }
    
    @FXML
    private void handleAddButtonAction(final ActionEvent actionEvent) {
        String key = keyTextField.getText();
        String value = valueTextField.getText();
        if (key.isEmpty()) {
            Dialogs.create()
                .style(DialogStyle.NATIVE)
                .lightweight()
                .title("Invalid Action")
                .masthead("Action is not allowed")
                .message("You cannot add an empty key.")
                .showError();
            return;
        }
        if (callbackTableView.getItems().stream().anyMatch(tableEntry -> tableEntry.getKey().equals(key))) {
            Dialogs.create()
                .style(DialogStyle.NATIVE)
                .lightweight()
                .title("Invalid Action")
                .masthead("Action is not allowed")
                .message("An entry with key \"" + key + "\" already exists.")
                .showError();
            return;
        }
        TableEntry tableEntry = new TableEntry(key, value);
        callbackTableView.getItems().add(tableEntry);
        outputTextArea.clear();
        luaService.submit(this::runLuaCode);
        keyTextField.clear();
        valueTextField.clear();
    }
    
    public static class TableEntry {
        private final StringProperty keyProperty;
        private final StringProperty valueProperty;
        private final StringProperty resultProperty;
        
        private TableEntry(final String key, final String value) {
            this.keyProperty = new SimpleStringProperty(key);
            this.valueProperty = new SimpleStringProperty(value);
            this.resultProperty = new SimpleStringProperty();
        }
        
        public StringProperty keyProperty() {
            return keyProperty;
        }
        
        public String getKey() {
            return keyProperty.getValue();
        }
        
        public StringProperty valueProperty() {
            return valueProperty;
        }
        
        public String getValue() {
            return valueProperty.getValue();
        }
        
        public void setValue(final String value) {
            valueProperty.setValue(value);
        }
        
        public StringProperty resultProperty() {
            return resultProperty;
        }
        
        public void setResult(final String result) {
            resultProperty.setValue(result);
        }
        
        public String getResult() {
            return resultProperty.getValue();
        }
    }
}
