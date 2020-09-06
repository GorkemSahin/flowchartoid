package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.RadioGroup;

import com.google.gson.annotations.Expose;
import com.sahin.gorkem.flowchartoid.R;

/**
 * Created by Gorkem on 5/24/2018.
 */

public class WhileShape extends Shape {

    @Expose private Line whileLine;
    private Shape endOfWhileShape;

    public WhileShape(Context context, DrawingSurface drawingSurface, int x, int y, int width, int height, SHAPETYPE shapetype, int id) {
        super(context, drawingSurface, x, y, width, height, shapetype, id);
    }

    @Override
    public void setLine (final Shape secondShape){
        final Dialog dialog = new Dialog(drawingSurface.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.while_shape_dialog);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.whilePositionsRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (secondShape.getPreviousShape() != null){
                    removePreviousConnection(secondShape, Line.POSITION.TOP);
                }
                secondShape.setPreviousShape(getThis());
                if (i == R.id.whileRadioButton){
                    whileLine = new Line(drawingSurface, getThis(), Line.POSITION.BOTTOM_RIGHT_CORNER, secondShape, Line.POSITION.TOP);
                } else {
                    line = new Line(drawingSurface, getThis(), Line.POSITION.BOTTOM, secondShape, Line.POSITION.TOP);
                }
                dialog.dismiss();
                drawingSurface.invalidate();
            }
        });
        dialog.show();
    }

    @Override
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
        if (whileLine != null){
            whileLine.drawThis();
        }
        if (line != null){
            line.drawThis();
        }
        return true;
    }

    @Override
    public void removeLine (Shape shape, Line.POSITION position){
        if (line != null && line.getSecondShape() == shape && line.getSecondPosition() == position){
            line = null;
        } else if (whileLine != null && whileLine.getSecondShape() == shape && whileLine.getSecondPosition() == position){
            whileLine = null;
        }
    }

    @Override
    public void setLineFromJSON (Line.POSITION firstPosition, int secondShapeId, Line.POSITION secondPosition){
        if (firstPosition == Line.POSITION.BOTTOM){
            if (drawingSurface.getShape(secondShapeId).getPreviousShape() == null){
                drawingSurface.getShape(secondShapeId).setPreviousShape(this);
            } else {
                drawingSurface.getShape(secondShapeId).setOtherPreviousShape(drawingSurface.getShape(secondShapeId).getPreviousShape());
                drawingSurface.getShape(secondShapeId).setPreviousShape(this);
            }
            this.line = new Line(drawingSurface, this, firstPosition, drawingSurface.getShape(secondShapeId), secondPosition);
        } else {
            drawingSurface.getShape(secondShapeId).setPreviousShape(this);
            whileLine = new Line(drawingSurface, this, firstPosition, drawingSurface.getShape(secondShapeId), secondPosition);
        }
    }

    public void setPreviousShape(Shape previousShape) {
        this.previousShape = previousShape;
    }

    public Shape getEndOfWhileShape() {
        return endOfWhileShape;
    }

    public void setEndOfWhileShape(Shape endOfWhileShape) {
        this.endOfWhileShape = endOfWhileShape;
    }
}
