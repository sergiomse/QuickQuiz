package com.sergiomse.quickquiz.database;

import android.content.Context;

import com.sergiomse.quickquiz.activities.MainActivity;
import com.sergiomse.quickquiz.model.Folder;

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
}
