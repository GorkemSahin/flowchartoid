package com.sahin.gorkem.flowchartoid;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sahin.gorkem.flowchartoid.DatabaseUtils.DatabaseHelper;
import com.sahin.gorkem.flowchartoid.DatabaseUtils.Diagram;
import com.sahin.gorkem.flowchartoid.DrawingUtils.ConditionShape;
import com.sahin.gorkem.flowchartoid.DrawingUtils.DrawingSurface;
import com.sahin.gorkem.flowchartoid.DrawingUtils.Line;
import com.sahin.gorkem.flowchartoid.DrawingUtils.Shape;
import com.sahin.gorkem.flowchartoid.DrawingUtils.Text;
import com.sahin.gorkem.flowchartoid.DrawingUtils.WhileShape;
import com.sahin.gorkem.flowchartoid.MainActivity;
import com.sahin.gorkem.flowchartoid.MainActivityFragments.FlowchartsFragment;
import com.sahin.gorkem.flowchartoid.R;
import com.sahin.gorkem.flowchartoid.RecyclerViewUtils.FlowchartAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.UUID;

import static com.sahin.gorkem.flowchartoid.App.getContext;

public class DrawingActivity extends AppCompatActivity {
    public final String TAG = MainActivity.class.getSimpleName();
    private DrawingSurface drawingSurface;
    public Point point;

    ImageView startShape;
    ImageView inputShape;
    ImageView conditionShape;
    ImageView processShape;
    ImageView outputShape;
    ImageButton deleteButton;
    ImageButton saveButton;
    ProgressBar progressBar;

    private String flowchartName;
    private String data;

    DatabaseHelper databaseHelper;
    String pathToFolder, pathToImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        getSupportActionBar().hide();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        flowchartName = getIntent().getStringExtra("flowchartName");
        data = getIntent().getStringExtra("data");
        if (data != null && data.length() > 0){
            new LoadDiagramDataTask().execute();
        }
        ImageView startShape = findViewById(R.id.start_shape);
        setDraggable(startShape);
        ImageView inputShape = findViewById(R.id.input_shape);
        setDraggable(inputShape);
        ImageView conditionShape = findViewById(R.id.condition_shape);
        setDraggable(conditionShape);
        ImageView processShape = findViewById(R.id.process_shape);
        setDraggable(processShape);
        ImageView outputShape = findViewById(R.id.output_shape);
        setDraggable(outputShape);
        ImageView whileShape = findViewById(R.id.while_shape);
        setDraggable(whileShape);
        drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);

        databaseHelper = new DatabaseHelper(getContext());
        pathToFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name);
        pathToImage = pathToFolder + "/" + flowchartName + ".png";
    }

    private void setDraggable (final ImageView view){
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item(String.valueOf(view.getTag()));
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
                return v.startDrag(data,
                        myShadow,
                        null,
                        0
                );
            }
        });
    }

    public void setDeleteButton (View view){
        Shape shapeToDelete = drawingSurface.getSelectedShape();
        if (shapeToDelete != null){
            if (shapeToDelete.getPreviousShape() != null){
                shapeToDelete.getPreviousShape().removeLine(shapeToDelete, Line.POSITION.TOP);
            }
            if (shapeToDelete.getOtherPreviousShape() != null){
                shapeToDelete.getOtherPreviousShape().removeLine(shapeToDelete, Line.POSITION.TOP);
            }
            if (shapeToDelete.getShapetype() == Shape.SHAPETYPE.WHILE){
                WhileShape whileShape = (WhileShape) shapeToDelete;
                if (whileShape.getEndOfWhileShape() != null){
                    whileShape.getEndOfWhileShape().removeLine(whileShape, Line.POSITION.TOP_RIGHT_CORNER);
                }
            }
            drawingSurface.getShapes().remove(drawingSurface.getSelectedShape());
            drawingSurface.setSelectedShape(null);
            drawingSurface.invalidate();
        }
    }

    public void setSaveButton (View view){
        String data = drawingSurface.getDiagramData();
        if (!(new File(pathToImage).exists())){
            saveImage();
        }
        if (databaseHelper.getDiagram(flowchartName) != null){
            databaseHelper.updateDiagram(flowchartName, data);
        } else {
            databaseHelper.insertDiagram(flowchartName, data, pathToImage);
        }
    }

    public void setImageButton (View view){
        saveImage();
    }

    private void saveImage(){
        View content = drawingSurface;
        drawingSurface.prepareToSaveAsImage(true);
        content.setDrawingCacheEnabled(true);
        content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = content.getDrawingCache();
        FileOutputStream ostream;
        File folder = new File(pathToFolder);
        if (!folder.exists()){
            folder.mkdirs();
        }
        File file = new File (pathToImage);
        try {
            file.createNewFile();
            ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
            Toast.makeText(getApplicationContext(), "image saved" + pathToImage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
        drawingSurface.prepareToSaveAsImage(false);
    }

    private class LoadDiagramDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int nextId = 0;
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jShape = jsonArray.getJSONObject(i);
                    int id = jShape.getInt("id");
                    if (id >= nextId){
                        nextId = id + 1;
                    }
                    int width = jShape.getInt("width");
                    int height = jShape.getInt("height");
                    JSONObject jOrigin = jShape.getJSONObject("shapeOrigin");
                    int x = jOrigin.getInt("x");
                    int y = jOrigin.getInt("y");
                    String shapeTypeString = jShape.getString("shapetype");
                    Shape.SHAPETYPE shapetype = Shape.SHAPETYPE.valueOf(shapeTypeString);
                    String text = "";
                    if (jShape.has("text")){
                        JSONObject jText = jShape.getJSONObject("text");
                        text = jText.getString("string");
                    }
                    if (jShape.has("line")){
                        JSONObject jLine = jShape.getJSONObject("line");
                    }
                    switch (shapetype){
                        case WHILE:
                            drawingSurface.getShapes().add(new WhileShape(DrawingActivity.this, drawingSurface, x, y, width, height, shapetype, id));
                            break;
                        case CONDITION:
                            drawingSurface.getShapes().add(new ConditionShape(DrawingActivity.this, drawingSurface, x, y, width, height, shapetype, id));
                            break;
                        default:
                            drawingSurface.getShapes().add(new Shape(DrawingActivity.this, drawingSurface, x, y, width, height, shapetype, id));
                            break;
                    }
                    if (text.length() > 0){
                        drawingSurface.getShape(id).setText(new Text(drawingSurface, drawingSurface.getShape(id), text));
                    }
                    drawingSurface.setNextId(nextId);
                }
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jShape = jsonArray.getJSONObject(i);
                    int id = jShape.getInt("id");
                    String shapeTypeString = jShape.getString("shapetype");
                    Shape.SHAPETYPE shapetype = Shape.SHAPETYPE.valueOf(shapeTypeString);
                    switch (shapetype){
                        case CONDITION:
                            if (jShape.has("trueLine")){
                                JSONObject jTrueLine = jShape.getJSONObject("trueLine");
                                String firstPositionString = jTrueLine.getString("firstPosition");
                                Line.POSITION firstPosition = Line.POSITION.valueOf(firstPositionString);
                                String secondPositionString = jTrueLine.getString("secondPosition");
                                Line.POSITION secondPosition = Line.POSITION.valueOf(secondPositionString);
                                int firstShapeId = jTrueLine.getInt("firstShapeId");
                                int secondShapeId = jTrueLine.getInt("secondShapeId");
                                ConditionShape conditionShape = (ConditionShape) drawingSurface.getShape(id); 
                                conditionShape.setLineFromJSON(firstPosition, secondShapeId, secondPosition);
                            }
                            if (jShape.has("falseLine")){
                                JSONObject jFalseLine = jShape.getJSONObject("falseLine");
                                String firstPositionString = jFalseLine.getString("firstPosition");
                                Line.POSITION firstPosition = Line.POSITION.valueOf(firstPositionString);
                                String secondPositionString = jFalseLine.getString("secondPosition");
                                Line.POSITION secondPosition = Line.POSITION.valueOf(secondPositionString);
                                int firstShapeId = jFalseLine.getInt("firstShapeId");
                                int secondShapeId = jFalseLine.getInt("secondShapeId");
                                ConditionShape conditionShape = (ConditionShape) drawingSurface.getShape(id);
                                conditionShape.setLineFromJSON(firstPosition, secondShapeId, secondPosition);
                            }
                            break;
                        case WHILE:
                            if (jShape.has("line")){
                                JSONObject jLine = jShape.getJSONObject("line");
                                String firstPositionString = jLine.getString("firstPosition");
                                Line.POSITION firstPosition = Line.POSITION.valueOf(firstPositionString);
                                String secondPositionString = jLine.getString("secondPosition");
                                Line.POSITION secondPosition = Line.POSITION.valueOf(secondPositionString);
                                int firstShapeId = jLine.getInt("firstShapeId");
                                int secondShapeId = jLine.getInt("secondShapeId");
                                drawingSurface.getShape(id).setLineFromJSON(firstPosition, secondShapeId, secondPosition);
                            }
                            if (jShape.has("whileLine")){
                                JSONObject jWhileLine = jShape.getJSONObject("whileLine");
                                String firstPositionString = jWhileLine.getString("firstPosition");
                                Line.POSITION firstPosition = Line.POSITION.valueOf(firstPositionString);
                                String secondPositionString = jWhileLine.getString("secondPosition");
                                Line.POSITION secondPosition = Line.POSITION.valueOf(secondPositionString);
                                int firstShapeId = jWhileLine.getInt("firstShapeId");
                                int secondShapeId = jWhileLine.getInt("secondShapeId");
                                WhileShape whileShape = (WhileShape) drawingSurface.getShape(id);
                                whileShape.setLineFromJSON(firstPosition, secondShapeId, secondPosition);
                            }
                            break;
                        default:
                            if (jShape.has("line")){
                                JSONObject jLine = jShape.getJSONObject("line");
                                String firstPositionString = jLine.getString("firstPosition");
                                Line.POSITION firstPosition = Line.POSITION.valueOf(firstPositionString);
                                String secondPositionString = jLine.getString("secondPosition");
                                Line.POSITION secondPosition = Line.POSITION.valueOf(secondPositionString);
                                int firstShapeId = jLine.getInt("firstShapeId");
                                int secondShapeId = jLine.getInt("secondShapeId");
                                drawingSurface.getShape(id).setLineFromJSON(firstPosition, secondShapeId, secondPosition);
                            }
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            drawingSurface.invalidate();
        }
    }
}
