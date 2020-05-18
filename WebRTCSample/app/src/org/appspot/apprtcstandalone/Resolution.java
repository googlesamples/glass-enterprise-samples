package org.appspot.apprtcstandalone;

public class Resolution {
    private int height;
    private int width;

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
