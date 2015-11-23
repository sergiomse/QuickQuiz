package com.sergiomse.quickquiz.filechooser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiomse.quickquiz.R;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by sergiomse@gmail.com on 23/11/2015.
 */
public class FileChooserAdapter extends RecyclerView.Adapter<FileChooserAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        private static final String TAG = ViewHolder.class.getSimpleName();

//        private OnFolderClickListener listener;

        public TextView tvFileName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFileName = (TextView) itemView.findViewById(R.id.tvFileName);
        }

//        public void setOnFolderClickListener(OnFolderClickListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        public void onClick(View v) {
//            listener.onThingItemClick((Long) rootLayout.getTag());
//        }
    }

    private Context context;
    private File folder;
    private File childrenFiles[];

    public FileChooserAdapter(Context context, File folder/*, OnFolderClickListener listener*/) {
        this.context = context;
        this.folder = folder;
        this.childrenFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
//        this.folderChildren = QuickQuizDAO.getChildrenFolders(context, folder);
//        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filechooser_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
//        vh.setOnFolderClickListener(listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvFileName.setText(childrenFiles[position].getName());
    }

    @Override
    public int getItemCount() {
        return childrenFiles.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
