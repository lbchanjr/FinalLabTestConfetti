package ca.louisechan.finallabtestconfetti;

import android.graphics.Color;

import java.util.Random;

public class Confetti {
    private float centerX;
    private float centerY;
    private int color;
    private int speed;

    public Confetti(float centerX, float centerY) {
        Random r = new Random();
        int red = r.nextInt(255-77+1) + 77;
        int green = r.nextInt(255-77+1) + 77;
        int blue = r.nextInt(255-77+1) + 77;

        this.centerX = centerX;
        this.centerY = centerY;
        this.color = Color.argb(255, red, green, blue);
        this.speed = 0;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
