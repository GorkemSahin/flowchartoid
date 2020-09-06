package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.graphics.drawable.VectorDrawableCompat;

import com.google.gson.annotations.Expose;
import com.sahin.gorkem.flowchartoid.App;
import com.sahin.gorkem.flowchartoid.R;

/**
 * Created by Gorkem on 4/25/2018.
 */

public class Line {

    public enum POSITION {
        TOP,
        BOTTOM,
        RIGHT,
        LEFT,
        BOTTOM_RIGHT_CORNER,
        TOP_RIGHT_CORNER
    }

    private final static int STROKE_WIDTH = 75;

    private Shape firstShape, secondShape;
    @Expose private int firstShapeId;
    @Expose private int secondShapeId;

    @Expose private POSITION firstPosition;
    @Expose private POSITION secondPosition;

    private DrawingSurface drawingSurface;
    private Paint linePaint;

    public Line (DrawingSurface drawingSurface, Shape firstShape, POSITION firstPosition, Shape secondShape, POSITION secondPosition) {
        this.drawingSurface = drawingSurface;
        this.firstShape = firstShape;
        this.secondShape = secondShape;
        firstShapeId = firstShape.getId();
        secondShapeId = secondShape.getId();
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(STROKE_WIDTH);
        linePaint.setAntiAlias(true);
    }

    public boolean drawThis(){
        int x1, y1, x2, y2;
        Path path = new Path();
        x1 = firstShape.getShapeOrigin().getX();
        x2 = secondShape.getShapeOrigin().getX();
        y1 = firstShape.getShapeOrigin().getY();
        y2 = secondShape.getShapeOrigin().getY();
        switch (firstPosition){
            case RIGHT:
                x1 += firstShape.getWidth()/2;
                y2 -= secondShape.getHeigth()/2;
                path.moveTo(x1, y1);
                path.lineTo(x2, y1);
                path.lineTo(x2, y2);
                break;
            case LEFT:
                x1 -= firstShape.getWidth()/2;
                y2 -= secondShape.getHeigth()/2;
                path.moveTo(x1, y1);
                path.lineTo(x2, y1);
                path.lineTo(x2, y2);
                break;
            case BOTTOM_RIGHT_CORNER:
                x1 += 3*firstShape.getWidth()/8;
                x2 = secondShape.getShapeOrigin().getX();
                y1 += firstShape.getHeigth()/4;
                y2 -= secondShape.getHeigth()/2;
                path.moveTo(x1, y1);
                path.lineTo(x2, y2 - secondShape.getHeigth()/4);
                path.lineTo(x2, y2);
                break;
            default:
                x1 = firstShape.getShapeOrigin().getX();
                x2 = secondShape.getShapeOrigin().getX();
                if (firstShape.getShapetype() == Shape.SHAPETYPE.OUTPUT){
                    y1 += firstShape.getHeigth()/3;
                } else {
                    y1 += firstShape.getHeigth()/2;
                }
                y2 = secondShape.getShapeOrigin().getY();
                switch (secondPosition){
                    case TOP_RIGHT_CORNER:
                        x2 += 3*secondShape.getWidth()/8;
                        y2 -= secondShape.getHeigth()/4;
                        path.moveTo(x1, y1);
                        path.lineTo(x1, y1 + firstShape.getHeigth()/4);
                        path.lineTo(x1 + firstShape.getWidth(), y1 + firstShape.getHeigth()/4);
                        path.lineTo(x1 + firstShape.getWidth(), y2);
                        path.lineTo(x2, y2);
                        break;
                    default:
                        y2 -= secondShape.getHeigth()/2;
                        int deltaX = x1 - x2;
                        int deltaY = y1 - y2;
                        path.moveTo(x1, y1);
                        path.lineTo(x1, y1 - deltaY/2);
                        path.lineTo(x1 - deltaX, y1 - deltaY/2);
                        path.lineTo(x1 - deltaX, y1 - deltaY);
                        break;
                }
                break;
        }
        //path.lineTo(x2, y2 - secondShape.getHeigth()/2);
        /*
        if (y1 > y2){
            path.lineTo(firstShape.getShapeOrigin().getX(), firstShape.getShapeOrigin().getY() - deltaY/2);
            path.lineTo(secondShape.getShapeOrigin().getX(), firstShape.getShapeOrigin().getY() - deltaY/2);
            path.lineTo(secondShape.getShapeOrigin().getX(), secondShape.getShapeOrigin().getY() + secondShape.getHeigth()/2);
        } else {
            path.lineTo(firstShape.getShapeOrigin().getX(), firstShape.getShapeOrigin().getY() - deltaY/2);
            path.lineTo(secondShape.getShapeOrigin().getX(), firstShape.getShapeOrigin().getY() - deltaY/2);
            path.lineTo(secondShape.getShapeOrigin().getX(), secondShape.getShapeOrigin().getY() - secondShape.getHeigth()/2);
        }
        */
        /*
        float frac = (float) 0.1;

        float point_x_1 = objOrigin.getX() + (1 - frac) * deltaX + frac * deltaY;
        float point_y_1 = objOrigin.getY() + (1 - frac) * deltaY - frac * deltaX;

        float point_x_2 = endPoint.getX();
        float point_y_2 = endPoint.getY();

        float point_x_3 = objOrigin.getX() + (1 - frac) * deltaX - frac * deltaY;
        float point_y_3 = objOrigin.getY() + (1 - frac) * deltaY + frac * deltaX;

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(point_x_2, point_y_2);
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.lineTo(point_x_1, point_y_1);
        path.close();
        drawingSurface.getCanvas().drawPath(path, linePaint);
        */

        drawingSurface.getCanvas().drawPath(path, linePaint);
        return true;
    }

    public Shape getFirstShape() {
        return firstShape;
    }

    public Shape getSecondShape() {
        return secondShape;
    }

    public POSITION getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(POSITION firstPosition) {
        this.firstPosition = firstPosition;
    }

    public POSITION getSecondPosition() {
        return secondPosition;
    }

    public void setSecondPosition(POSITION secondPosition) {
        this.secondPosition = secondPosition;
    }

}
