package com.example.egercomms;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.egercomms.AnnouncementFragment.OnListFragmentInteractionListener;
import com.example.egercomms.dummy.DummyContent.DummyItem;
import com.example.egercomms.models.Announcement;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAnnouncementRecyclerViewAdapter extends RecyclerView.Adapter<MyAnnouncementRecyclerViewAdapter.ViewHolder> {

    private final List<Announcement> announcements;
    private final OnListFragmentInteractionListener mListener;
    public static final String TAG = "AnnAdapter";
    private StringBuilder builder = new StringBuilder();

    public MyAnnouncementRecyclerViewAdapter(List<Announcement> items, OnListFragmentInteractionListener listener) {
        announcements = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: "+announcements.get(position));
        holder.mItem = announcements.get(position);
        holder.title.setText(announcements.get(position).getTitle());
        holder.message.setText(announcements.get(position).getMessage());
        holder.deadline.setText(announcements.get(position).getDeadline());
        holder.updated.setText(announcements.get(position).getUpdated());
        String attachment = announcements.get(position).getAttachments();
        if (attachment != null) {
            holder.attachment.setText(getFileName(attachment));
        }else{
            holder.attachment.setText(attachment);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private String getFileName(String path) {
        return path.replaceAll(".*/", "");
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView message;
        public final TextView deadline;
        public final TextView attachment;
        public final TextView updated;
        public Announcement mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            message = (TextView) view.findViewById(R.id.messsage);
            deadline = (TextView) view.findViewById(R.id.deadline);
            attachment = (TextView) view.findViewById(R.id.attachments);
            updated = (TextView) view.findViewById(R.id.updated);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "title=" + title +
                    ", message=" + message +
                    ", deadline=" + deadline +
                    ", attachment=" + attachment +
                    '}';
        }
    }
}
