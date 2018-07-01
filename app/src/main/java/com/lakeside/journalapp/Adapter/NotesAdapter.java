package com.lakeside.journalapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lakeside.journalapp.Data.Note;
import com.lakeside.journalapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context mContext;
    private List<Note> mNotes;
    // Member variable to handle item clicks
    private ItemClickListener mItemClickListener;

    public NotesAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_item_layout, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {

        //Get the note at the current position
        Note note = mNotes.get(position);
        //Set the other values required
        holder.noteTitleView.setText(note.getTitle());
        holder.noteDescriptionView.setText(note.getDescription());

        holder.updatedAtView.setText(getFormattedTime(note.getUpDatedAt()));

    }



    @Override
    public int getItemCount() {

        if (mNotes == null) {
            return 0;
        }
        return mNotes.size();
    }

    public void setNote(List<Note> notes) {
        mNotes = notes;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String noteId);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

       TextView noteTitleView;
       TextView noteDescriptionView;
       TextView updatedAtView;

       public NoteViewHolder(View itemView) {
           super(itemView);

           noteTitleView = itemView.findViewById(R.id.noteTitle);
           noteDescriptionView = itemView.findViewById(R.id.noteDescription);
           updatedAtView = itemView.findViewById(R.id.noteUpdatedAt);
           itemView.setOnClickListener(this);
       }

       @Override
       public void onClick(View v) {
            String noteId = mNotes.get(getAdapterPosition()).getNoteId();
           mItemClickListener.onItemClickListener(noteId);
       }
   }


    public String getFormattedTime(String epoch){


        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        Date dateTime = new Date(Long.valueOf(epoch));
        String timeString = formatter.format(dateTime);

        return timeString;
    }
}
