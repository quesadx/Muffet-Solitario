package cr.ac.una.muffetsolitario.controller;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.geometry.Bounds;

/**
 * Utility for making a JavaFX window resizable by dragging its borders.
 */
public class WindowResizer {
    private static final int RESIZE_MARGIN = 8;
    private static double xOffset = 0, yOffset = 0;
    private static boolean resizing = false;
    private static Cursor currentCursor = Cursor.DEFAULT;
    // Add these fields for correct resize reference
    private static double startX = 0, startY = 0, startWidth = 0, startHeight = 0;

    public static void addResizeListeners(Node root) {
        root.setOnMouseMoved(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            Cursor cursor = getCursorForPosition(root, event);
            root.setCursor(cursor);
        });
        root.setOnMousePressed(event -> {
            Cursor cursor = getCursorForPosition(root, event);
            if (cursor != Cursor.DEFAULT) {
                resizing = true;
                currentCursor = cursor;
                xOffset = event.getScreenX();
                yOffset = event.getScreenY();
                startX = ((Stage) root.getScene().getWindow()).getX();
                startY = ((Stage) root.getScene().getWindow()).getY();
                startWidth = ((Stage) root.getScene().getWindow()).getWidth();
                startHeight = ((Stage) root.getScene().getWindow()).getHeight();
            }
        });
        root.setOnMouseDragged(event -> {
            if (!resizing) return;
            Stage stage = (Stage) root.getScene().getWindow();
            double dx = event.getScreenX() - xOffset;
            double dy = event.getScreenY() - yOffset;
            double minWidth = 400, minHeight = 300;
            switch (currentCursor.toString()) {
                case "NW_RESIZE":
                    if (startWidth - dx > minWidth) {
                        stage.setX(startX + dx);
                        stage.setWidth(startWidth - dx);
                    }
                    if (startHeight - dy > minHeight) {
                        stage.setY(startY + dy);
                        stage.setHeight(startHeight - dy);
                    }
                    break;
                case "NE_RESIZE":
                    if (startWidth + dx > minWidth) {
                        stage.setWidth(startWidth + dx);
                    }
                    if (startHeight - dy > minHeight) {
                        stage.setY(startY + dy);
                        stage.setHeight(startHeight - dy);
                    }
                    break;
                case "SW_RESIZE":
                    if (startWidth - dx > minWidth) {
                        stage.setX(startX + dx);
                        stage.setWidth(startWidth - dx);
                    }
                    if (startHeight + dy > minHeight) {
                        stage.setHeight(startHeight + dy);
                    }
                    break;
                case "SE_RESIZE":
                    if (startWidth + dx > minWidth) {
                        stage.setWidth(startWidth + dx);
                    }
                    if (startHeight + dy > minHeight) {
                        stage.setHeight(startHeight + dy);
                    }
                    break;
                case "E_RESIZE":
                    if (startWidth + dx > minWidth) {
                        stage.setWidth(startWidth + dx);
                    }
                    break;
                case "W_RESIZE":
                    if (startWidth - dx > minWidth) {
                        stage.setX(startX + dx);
                        stage.setWidth(startWidth - dx);
                    }
                    break;
                case "N_RESIZE":
                    if (startHeight - dy > minHeight) {
                        stage.setY(startY + dy);
                        stage.setHeight(startHeight - dy);
                    }
                    break;
                case "S_RESIZE":
                    if (startHeight + dy > minHeight) {
                        stage.setHeight(startHeight + dy);
                    }
                    break;
            }
        });
        root.setOnMouseReleased(event -> {
            resizing = false;
            currentCursor = Cursor.DEFAULT;
            root.setCursor(Cursor.DEFAULT);
        });
    }

    private static Cursor getCursorForPosition(Node root, MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        double width = root.getBoundsInLocal().getWidth();
        double height = root.getBoundsInLocal().getHeight();
        boolean left = x < RESIZE_MARGIN;
        boolean right = x > width - RESIZE_MARGIN;
        boolean top = y < RESIZE_MARGIN;
        boolean bottom = y > height - RESIZE_MARGIN;
        if (left && top) return Cursor.NW_RESIZE;
        if (right && top) return Cursor.NE_RESIZE;
        if (left && bottom) return Cursor.SW_RESIZE;
        if (right && bottom) return Cursor.SE_RESIZE;
        if (right) return Cursor.E_RESIZE;
        if (left) return Cursor.W_RESIZE;
        if (top) return Cursor.N_RESIZE;
        if (bottom) return Cursor.S_RESIZE;
        return Cursor.DEFAULT;
    }
}
