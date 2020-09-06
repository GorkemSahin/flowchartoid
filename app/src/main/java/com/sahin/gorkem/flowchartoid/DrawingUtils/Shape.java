package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.Window;
import android.widget.RadioGroup;

import com.google.gson.annotations.Expose;
import com.sahin.gorkem.flowchartoid.R;

import java.util.ArrayList;

import static com.sahin.gorkem.flowchartoid.DrawingUtils.Shape.SHAPETYPE.CONDITION;
import static com.sahin.gorkem.flowchartoid.DrawingUtils.Shape.SHAPETYPE.ENDofCONDITION;
import static com.sahin.gorkem.flowchartoid.DrawingUtils.Shape.SHAPETYPE.WHILE;

/**
 * Created by Gorkem on 4/17/2018.
 */

public class Shape {

    public enum SHAPETYPE {
        START, INPUT, CONDITION, PROCESS, WHILE, OUTPUT, ENDofCONDITION
    }

    protected final static int MAX_SHAPE_SIZE = 4000;
    protected final static int MIN_SHAPE_SIZE = 700;
    protected final static int SHAPE_PAINT_STROKE = 60;
    protected final static float SHAPE_SELECT_PAINT_STROKE_FACTOR = 1.5f;

    protected VectorDrawableCompat shape;

    protected Rect rectangle;

    @Expose protected int id;
    @Expose protected int width;
    @Expose protected int height;
    protected float ratio, factor;

    protected DrawingSurface drawingSurface;
    @Expose protected Point shapeOrigin;
    protected Paint shapePaintStroke;
    protected Paint shapePaintFill;
    protected Paint selectBorder;

    @Expose protected SHAPETYPE shapetype;
    protected boolean shapeSelect;
    @Expose protected Text text;

    @Expose protected Line line;

    protected Shape previousShape;

    protected Shape otherPreviousShape;


    public Shape (Context context, DrawingSurface drawingSurface, int x, int y, int width, int height, SHAPETYPE shapetype, int id){
        this.drawingSurface = drawingSurface;
        this.width = width;
        this.height = height;
        ratio = (float) width/height;
        shapeOrigin = new Point(x, y);
        this.shapetype = shapetype;
        switch (shapetype){
            case START:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.start_shape, null);
                break;
            case INPUT:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.input_shape, null);
                break;
            case CONDITION:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.condition_shape, null);
                break;
            case PROCESS:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.process_shape, null);
                break;
            case WHILE:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.while_shape, null);
                break;
            case OUTPUT:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.output_shape, null);
                break;
            case ENDofCONDITION:
                shape = VectorDrawableCompat.create(context.getResources(), R.drawable.process_shape, null);
                break;
        }
        rectangle = new Rect(x-(width/2), y-(height/2), x+(width/2), y+(height/2));

        selectBorder = new Paint();
        shapePaintStroke = new Paint();
        shapePaintStroke.setColor(Color.BLACK);
        shapePaintStroke.setStyle(Paint.Style.STROKE);
        shapePaintStroke.setStrokeWidth(SHAPE_PAINT_STROKE);
        shapePaintStroke.setAntiAlias(true);

        shapePaintFill = new Paint();
        shapePaintFill.setColor(Color.WHITE);
        shapePaintFill.setStyle(Paint.Style.FILL);
        shapePaintFill.setAntiAlias(true);
        this.id = id;
    }

    public boolean setSelect (boolean flag){
        boolean last = shapeSelect;
        shapeSelect = flag;
        return last;
    }

    void updateSelectBorder (){
        selectBorder.set(shapePaintStroke);
        selectBorder.setDither(true);
        selectBorder.setColor(Color.RED);
        selectBorder.setStrokeWidth(shapePaintStroke.getStrokeWidth() * SHAPE_SELECT_PAINT_STROKE_FACTOR);
        selectBorder.setMaskFilter(new BlurMaskFilter(shapePaintStroke.getStrokeWidth() * 2, BlurMaskFilter.Blur.NORMAL));
    }

    public boolean drawThis(){
        shape.setBounds(rectangle);
        if (shapeSelect){
            updateSelectBorder();
            drawingSurface.getCanvas().drawRect(rectangle, selectBorder);
        }
        if (text != null){
            text.drawThis();
        }
        shape.draw(drawingSurface.getCanvas());
        if (line != null){
            line.drawThis();
        }
        return true;
    }

    public boolean contains(Point point){
        int x = Math.round(point.getX());
        int y = Math.round(point.getY());
        return (rectangle.contains(x, y));
    }

    public void scale (float newFactor){
        factor = newFactor;
        height = Math.max(MIN_SHAPE_SIZE, Math.min(MAX_SHAPE_SIZE, Math.round(height * factor)));
        width = Math.round(height * ratio);
        rectangle.set(shapeOrigin.getX() - width/2, shapeOrigin.getY() - height/2, shapeOrigin.getX() + width/2, shapeOrigin.getY() + height/2);
    }

    public void translate (int xDis, int yDis){
        shapeOrigin.move(xDis, yDis);
        rectangle.set(shapeOrigin.getX()-(rectangle.width()/2), shapeOrigin.getY()-(rectangle.height()/2), shapeOrigin.getX() + (rectangle.width()/2), shapeOrigin.getY() + (rectangle.height()/2));
    }

    public void removeLine (Shape shape, Line.POSITION position){
        if (line != null && line.getSecondShape() == shape && line.getSecondPosition() == position){
            line = null;
        }
    }

    public void setLine(Shape secondShape) {
        if (secondShape != null){
            if (secondShape.getShapetype() == SHAPETYPE.WHILE) {
                setupWhileLine(secondShape);
            } else {
                removePreviousConnection(secondShape, Line.POSITION.TOP);
                secondShape.setPreviousShape(this);
                this.line = new Line(drawingSurface, this, Line.POSITION.BOTTOM, secondShape, Line.POSITION.TOP);
            }
        } else {
            line = null;
        }
    }

    public void removePreviousConnection (Shape shape, Line.POSITION position){
        if (this.getPreviousShape() == shape && position != Line.POSITION.TOP_RIGHT_CORNER){
            shape.removeLine(this, Line.POSITION.TOP);
        }
        if (shape.getPreviousShape() != null) {
            if (shape.getPreviousShape().getPreviousShape() != null && previousShape != null){
                if (shape.getPreviousShape().getPreviousShape() == previousShape && previousShape.getShapetype() == CONDITION){
                    shape.setOtherPreviousShape(shape.previousShape);
                }
            } else {
                if (shape.getOtherPreviousShape() != null){
                    shape.getOtherPreviousShape().removeLine(shape, Line.POSITION.TOP);
                    shape.setOtherPreviousShape(null);
                }
                shape.getPreviousShape().removeLine(shape, Line.POSITION.TOP);
            }
        }
    }

    public void setupWhileLine (final Shape shape){
        final Dialog dialog = new Dialog(drawingSurface.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.while_shape_dialog);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.whilePositionsRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.whileRadioButton){
                    WhileShape whileShape = (WhileShape) shape;
                    whileShape.setEndOfWhileShape(getThis());
                    if (shape.getPreviousShape() != null){
                        removePreviousConnection(whileShape, Line.POSITION.TOP_RIGHT_CORNER);
                    }
                    line = new Line(drawingSurface, getThis(), Line.POSITION.BOTTOM, shape, Line.POSITION.TOP_RIGHT_CORNER);
                } else {
                    if (shape.getPreviousShape() != null){
                        removePreviousConnection(shape, Line.POSITION.TOP);
                        shape.setPreviousShape(getThis());
                    }
                    line = new Line(drawingSurface, getThis(), Line.POSITION.BOTTOM, shape, Line.POSITION.TOP);
                }
                dialog.dismiss();
                drawingSurface.invalidate();
            }
        });
        dialog.show();
    }

    public void setLineFromJSON (Line.POSITION firstPosition, int secondShapeId, Line.POSITION secondPosition){
        if (drawingSurface.getShape(secondShapeId).getPreviousShape() == null){
            drawingSurface.getShape(secondShapeId).setPreviousShape(this);
        } else {
            drawingSurface.getShape(secondShapeId).setOtherPreviousShape(drawingSurface.getShape(secondShapeId).getPreviousShape());
            drawingSurface.getShape(secondShapeId).setPreviousShape(this);
        }
        if (secondPosition == Line.POSITION.TOP_RIGHT_CORNER){
            WhileShape whileShape = (WhileShape) drawingSurface.getShape(secondShapeId);
            whileShape.setEndOfWhileShape(this);
        }
        this.line = new Line(drawingSurface, this, firstPosition, drawingSurface.getShape(secondShapeId), secondPosition);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected Shape getThis (){
        return this;
    }

    public Shape getPreviousShape() {
        return previousShape;
    }

    public void setPreviousShape(Shape previousShape) {
        if (previousShape == null){
            if (this.previousShape != null){
                this.previousShape.removeLine(this, Line.POSITION.TOP);
            }
        }
        this.previousShape = previousShape;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Point getShapeOrigin() {
        return shapeOrigin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeigth() {
        return height;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    public SHAPETYPE getShapetype (){ return shapetype; }

    public Line getLine() {
        return line;
    }

    public Shape getOtherPreviousShape() {
        return otherPreviousShape;
    }

    public void setOtherPreviousShape(Shape otherPreviousShape) {
        this.otherPreviousShape = otherPreviousShape;
    }

}
