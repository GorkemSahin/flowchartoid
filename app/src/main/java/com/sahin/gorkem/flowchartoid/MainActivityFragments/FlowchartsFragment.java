package com.sahin.gorkem.flowchartoid.MainActivityFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import com.sahin.gorkem.flowchartoid.DatabaseUtils.DatabaseHelper;
import com.sahin.gorkem.flowchartoid.DatabaseUtils.Diagram;
import com.sahin.gorkem.flowchartoid.DrawingActivity;
import com.sahin.gorkem.flowchartoid.R;
import com.sahin.gorkem.flowchartoid.RecyclerViewUtils.FlowchartAdapter;

import java.util.ArrayList;
import java.util.List;

public class FlowchartsFragment extends Fragment implements FlowchartAdapter.ItemClickListener {

    private DatabaseHelper databaseHelper;
    private ArrayList<Diagram> diagrams;
    private FlowchartAdapter adapter;

    public FlowchartsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_flowcharts, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.flowchart_thumbnails_rv);
        final FloatingActionButton floatingActionButton = view.findViewById(R.id.new_flowchart_fab);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter the name of the flowchart diagram.");
                final EditText flowchartNameEditText = new EditText(getContext());
                flowchartNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(flowchartNameEditText);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String flowchartName = flowchartNameEditText.getText().toString();
                        if (flowchartName.length() < 2){
                            Toast.makeText(getContext(), "Invalid name.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getContext(), DrawingActivity.class);
                            intent.putExtra("flowchartName", flowchartName);
                            startActivity(intent);
                        }
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
        });
        databaseHelper = new DatabaseHelper(getContext());
        diagrams = new ArrayList<Diagram>();
        diagrams.addAll(databaseHelper.getAllDiagrams());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FlowchartAdapter(getActivity(), diagrams, this);
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onItemClick(int position) {
        Diagram diagram = diagrams.get(position);
        String name = diagram.getName();
        String data = diagram.getData();
        Intent intent = new Intent(getContext(), DrawingActivity.class);
        intent.putExtra("flowchartName", name);
        intent.putExtra("data", data);
        startActivity(intent);
    }

}
