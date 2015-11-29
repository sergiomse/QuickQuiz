package com.sergiomse.quickquiz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.filechooser.FileChooserAdapter;
import com.sergiomse.quickquiz.model.Note;

import java.io.File;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 23/11/2015.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = ViewHolder.class.getSimpleName();

        public LinearLayout parentLayout;
        public TextView tvPackageId;
        public TextView tvQuestionId;
        public TextView tvContent;
        public File file;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            tvPackageId  = (TextView) itemView.findViewById(R.id.tvPackageId);
            tvQuestionId = (TextView) itemView.findViewById(R.id.tvQuestionId);
            tvContent    = (TextView) itemView.findViewById(R.id.tvContent);
        }

    }

    private Context context;
    private List<Note> noteList;
    private OnNoteClickListener listener;

    public NoteListAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        this.listener = (OnNoteClickListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_note_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvPackageId.setText("Package Id: " + noteList.get( position ).getPackageId());
        holder.tvQuestionId.setText("Question Id: " + noteList.get( position ).getQuestionId());
        holder.tvContent.setText( noteList.get( position ).getContent() );

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteClick( noteList.get( position ) );
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }
}
