package com.sergiomse.quickquiz.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.adapters.FolderAdapter;
import com.sergiomse.quickquiz.adapters.NoteListAdapter;
import com.sergiomse.quickquiz.database.QuickQuizDAO;
import com.sergiomse.quickquiz.model.Note;

import java.io.File;
import java.util.List;

public class NoteListActivity extends AppCompatActivity implements NoteListAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Note list");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Note> noteList = QuickQuizDAO.getAllNotes(this);
        NoteListAdapter adapter = new NoteListAdapter(this, noteList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("packageId", note.getPackageId());
        intent.putExtra("questionId", note.getQuestionId());
        startActivity( intent );
    }
}
