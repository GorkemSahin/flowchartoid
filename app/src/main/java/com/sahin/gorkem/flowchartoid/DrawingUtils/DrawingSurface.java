package com.sahin.gorkem.flowchartoid.DrawingUtils;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sahin.gorkem.flowchartoid.DrawingActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Gorkem on 4/17/2018.
 */

public class DrawingSurface extends View implements View.OnDragListener {

    private Context context;

    private static ArrayList<Shape> shapes;
    private ScaleGestureDetector scaleGestureDetector;

    private static float scaleFactor;
    private boolean isZooming;

    private Shape selectedShape;
    private int lastX, lastY;
    private int posX, posY;
    private int cX, cY;

    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;


    private static final int MAX_CLICK_DURATION = 150;
    private static final int MIN_COORDINATE = -3000;
    private static final int MAX_COORDINATE = 3000;

    private long startClickTime;

    private static final String start = Shape.SHAPETYPE.START.toString();
    private static final String process = Shape.SHAPETYPE.PROCESS.toString();
    private static final String input = Shape.SHAPETYPE.INPUT.toString();
    private static final String condition = Shape.SHAPETYPE.CONDITION.toString();
    private static final String output = Shape.SHAPETYPE.OUTPUT.toString();
    private static final String while_ = Shape.SHAPETYPE.WHILE.toString();

    private int nextId;
    private boolean hideLines = false;

    public DrawingSurface(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        shapes = new ArrayList<Shape>();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        canvas = new Canvas();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(16f);
        scaleFactor = 0.237f;
        posX = 0;
        posY = 0;
        setOnDragListener(this);
        nextId = 0;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.save();
        posX = Math.min(MAX_COORDINATE, Math.max(MIN_COORDINATE, posX));
        posY = Math.min(MAX_COORDINATE, Math.max(MIN_COORDINATE, posY));
        canvas.translate(posX, posY);
        canvas.scale(scaleFactor, scaleFactor);
        if (!hideLines){
            paint.setColor(Color.GRAY);
            for (int a = -10; a < 10; a++) {
                for (int b = -10; b < 10; b++) {
                    canvas.drawLine(1000 * a, -10000, 1000 * a, 10000, paint);
                    canvas.drawLine(-10000, 1000 * b, 10000, 1000 * b, paint);
                }
            }
            paint.setColor(Color.BLACK);
        }
        for (Shape shape : shapes){
            shape.drawThis();
        }
        canvas.restore();
    }

    public void reset (){
        shapes.clear();
        invalidate();
    }

    public void addShape (Shape.SHAPETYPE shapetype, int x, int y){
        float usefulScale = scaleFactor * 3.5f;
        float[] coords = new float[2];

        coords[0] = x;
        coords[1] = y;
        Matrix matrix = new Matrix();
        matrix.set(getMatrix());
        matrix.preTranslate(posX, posY);
        matrix.preScale(scaleFactor, scaleFactor);
        matrix.invert(matrix);
        matrix.mapPoints(coords);

        int l = Math.round(this.getHeight());
        int w = Math.round(this.getWidth());

        if (shapetype == Shape.SHAPETYPE.CONDITION){
            shapes.add(new ConditionShape(context, this, x, y, l, w, shapetype, nextId));
        } else if (shapetype == Shape.SHAPETYPE.WHILE) {
            shapes.add(new WhileShape(context, this, x, y, l, w, shapetype, nextId));
        } else {
            shapes.add(new Shape(context,this, x, y, l, w, shapetype, nextId));
        }
        nextId++;
        invalidate();
    }

    public void select (Shape newShape){
        if (selectedShape != null){
            selectedShape.setSelect(false);
            selectedShape = null;
        }
        selectedShape = newShape;
        selectedShape.setSelect(true);
    }

    public boolean onTouchEvent (MotionEvent event){
        scaleGestureDetector.setQuickScaleEnabled(true);
        scaleGestureDetector.onTouchEvent(event);
        final int action = event.getAction();

        float[] coords = new float[2];
        coords[0] = event.getX();
        coords[1] = event.getY();

        Matrix matrix = new Matrix();
        matrix.set(getMatrix());

        matrix.preTranslate(posX, posY);
        matrix.preScale(scaleFactor, scaleFactor);
        matrix.invert(matrix);
        matrix.mapPoints(coords);

        final int x = Math.round(event.getX());
        final int y = Math.round(event.getY());

        cX = Math.round(coords[0]);
        cY = Math.round(coords[1]);

        switch (action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN: {
                startClickTime = Calendar.getInstance().getTimeInMillis();
                isZooming = false;
                lastX = x;
                lastY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (scaleGestureDetector.isInProgress()){
                    isZooming = true;
                } else if (!isZooming){
                    final int dX = (x - lastX);
                    final int dY = (y - lastY);
                    if (selectedShape != null){
                        selectedShape.translate(Math.round(dX / scaleFactor), Math.round(dY / scaleFactor));
                    } else {
                        posX += dX;
                        posY += dY;
                    }
                }
                lastX = x;
                lastY = y;
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION && !scaleGestureDetector.isInProgress()){
                    // Kullanıcı sadece dokundu. Nereye dokunduğuna bakılmalı.
                    boolean touchedAShape = false;
                    boolean addedTextOrLine = false;
                    Shape lastShape;
                    // Bir şekle dokunduysa o şekil seçili olmalı.
                    for (Shape shape : shapes){
                        if (shape.contains(new Point(cX, cY))){
                            if (selectedShape != null){
                                addedTextOrLine = true;
                                if (selectedShape == shape){
                                    getShapeTextInput(shape);
                                } else {
                                    selectedShape.setLine(shape);
                                }
                            }
                            select(shape);
                            touchedAShape = true;
                        }
                    }
                    // Boş alana dokunuldu, önceden seçilmiş olan şekil artık seçili olmamalı.
                    if ((!touchedAShape) || addedTextOrLine){
                        if (selectedShape != null) selectedShape.setSelect(false);
                        selectedShape = null;
                    }
                }
                invalidate();
            }
        }
        return true;
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        if (dragEvent.getAction() == DragEvent.ACTION_DROP){

            float[] coords = new float[2];
            coords[0] = dragEvent.getX();
            coords[1] = dragEvent.getY();

            Matrix matrix = new Matrix();
            matrix.set(getMatrix());

            matrix.preTranslate(posX, posY);
            matrix.preScale(scaleFactor, scaleFactor);
            matrix.invert(matrix);
            matrix.mapPoints(coords);

            final float x = dragEvent.getX();
            final float y = dragEvent.getY();

            cX = Math.round(coords[0]);
            cY = Math.round(coords[1]);

            ClipData.Item item = dragEvent.getClipData().getItemAt(0);
            String dragData = item.getText().toString();
            if (dragData.equals(start)){
                addShape(Shape.SHAPETYPE.START, cX, cY);
            } else if (dragData.equals(process)) {
                addShape(Shape.SHAPETYPE.PROCESS, cX, cY);
            } else if (dragData.equals(input)){
                addShape(Shape.SHAPETYPE.INPUT, cX, cY);
            } else if (dragData.equals(condition)){
                addShape(Shape.SHAPETYPE.CONDITION, cX, cY);
            } else if (dragData.equals(output)){
                addShape(Shape.SHAPETYPE.OUTPUT, cX, cY);
            } else if (dragData.equals(while_)){
                addShape(Shape.SHAPETYPE.WHILE, cX, cY);
            }
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (selectedShape != null){
                float shapeScaleFactor = detector.getScaleFactor();
                selectedShape.scale(shapeScaleFactor);
            } else {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 1.0f));
            }
            invalidate();
            return true;
        }
    }

    private void getShapeTextInput (final Shape shape){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter some text.");
        final EditText shapeTextEditText = new EditText(context);
        shapeTextEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(shapeTextEditText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String shapeString = shapeTextEditText.getText().toString();
                shape.setText(new Text(DrawingSurface.this, shape, shapeString));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void prepareToSaveAsImage (boolean hideLines){
        this.hideLines = hideLines;
        invalidate();
        /*
        int left, right, top, bottom;
        left = right = top = bottom = 0;
        for (Shape shape : shapes){
            if (shape.getShapeOrigin().getX() - shape.getWidth()/2 < left){
                left = shape.getShapeOrigin().getX() - shape.getWidth()/2;
            }
            if (shape.getShapeOrigin().getX() + shape.getWidth()/2 > right){
                right = shape.getShapeOrigin().getX() + shape.getWidth()/2;
            }
            if (shape.getShapeOrigin().getY() - shape.getHeigth()/2 < bottom){
                top = shape.getShapeOrigin().getY() - shape.getHeigth()/2;
            }
            if (shape.getShapeOrigin().getY() + shape.getHeigth()/2 > top){
                bottom = shape.getShapeOrigin().getY() + shape.getHeigth()/2;
            }
        }
        Log.d(DrawingActivity.class.getSimpleName().toString(), "Top: " + top + " Left: " + left + " Bottom: " + bottom + " Right: " + right);
        */
    }

    public String getDiagramData (){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<ArrayList<Shape>>(){}.getType();
        String data = gson.toJson(shapes, type);
        return data;
    }

    public void setDiagramData (String jShapes, String jLines){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = new TypeToken<ArrayList<Shape>>(){}.getType();
        shapes = gson.fromJson(jShapes, type);
        invalidate();
    }

    public Shape getShape (int id){
        for (Shape shape : shapes){
            if (shape.getId() == id){
                return shape;
            }
        }
        return null;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }

    public static float getScaleFactor() {
        return scaleFactor;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

}
