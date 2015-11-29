package com.sergiomse.quickquiz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sergiomse.quickquiz.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by sergiomse@gmail.com on 08/11/2015.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private static final String TAG = FolderAdapter.class.getName();

    public static class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        private static final String TAG = ViewHolder.class.getSimpleName();

        public LinearLayout rootLayout;
        public ImageView ivIcon;
        public TextView tvFolderName;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout      = (LinearLayout) itemView;
            ivIcon          = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvFolderName    = (TextView) itemView.findViewById(R.id.tvFolderName);

        }

    }


    public static class FolderItem {

        public final static int FOLDER_TYPE_UNDEFINED   = 0;
        public final static int FOLDER_TYPE_DIRECTORY   = 1;
        public final static int FOLDER_TYPE_PACKAGE     = 2;

        private File file;
        private int type;

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    private Context context;
    private File folder;
    private List<FolderItem> childrenDirs;
//    private Folder folder;
    private OnFolderClickListener listener;

    public FolderAdapter(Context context, File folder, OnFolderClickListener listener) {
        this.context = context;
        this.folder = folder;
        this.childrenDirs = new ArrayList<>();

        File[] dirs = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && !pathname.getAbsolutePath().trim().endsWith(".pkg");
            }
        });
        for ( File d : dirs ) {
            FolderItem fi = new FolderItem();
            fi.setFile( d );
            fi.setType( FolderItem.FOLDER_TYPE_DIRECTORY );
            this.childrenDirs.add( fi );
        }

        File[] pkgs = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getAbsolutePath().trim().endsWith(".pkg");
            }
        });
        for ( File p : pkgs ) {
            FolderItem fi = new FolderItem();
            fi.setFile( p );
            fi.setType( FolderItem.FOLDER_TYPE_PACKAGE );
            this.childrenDirs.add( fi );
        }

        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_folders, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final FolderItem fi = childrenDirs.get( position );
        if ( fi.getType() == FolderItem.FOLDER_TYPE_DIRECTORY ) {
            holder.ivIcon.setImageResource(R.drawable.img_folder);
            holder.tvFolderName.setText(fi.getFile().getName());

        } else if ( fi.getType() == FolderItem.FOLDER_TYPE_PACKAGE ) {
            holder.ivIcon.setImageResource(R.drawable.img_package);
            String name = childrenDirs.get(position).getFile().getName();
            if ( name.lastIndexOf(".pkg") != -1 ) {
                name = name.substring( 0, name.lastIndexOf('.') );
            }
            holder.tvFolderName.setText(name);

        } else {
            Log.d(TAG, "Undefined folder item type");
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFolderClick( fi );
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return childrenDirs.size();
    }

    public interface OnFolderClickListener {
        void onFolderClick(FolderItem folder);
    }
}
