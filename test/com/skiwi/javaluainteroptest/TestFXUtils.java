
package com.skiwi.javaluainteroptest;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author Frank van Heeswijk
 */
public final class TestFXUtils {
    private TestFXUtils() {
        throw new UnsupportedOperationException();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T lookupWithinNode(final Node parent, final String query, final Class<T> clazz) {
        for (Node node : parent.lookupAll(query)) {
            if (node.getClass().isAssignableFrom(clazz)) {
                return (T)node;
            }
        }
        throw new IllegalArgumentException("Parent " + parent + " doesn't contain node matching query " + query);
    }
    
    public static Tab lookupTab(final TabPane tabPane, final String text) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(text)) {
                return tab;
            }
        }
        throw new IllegalArgumentException("TabPane " + tabPane + "doesn't contain tab with text " + text);
    }
}
