package com.example.egercomms;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.egercomms.JurisdictionFragment.OnListFragmentInteractionListener;
import com.example.egercomms.data.DataHandler;
import com.example.egercomms.dummy.DummyContent.DummyItem;
import com.example.egercomms.eventObjects.JurisdictionEventObject;
import com.example.egercomms.models.Jurisdiction;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyJurisdictionRecyclerViewAdapter extends RecyclerView.Adapter<MyJurisdictionRecyclerViewAdapter.ViewHolder>{

    private List<Jurisdiction> jurisdictions;
    private List<Jurisdiction> list;
    private final OnListFragmentInteractionListener mListener;
    private DataHandler dataHandler = DataHandler.getInstance();

    public MyJurisdictionRecyclerViewAdapter(List<Jurisdiction> items, OnListFragmentInteractionListener listener)

    {
        jurisdictions = items;
        mListener = listener;
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                list = (List<Jurisdiction>) results.values;
//                notifyDataSetChanged();
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                List<Jurisdiction> filteredResults = null;
//                if (constraint.length() == 0) {
//                    filteredResults = jurisdictions;
//                } else {
//                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
//                }
//
//                FilterResults results = new FilterResults();
//                results.values = filteredResults;
//                notifyDataSetChanged();
//                return results;
//            }
//        };
//    }

    public void filter(String text) {
        List<Jurisdiction> jurisdictionsCopy = dataHandler.getJurisdictions();
        jurisdictions.clear();
        if(text.isEmpty() && !jurisdictions.isEmpty()){
            jurisdictions.addAll(jurisdictionsCopy);
        } else{
            text = text.toLowerCase();
            for(Jurisdiction item: jurisdictionsCopy){
                if(item.getName().toLowerCase().contains(text)){
                    jurisdictions.add(item);
                }
            }
        }
        Log.e("FILTER", "filter: "+jurisdictions);
        JurisdictionEventObject jurisdictionEventObject = new JurisdictionEventObject(jurisdictions);
        EventBus.getDefault().post(jurisdictionEventObject);
    }

//    protected List<Jurisdiction> getFilteredResults(String constraint) {
//        List<Jurisdiction> results = new ArrayList<>();
//
//        for (Jurisdiction item : jurisdictions) {
//            if (item.getName().toLowerCase().contains(constraint)) {
//                results.add(item);
//            }
//        }
//        return results;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_jurisdiction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = jurisdictions.get(position);
        holder.mContentView.setText(jurisdictions.get(position).getName());
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
