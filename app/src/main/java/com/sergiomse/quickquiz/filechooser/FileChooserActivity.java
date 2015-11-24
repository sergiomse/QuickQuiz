package com.sergiomse.quickquiz.filechooser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.adapters.FolderAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class FileChooserActivity extends AppCompatActivity implements FileChooserAdapter.OnFileChooserClickListener{

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private File currentFolder;
    private RecyclerView filesRecyclerView;
    private TextView tvCurrentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Package selector");

        filesRecyclerView = (RecyclerView) findViewById(R.id.fileChooserRecyclerView);
        tvCurrentFolder = (TextView) findViewById(R.id.currentFolder);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        filesRecyclerView.setLayoutManager(layoutManager);

        currentFolder = Environment.getExternalStorageDirectory();

        //check if we have write file permission (API >= 23)
        checkWriteFilePermission();
    }


    private void updateRecyclerView() {
        File childrenFiles[] = currentFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                if (file.isFile() && file.getName().trim().endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });

        FileChooserAdapter adapter = new FileChooserAdapter(this, currentFolder, childrenFiles, this);
        filesRecyclerView.setAdapter(adapter);

        tvCurrentFolder.setText(currentFolder.getAbsolutePath());
    }


    private void checkWriteFilePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        } else {
            updateRecyclerView();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    updateRecyclerView();

                } else {

                    Toast.makeText(this, "No tiene permisos para leer ficheros en el almacenamiento externo", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public void onFileClick(File file) {
        currentFolder = file;
        updateRecyclerView();
    }

    public void upFolder(View view) {
        if(currentFolder.getParentFile() != null) {
            currentFolder = currentFolder.getParentFile();
        }
        updateRecyclerView();
    }
}
