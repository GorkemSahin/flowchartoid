package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.view.MotionEvent;

import com.google.gson.annotations.Expose;

/**
 * Created by Gorkem on 4/17/2018.
 */

public class Point {
    @Expose private int x;
    @Expose private int y;

    Point (int x, int y){
        this.x = x;
        this.y = y;
    }

    public void set (int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int xDis, int yDis) {
        this.x += xDis;
        this.y += yDis;
    }

    public static int distance(Point p1, Point p2) {
        return (int) Math.round(Math.sqrt(Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2)));
    }

    public static Point findMidPoint (Point point, MotionEvent event){
        int x = Math.round(event.getX(0) + event.getX(1));
        int y = Math.round(event.getY(0) + event.getY(1));
        point.set(x / 2, y / 2);
        return point;
    }
}
