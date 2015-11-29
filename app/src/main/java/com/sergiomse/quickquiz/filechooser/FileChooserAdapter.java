package com.sergiomse.quickquiz.filechooser;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sergiomse.quickquiz.R;

import java.io.File;

/**
 * Created by sergiomse@gmail.com on 23/11/2015.
 */
public class FileChooserAdapter extends RecyclerView.Adapter<FileChooserAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = ViewHolder.class.getSimpleName();

        public LinearLayout fileChooserRootLayout;
        public TextView tvFileName;
        public File file;

        public ViewHolder(View itemView) {
            super(itemView);
            fileChooserRootLayout = (LinearLayout) itemView.findViewById(R.id.fileChooserRootLayout);
            tvFileName = (TextView) itemView.findViewById(R.id.tvFileName);
        }

    }

    private Context context;
    private File folder;
    private File childrenFiles[];
    private OnFileChooserClickListener listener;

    public FileChooserAdapter(Context context, File folder, File childrenFiles[], OnFileChooserClickListener listener) {
        this.context = context;
        this.folder = folder;
        if(childrenFiles != null) {
            this.childrenFiles = childrenFiles;
        } else {
            this.childrenFiles = new File[0];
        }

        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filechooser_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (childrenFiles[position].isDirectory() ) {
            holder.fileChooserRootLayout.setBackgroundColor(Color.YELLOW);
        } else {
            holder.fileChooserRootLayout.setBackgroundColor(Color.CYAN);
        }
        holder.tvFileName.setText(childrenFiles[position].getName());
        holder.file = childrenFiles[position];
        holder.fileChooserRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFileClick(childrenFiles[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return childrenFiles.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnFileChooserClickListener {
        void onFileClick(File file);
    }
}
