package com.sergiomse.quickquiz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.database.QuickQuizDAO;
import com.sergiomse.quickquiz.model.Folder;

import java.util.List;


/**
 * Created by sergiomse@gmail.com on 08/11/2015.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        private static final String TAG = ViewHolder.class.getSimpleName();

        private OnFolderClickListener listener;

        public LinearLayout rootLayout;
        public TextView tvFolderName;
        public ImageView playTests;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (LinearLayout) itemView;
            tvFolderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            playTests = (ImageView) itemView.findViewById(R.id.ivPlayTests);

        }

        public void setOnFolderClickListener(OnFolderClickListener listener) {
            this.listener = listener;
        }
//
//        @Override
//        public void onClick(View v) {
//            listener.onThingItemClick((Long) rootLayout.getTag());
//        }
    }



    private Context context;
    private Folder folder;
    private List<Folder> folderChildren;
//    private Folder folder;
    private OnFolderClickListener listener;

    public FolderAdapter(Context context, Folder folder, OnFolderClickListener listener) {
        this.context = context;
        this.folder = folder;
        this.folderChildren = QuickQuizDAO.getChildrenFolders(context, folder);
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_folders, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.setOnFolderClickListener(listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvFolderName.setText(folderChildren.get(position).getName());

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFolderClick(folderChildren.get(position));
            }
        });
//        holder.playTests.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onPlayFolderClick(folder.getChildren().get(position));
//            }
//        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return folderChildren.size();
    }

    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
        void onPlayFolderClick(Folder folder);
    }
}
