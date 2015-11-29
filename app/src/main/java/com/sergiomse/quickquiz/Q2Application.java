package com.sergiomse.quickquiz;

import android.app.Application;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by sergiomse@gmail.com on 29/11/2015.
 */
public class Q2Application extends Application{

    private static final String TAG = Q2Application.class.getName();

    @Nullable
    public File getAppRootDir() {
        if (isExternalStorageWritable()) {

            File rootDir = new File(getExternalFilesDir("Documents"), "QuickQuiz");

            if (!rootDir.mkdirs()) {
                Log.e(TAG, "Directory not created. Maybe it already exists");
            }

            if (!rootDir.exists()) {
                Toast.makeText(this, "No se puede crear el directorio de la aplicación", Toast.LENGTH_LONG).show();
                return null;

            }

            return rootDir;

        } else {
            Toast.makeText(this, "No hay permisos de escritura en la memoria externa", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String getInstalledPackageId(File pkg) {
        String id = null;
        BufferedReader br = null;

        try {

            br = new BufferedReader(new FileReader(new File(pkg, ".metadata")));
            String line;

            while ((line = br.readLine()) != null) {
                String fields[] = line.split(":", 2);
                if ( fields.length == 2 ) {
                    if ( fields[0].trim().equals("id") ) {
                        id = fields[1].trim();
                        break;
                    }
                }
            }
            br.close();
        }

        catch (IOException e) {
            Log.d(TAG, e.getMessage());

        } finally {
            try {
                if ( br != null ) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return id;
    }
}
