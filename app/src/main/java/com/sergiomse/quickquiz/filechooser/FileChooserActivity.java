package com.sergiomse.quickquiz.filechooser;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiomse.quickquiz.Q2Application;
import com.sergiomse.quickquiz.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileChooserActivity extends AppCompatActivity implements FileChooserAdapter.OnFileChooserClickListener{

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final String TAG = FileChooserActivity.class.getSimpleName();

    private File currentFolder;
    private File installationPath;
    private RecyclerView filesRecyclerView;
    private TextView tvCurrentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Package selector");

        Intent intent = getIntent();
        installationPath = new File( intent.getStringExtra("installationPath"));

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        if ( file.isDirectory() ) {
            currentFolder = file;
            updateRecyclerView();
        } else {
            processPackage(file);
        }
    }

    private void processPackage(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            String pckId = getZippedPackageId(zipFile);
            File installedPackageDir = searchInstalledPackage(pckId);

            confirmPackageInstallation(installedPackageDir, file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void confirmPackageInstallation(@Nullable final File installedPackageDir, final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.filechooser_confirm, null);

        String title = (installedPackageDir == null ? "¿Confirmar instalación de paquete?"
                            : "¿Confirmar actualización de paquete ya existente?");

        builder.setTitle(title)
                .setView(customView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (installedPackageDir != null) {
                            //package already existing
                            //remove before reinstall
                            removeDirRecursively( installedPackageDir );
                            installedPackageDir.delete();
                            installPackage(file, installedPackageDir.getParentFile());
                        } else {
                            installPackage(file, installationPath);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        TextView tvPath = (TextView) customView.findViewById(R.id.path);

        //remove the beginning of the path
        String appRootDir = ((Q2Application) getApplication()).getAppRootDir().getAbsolutePath();
        String displayPath = (installedPackageDir != null ? installedPackageDir.getAbsolutePath() .trim()
                                : installationPath.getAbsolutePath().trim());

        if (displayPath.startsWith(appRootDir)) {
            displayPath = displayPath.substring(appRootDir.length());
        }

        //if the package already exists remove the *.pkg from the end of the string
        if ( installedPackageDir != null && displayPath.endsWith(".pkg")) {
            displayPath = displayPath.substring(0, displayPath.lastIndexOf('/'));
        }

        if (displayPath.trim().isEmpty()) {
            displayPath = "/";
        }

        tvPath.setText( displayPath );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeDirRecursively(File file) {
        File children[] = file.listFiles();
        for (File child : children) {
            if ( child.isDirectory() ) {
                removeDirRecursively( child );
            }
            child.delete();
        }
    }

    private void installPackage(File zipFile, File destination) {
        String name = zipFile.getName();
        if ( name.indexOf(".zip") != -1 ) {
            name = name.substring( 0, name.lastIndexOf('.') );
        }

        //creating package directory
        File dstDir = new File(destination, name + ".pkg");
        if ( !dstDir.mkdirs() ) {
            Log.e(TAG, "Directory not created. Maybe it already exists");
        }

        if (!dstDir.exists()) {
            Toast.makeText(this, "No se puede crear el directorio del paquete", Toast.LENGTH_LONG).show();
            return;

        }

        //extract zip file
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                String filename = ze.getName();
                byte[] bytes = baos.toByteArray();

                FileOutputStream stream = new FileOutputStream( new File(dstDir, filename) );
                try {
                    stream.write(bytes);
                } finally {
                    if ( stream != null ) {
                        stream.close();
                    }
                }
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());

        } catch (IOException e) {
            Log.d(TAG, e.getMessage());

        } finally {
            if ( zis != null ) {
                try {
                    zis.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

    }

    private File searchInstalledPackage(String pckId) {
        if ( isAvailableRootFolder() ) {
            File rootDir = new File(getExternalFilesDir("Documents"), "QuickQuiz");
            File installedPackage = searchForPackageRecursive(rootDir, pckId);
            return installedPackage;
        }
        return null;
    }

    private File searchForPackageRecursive(File dir, String id) {
        File subdirs[] = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().trim().endsWith(".pkg");
            }
        });

        for ( File subdir : subdirs ) {
            File installed = searchForPackageRecursive(subdir, id);
            if ( installed != null ) {
                return installed;
            }
        }

        File packages[]  = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && file.getName().trim().endsWith(".pkg");
            }
        });

        for ( File pkg : packages ) {
            String installedId = ((Q2Application) getApplication()).getInstalledPackageId(pkg);
            if ( installedId.equals(id) ) {
                return pkg;
            }
        }

        return null;
    }

    private boolean isAvailableRootFolder() {
        if (isExternalStorageWritable()) {

            File rootDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "QuickQuiz");

            if (!rootDir.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }

            if (!rootDir.exists()) {
                Toast.makeText(this, "No se puede crear el directorio de la aplicación", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;

        } else {
            Toast.makeText(this, "No hay permisos de escritura en la memoria externa", Toast.LENGTH_LONG).show();
            return false;

        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String getZippedPackageId(ZipFile zipFile) throws IOException {
        String metadataContent = readZipFile(zipFile, ".metadata");
        String lines[] = metadataContent.split("\\n");
        for (String line : lines) {
            String fields[] = line.split(":", 2);
            if(fields.length == 2) {
                if(fields[0].trim().equals("id")) {
                    return fields[1].trim();
                }
            }
        }

        return "";
    }

    private String readZipFile(ZipFile zipFile, String entryName) throws IOException {
        ZipEntry entry = zipFile.getEntry( entryName );
        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream( entry ));

        byte buffer[] = new byte[1024];
        StringBuilder sb = new StringBuilder();
        int count;
        while( (count = bis.read(buffer)) != -1 ) {
            sb.append(new String(buffer, 0, count));
        }

        return sb.toString();
    }


    public void upFolder(View view) {
        if(currentFolder.getParentFile() != null) {
            currentFolder = currentFolder.getParentFile();
        }
        updateRecyclerView();
    }
}
