package com.studentnest.util;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public final class Icons {
    private Icons() {
    }

    public static Region glyph(String path, double size, Color color) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.setFill(color);
        double wrap = 24;
        svg.setScaleX(size / wrap);
        svg.setScaleY(size / wrap);
        Region box = new Region();
        box.setShape(svg);
        box.setMinSize(size, size);
        box.setPrefSize(size, size);
        box.setMaxSize(size, size);
        box.setStyle("-fx-background-color: " + toRgb(color) + ";");
        return box;
    }

    public static SVGPath path(String content, Color color) {
        SVGPath svg = new SVGPath();
        svg.setContent(content);
        svg.setFill(color);
        return svg;
    }

    public static final String HOME = "M3 10.5 12 3l9 7.5V21a1 1 0 0 1-1 1h-5v-7H9v7H4a1 1 0 0 1-1-1z";
    public static final String SEARCH = "M10 3a7 7 0 1 1 0 14 7 7 0 0 1 0-14zm11 18-5.2-5.2";
    public static final String HEART = "M12 21s-7-4.6-9.5-9A5.5 5.5 0 0 1 12 6.2 5.5 5.5 0 0 1 21.5 12C19 16.4 12 21 12 21z";
    public static final String PLUS = "M11 5h2v14h-2zM5 11h14v2H5z";
    public static final String LIST = "M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z";
    public static final String SHIELD = "M12 2 4 6v6c0 5 3.4 9.4 8 10 4.6-.6 8-5 8-10V6z";
    public static final String STAR = "M12 2l2.4 7.2H22l-6 4.4 2.3 7.2L12 16.8 5.7 20.8 8 13.6 2 9.2h7.6z";
    public static final String INFO = "M12 2a10 10 0 1 1 0 20 10 10 0 0 1 0-20zm-1 9h2v6h-2zm0-4h2v2h-2z";
    public static final String SUN = "M12 5a7 7 0 1 1 0 14 7 7 0 0 1 0-14zm0-3h2v3h-2zM12 19h2v3h-2zM2 11h3v2H2zm17 0h3v2h-3z";
    public static final String MOON = "M15 3a9 9 0 1 0 6 15 7 7 0 0 1-6-15z";
    public static final String USER = "M12 12a4 4 0 1 0-4-4 4 4 0 0 0 4 4zm0 2c-4 0-8 2-8 5v1h16v-1c0-3-4-5-8-5z";
    public static final String CHECK = "M4 12.5 9 17.5 20 6.5";
    public static final String PHONE = "M6 3h4l1 5-3 2a12 12 0 0 0 6 6l2-3 5 1v4a2 2 0 0 1-2 2A16 16 0 0 1 4 5a2 2 0 0 1 2-2z";
    public static final String MAP = "M4 6l6-2 4 2 6-2v14l-6 2-4-2-6 2z";
    public static final String BUILDING = "M4 21V5h10v16zM14 9h6v12h-6zM7 8h4v2H7zm0 4h4v2H7zm0 4h4v2H7z";

    private static String toRgb(Color color) {
        return String.format("rgba(%d,%d,%d,%.2f)",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255),
                color.getOpacity());
    }
}
