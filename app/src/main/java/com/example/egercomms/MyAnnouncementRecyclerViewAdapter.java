package com.example.egercomms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.egercomms.AnnouncementFragment.OnListFragmentInteractionListener;
import com.example.egercomms.data.DataHandler;
import com.example.egercomms.dummy.DummyContent.DummyItem;
import com.example.egercomms.eventObjects.AnnouncementEventObject;
import com.example.egercomms.eventObjects.FilePathEventObject;
import com.example.egercomms.models.Announcement;
import com.example.egercomms.utils.NetworkHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
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
    private Context mContext;
    private DataHandler dataHandler = DataHandler.getInstance();

    public MyAnnouncementRecyclerViewAdapter(Context context, List<Announcement> items, OnListFragmentInteractionListener listener) {
        announcements = items;
        mListener = listener;
        mContext = context;
    }

    public void filter(String text) {
        List<Announcement> announcementsCopy = new ArrayList<>(dataHandler.getAnnouncements());
        announcements.clear();
        if (text.isEmpty()) {
            announcements.addAll(announcementsCopy);
        } else {
            text = text.toLowerCase();
            for (Announcement item : announcementsCopy) {
                if (item.getTitle().toLowerCase().contains(text) || item.getMessage().toLowerCase().contains(text)) {
                    announcements.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = announcements.get(position);
        holder.title.setText(returnNotNullString(announcements.get(position).getTitle()));
        holder.message.setText(returnNotNullString(announcements.get(position).getMessage()));
        holder.deadline.setText(returnNotNullString(announcements.get(position).getDeadline()));
        holder.updated.setText(returnNotNullString(announcements.get(position).getUpdated()));
        String attachment = announcements.get(position).getAttachments();
        if (attachment != null && !TextUtils.isEmpty(attachment)) {
            holder.attachment.setText(getFileName(attachment));
        } else {
            holder.attachment.setText("none");
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

    private String returnNotNullString(String name) {
        if (name != null && !TextUtils.isEmpty(name)) {
            return name;
        } else {
            return "none";
        }
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

            attachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filePath = mItem.getAttachments();
                    if (NetworkHelper.hasNetworkAccess(mContext) && filePath != null) {
                        FilePathEventObject filePathEventObject = new FilePathEventObject(filePath);
                        EventBus.getDefault().post(filePathEventObject);
                    } else if (!NetworkHelper.hasNetworkAccess(mContext) && filePath != null) {
                        Toast.makeText(mContext, "Network not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
