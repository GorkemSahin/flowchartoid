package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.annotations.Expose;
import com.sahin.gorkem.flowchartoid.App;
import com.sahin.gorkem.flowchartoid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gorkem on 5/22/2018.
 */

public class ConditionShape extends Shape {

    @Expose private Line trueLine;
    @Expose private Line falseLine;

    public ConditionShape(Context context, DrawingSurface drawingSurface, int x, int y, int width, int height, SHAPETYPE shapetype, int id) {
        super(context, drawingSurface, x, y, width, height, shapetype, id);
    }

    @Override
    public void setLine (final Shape secondShape){
        final Dialog dialog = new Dialog(drawingSurface.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.if_shape_dialog);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.ifPositionsRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (secondShape.getPreviousShape() != null){
                    removePreviousConnection(secondShape, Line.POSITION.TOP);
                }
                secondShape.setPreviousShape(getThis());
                if (i == R.id.trueRadioButton){
                    trueLine = new Line(drawingSurface, getThis(), Line.POSITION.RIGHT, secondShape, Line.POSITION.TOP);
                } else {
                    falseLine = new Line(drawingSurface, getThis(), Line.POSITION.LEFT, secondShape, Line.POSITION.TOP);
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
        if (trueLine != null){
            trueLine.drawThis();
        }
        if (falseLine != null){
            falseLine.drawThis();
        }
        return true;
    }

    @Override
    public void removeLine (Shape secondShape, Line.POSITION position){
        if (trueLine != null && trueLine.getSecondShape() == secondShape && trueLine.getSecondPosition() == position){
            trueLine = null;
        } else if (falseLine != null && falseLine.getSecondShape() == secondShape && falseLine.getSecondPosition() == position){
            falseLine = null;
        }
    }

    @Override
    public void setLineFromJSON (Line.POSITION firstPosition, int secondShapeId, Line.POSITION secondPosition){
        drawingSurface.getShape(secondShapeId).setPreviousShape(this);
        if (firstPosition == Line.POSITION.LEFT){
            falseLine = new Line(drawingSurface, this, firstPosition, drawingSurface.getShape(secondShapeId), secondPosition);
        } else {
            trueLine = new Line(drawingSurface, this, firstPosition, drawingSurface.getShape(secondShapeId), secondPosition);
        }
    }

}
