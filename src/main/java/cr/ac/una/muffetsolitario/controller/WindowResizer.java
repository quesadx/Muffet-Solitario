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
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(event -> {
            if (!resizing) return;
            Stage stage = (Stage) root.getScene().getWindow();
            double dx = event.getSceneX() - xOffset;
            double dy = event.getSceneY() - yOffset;
            Bounds bounds = root.getBoundsInParent();
            double minWidth = 400, minHeight = 300;
            switch (currentCursor.toString()) {
                case "NW_RESIZE":
                    if (stage.getWidth() - dx > minWidth) {
                        stage.setX(stage.getX() + dx);
                        stage.setWidth(stage.getWidth() - dx);
                    }
                    if (stage.getHeight() - dy > minHeight) {
                        stage.setY(stage.getY() + dy);
                        stage.setHeight(stage.getHeight() - dy);
                    }
                    break;
                case "NE_RESIZE":
                    if (stage.getWidth() + dx > minWidth) {
                        stage.setWidth(stage.getWidth() + dx);
                        xOffset = event.getSceneX();
                    }
                    if (stage.getHeight() - dy > minHeight) {
                        stage.setY(stage.getY() + dy);
                        stage.setHeight(stage.getHeight() - dy);
                    }
                    break;
                case "SW_RESIZE":
                    if (stage.getWidth() - dx > minWidth) {
                        stage.setX(stage.getX() + dx);
                        stage.setWidth(stage.getWidth() - dx);
                    }
                    if (stage.getHeight() + dy > minHeight) {
                        stage.setHeight(stage.getHeight() + dy);
                        yOffset = event.getSceneY();
                    }
                    break;
                case "SE_RESIZE":
                    if (stage.getWidth() + dx > minWidth) {
                        stage.setWidth(stage.getWidth() + dx);
                        xOffset = event.getSceneX();
                    }
                    if (stage.getHeight() + dy > minHeight) {
                        stage.setHeight(stage.getHeight() + dy);
                        yOffset = event.getSceneY();
                    }
                    break;
                case "E_RESIZE":
                    if (stage.getWidth() + dx > minWidth) {
                        stage.setWidth(stage.getWidth() + dx);
                        xOffset = event.getSceneX();
                    }
                    break;
                case "W_RESIZE":
                    if (stage.getWidth() - dx > minWidth) {
                        stage.setX(stage.getX() + dx);
                        stage.setWidth(stage.getWidth() - dx);
                    }
                    break;
                case "N_RESIZE":
                    if (stage.getHeight() - dy > minHeight) {
                        stage.setY(stage.getY() + dy);
                        stage.setHeight(stage.getHeight() - dy);
                    }
                    break;
                case "S_RESIZE":
                    if (stage.getHeight() + dy > minHeight) {
                        stage.setHeight(stage.getHeight() + dy);
                        yOffset = event.getSceneY();
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
