package com.sergiomse.quickquiz.filechooser;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.adapters.FolderAdapter;

import java.io.File;

public class FileChooserActivity extends AppCompatActivity {

    private File currentFolder;
    private RecyclerView filesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select package file");

        currentFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        filesRecyclerView = (RecyclerView) findViewById(R.id.fileChooserRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        filesRecyclerView.setLayoutManager(layoutManager);
        FileChooserAdapter adapter = new FileChooserAdapter(this, currentFolder);
        filesRecyclerView.setAdapter(adapter);
    }

}
