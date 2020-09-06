package com.sahin.gorkem.flowchartoid.RecyclerViewUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sahin.gorkem.flowchartoid.DatabaseUtils.Diagram;
import com.sahin.gorkem.flowchartoid.MainActivity;
import com.sahin.gorkem.flowchartoid.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gorkem on 2/14/2018.
 */

public class FlowchartAdapter extends RecyclerView.Adapter<FlowchartAdapter.ViewHolder> {

    private ArrayList<Diagram> diagrams;
    private LayoutInflater mInflater;
    final private ItemClickListener itemClickListener;
    private Context context;

    // data is passed into the constructor
    public FlowchartAdapter(Context context, ArrayList<Diagram> diagrams, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.diagrams = diagrams;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.flowchart_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String flowchartName = diagrams.get(position).getName();
        holder.tv_flowchart_name.setText(flowchartName);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name) + "/" + flowchartName + ".png";
        File file = new File(path);
        if (file.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            int a = bitmap.getWidth();
            int b = bitmap.getHeight();
            holder.iv_flowchart_thumbnail.setImageBitmap(bitmap);
        } else {
            holder.iv_flowchart_thumbnail.setImageDrawable(context.getDrawable(R.drawable.ic_image));
        }

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return diagrams.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_flowchart_name;
        ImageView iv_flowchart_thumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            tv_flowchart_name = (TextView) itemView.findViewById(R.id.flowchart_name_tv);
            iv_flowchart_thumbnail = (ImageView) itemView.findViewById(R.id.flowchart_thumbnail_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }

    Diagram getItem(int id) {
        return diagrams.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}