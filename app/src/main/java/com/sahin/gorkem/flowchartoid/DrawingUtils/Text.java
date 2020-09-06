package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.google.gson.annotations.Expose;
import com.sahin.gorkem.flowchartoid.DrawingActivity;

/**
 * Created by Gorkem on 4/26/2018.
 */

public class Text {
    private DrawingSurface drawingSurface;
    private Shape shape;
    @Expose private String string;
    private TextPaint textPaint;
    private float textSize;
    private final static float BIG_HEIGHT_PADDING_FACTOR = 3f;
    private final static float SMALL_HEIGHT_PADDING_FACTOR = 1.25f;
    private final static float SMALL_WIDTH_PADDING_FACTOR = 1.25f;
    private final static float BIG_WIDTH_PADDING_FACTOR = 1.75f;

    private final static int TEXT_TEST_SIZE = 500;
    private final static int MIN_TEXT_SIZE = 50;
    private final static int MAX_TEXT_SIZE = 1000;

    public Text (DrawingSurface drawingSurface, Shape shape, String string){
        this.drawingSurface = drawingSurface;
        this.shape = shape;
        this.string = string;
        this.string = string;
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(10f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public boolean drawThis(){
        setTextSizeForWidth(shape.getWidth(), shape.getHeigth());
        Rect bounds = new Rect();
        textPaint.getTextBounds(string, 0, 1, bounds);
        drawingSurface.getCanvas().drawText(string, shape.getShapeOrigin().getX(), shape.getShapeOrigin().getY() + (bounds.height())/2, textPaint);
        return true;
    }

    private void setTextSizeForWidth(float desiredWidth, float desiredHeight) {
        if (shape.getShapetype() == Shape.SHAPETYPE.CONDITION || shape.getShapetype() == Shape.SHAPETYPE.OUTPUT || string.length() < 3){
            desiredHeight /= BIG_HEIGHT_PADDING_FACTOR;
            desiredWidth /= BIG_WIDTH_PADDING_FACTOR;
        } else {
            desiredHeight /= SMALL_HEIGHT_PADDING_FACTOR;
            desiredWidth /= SMALL_WIDTH_PADDING_FACTOR;
        }

        textPaint.setTextSize(TEXT_TEST_SIZE);
        Rect bounds = new Rect();
        textPaint.getTextBounds(string, 0, string.length(), bounds);
        float textSizeForWidth = TEXT_TEST_SIZE * desiredWidth / bounds.width();
        float textSizeForHeigth  = TEXT_TEST_SIZE * desiredHeight / bounds.height();
        textSize = Math.min(textSizeForHeigth, textSizeForWidth);
        textSize = Math.max(MIN_TEXT_SIZE, Math.min(MAX_TEXT_SIZE, textSize));
        textPaint.setTextSize(textSize);
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
