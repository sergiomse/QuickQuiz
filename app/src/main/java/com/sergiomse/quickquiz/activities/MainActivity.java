package com.sergiomse.quickquiz.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.adapters.FolderAdapter;
import com.sergiomse.quickquiz.database.QuickQuizDAO;
import com.sergiomse.quickquiz.model.Folder;

import java.io.File;


public class MainActivity extends AppCompatActivity implements FolderAdapter.OnFolderClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    private ProgressDialog progressDialog;
    private File rootFolder;
    private File folder;
    private RecyclerView foldersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quick Quiz");

        foldersRecyclerView = (RecyclerView) findViewById(R.id.foldersRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        foldersRecyclerView.setLayoutManager(layoutManager);

        checkRootFolder();

//        folder = QuickQuizDAO.getRootFolder(this);
        FolderAdapter adapter = new FolderAdapter(this, folder, this);
        foldersRecyclerView.setAdapter(adapter);

//        receiveData();
    }

    private void checkRootFolder() {
        if(isExternalStorageWritable()) {

            File rootDir = new File(getExternalFilesDir( Environment.DIRECTORY_DOCUMENTS), "QuickQuiz" );

            if (!rootDir.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }

            if( !rootDir.exists() ) {
                Toast.makeText(this, "No se puede crear el directorio de la aplicación", Toast.LENGTH_LONG).show();
                rootFolder = null;
                folder = null;
            }

            rootFolder = rootDir;
            folder = rootDir;
        } else {
            Toast.makeText(this, "No hay permisos de escritura en la memoria externa", Toast.LENGTH_LONG).show();
            rootFolder = null;
            folder = null;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private void drawFolders() {
        FolderAdapter adapter = new FolderAdapter(this, folder, this);
        foldersRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if(folder.equals(rootFolder)) {
            finish();
            return;
        }

//        folder = QuickQuizDAO.getParentFolder(this, folder);
        folder = folder.getParentFile();
        drawFolders();
    }

    @Override
    public void onFolderClick(File folder) {
        Log.d(TAG, "onFolderClick: " + folder.getName());

        this.folder = folder;
        drawFolders();
    }

    @Override
    public void onPlayFolderClick(Folder folder) {
        Log.d(TAG, "onPlayFolderClick: " + folder.getName());

        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra("folderName", folder.getName());
        intent.putExtra("folderId", folder.getId());
        startActivity(intent);
    }

    public void addFolder(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_add_folder, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        Folder folder = new Folder();
//                        folder.setName(((EditText) view.findViewById(R.id.etFolderName)).getText().toString());
//                        folder.setParentId(MainActivity.this.folder.getId());
//                        folder.setIsRoot(false);

//                        QuickQuizDAO.insertFolder(MainActivity.this, folder);

                        String name = ((EditText) view.findViewById(R.id.etFolderName)).getText().toString();
                        File newFolder = new File(MainActivity.this.folder, name);
                        if(!newFolder.mkdirs()) {
                            Toast.makeText(MainActivity.this, "No se puede crear el directorio de la aplicación", Toast.LENGTH_LONG).show();
                        }
                        MainActivity.this.drawFolders();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }

//    private void saveFolder(Folder folder) {
//        Folder parentFolder = getRootFolder(folder);
//        RequestQueue queue = null;
//        if(Configuration.getInstance().getProxyEnabled()) {
//            queue = Volley.newRequestQueue(this, new ProxiedHurlStack());
//        } else {
//            queue = Volley.newRequestQueue(this);
//        }
//        String url = Configuration.getInstance().getBaseUrl() + "/folders";
//
//        Gson gson = new Gson();
//        String jsonBody = gson.toJson(parentFolder, Folder.class);
//        JsonStringRequest stringRequest = new JsonStringRequest(Request.Method.POST, url, jsonBody,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
////                        Gson gson = new GsonBuilder().create();
////
////                        folder = gson.fromJson(response, Folder.class);
////
////                        createParentLinks(folder);
////
////                        drawFolders();
////
////                        if (progressDialog.isShowing()) {
////                            progressDialog.dismiss();
////                        }
//                    }
//                },
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        if (progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(MainActivity.this, "Error " + error, Toast.LENGTH_LONG).show();
//                    }
//                }
//        );
//
//        progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Loading...");
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }


}
