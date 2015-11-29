package com.sergiomse.quickquiz.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.database.QuickQuizDAO;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = NoteActivity.class.getName();

    private EditText etNote;
    private String txtNote;

    String packageId;
    String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nota");

        Intent intent = getIntent();
        packageId  = intent.getStringExtra("packageId");
        questionId = intent.getStringExtra("questionId");

        TextView tvPackageId  = (TextView) findViewById(R.id.tvPackageId);
        TextView tvQuestionId = (TextView) findViewById(R.id.tvQuestionId);
        etNote = (EditText) findViewById(R.id.etNote);

        tvPackageId.setText( "Package Id: " + packageId );
        tvQuestionId.setText( "Question Id: " + questionId );

        txtNote = QuickQuizDAO.getNote(this, packageId, questionId);
        if ( txtNote != null ) {
            etNote.setText( txtNote );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ( txtNote == null ) {
            menu.removeItem(R.id.action_delete_note);
        }
        return true;
    }


    public void onCancel(View view) {
        finish();
    }

    public void onSave(View view) {
        if ( etNote.getText().toString().trim().isEmpty() ) {
            Toast.makeText(this, "Escribe algo antes", Toast.LENGTH_LONG).show();
            return;
        }

        if ( txtNote == null ) {
            QuickQuizDAO.insertNote(this, packageId, questionId, etNote.getText().toString());
        } else {
            QuickQuizDAO.updateNote(this, packageId, questionId, etNote.getText().toString());
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_note:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

//                LayoutInflater inflater = getLayoutInflater();
//                View customView = inflater.inflate(R.layout.filechooser_confirm, null);

                builder.setTitle("Borrar nota")
                        .setMessage("¿Seguro que quieres borrar la nota?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                QuickQuizDAO.deleteNote(NoteActivity.this, packageId, questionId);
                                NoteActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }
        return true;
    }
}
