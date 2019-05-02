package com.example.egercomms;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.egercomms.JurisdictionFragment.OnListFragmentInteractionListener;
import com.example.egercomms.dummy.DummyContent.DummyItem;
import com.example.egercomms.models.Jurisdiction;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyJurisdictionRecyclerViewAdapter extends RecyclerView.Adapter<MyJurisdictionRecyclerViewAdapter.ViewHolder> {

    private final List<Jurisdiction> jurisdictions;
    private final OnListFragmentInteractionListener mListener;

    public MyJurisdictionRecyclerViewAdapter(List<Jurisdiction> items, OnListFragmentInteractionListener listener) {
        jurisdictions = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_jurisdiction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e("BIND", "onBindJurisdictions: "+ jurisdictions.toString());
        holder.mItem = jurisdictions.get(position);
        holder.mContentView.setText(jurisdictions.get(position).getName());
        Log.e("BIND", "onBindViewHolder: " + jurisdictions.get(position).getName());
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

    @Override
    public int getItemCount() {
        return jurisdictions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Jurisdiction mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
