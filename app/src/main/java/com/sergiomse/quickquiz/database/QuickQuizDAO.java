package com.sergiomse.quickquiz.database;

import android.content.Context;

import com.sergiomse.quickquiz.activities.MainActivity;
import com.sergiomse.quickquiz.activities.NoteActivity;
import com.sergiomse.quickquiz.activities.NoteListActivity;
import com.sergiomse.quickquiz.model.Folder;
import com.sergiomse.quickquiz.model.Note;

import java.util.List;

/**
 * Created by sergiomse@gmail.com on 21/11/2015.
 */
public class QuickQuizDAO {
    public static Folder getRootFolder(Context context) {
        QuickQuizDB db = new QuickQuizDB(context);
        Folder root = db.getFolderByColumnValue("root", "1");
        db.cleanup();
        return root;
    }

    public static List<Folder> getChildrenFolders(Context context, Folder folder) {
        QuickQuizDB db = new QuickQuizDB(context);
        List<Folder> folders = db.getFolderListByColumnValue("parent", String.valueOf(folder.getId()));
        db.cleanup();
        return folders;
    }

    public static Folder getParentFolder(Context context, Folder folder) {
        QuickQuizDB db = new QuickQuizDB(context);
        Folder parentFolder = db.getFolderByColumnValue("_id", String.valueOf(folder.getParentId()));
        db.cleanup();
        return parentFolder;
    }

    public static void insertFolder(Context context, Folder folder) {
        QuickQuizDB db = new QuickQuizDB(context);
        db.insertFolder(folder);
        db.cleanup();
    }

    public static String getNote(Context context, String packageId, String questionId) {
        QuickQuizDB db = new QuickQuizDB(context);
        String note = db.getNote(packageId, questionId);
        db.cleanup();

        return note;
    }

    public static void insertNote(Context context, String packageId, String questionId, String note) {
        QuickQuizDB db = new QuickQuizDB(context);
        db.insertNote(packageId, questionId, note);
        db.cleanup();
    }

    public static void deleteNote(Context context, String packageId, String questionId) {
        QuickQuizDB db = new QuickQuizDB(context);
        db.deleteNote(packageId, questionId);
        db.cleanup();
    }

    public static void updateNote(Context context, String packageId, String questionId, String note) {
        QuickQuizDB db = new QuickQuizDB(context);
        db.updateNote(packageId, questionId, note);
        db.cleanup();
    }

    public static List<Note> getAllNotes(Context context) {
        QuickQuizDB db = new QuickQuizDB(context);
        List<Note> noteList = db.getAllNote();
        db.cleanup();

        return noteList;
    }
}
