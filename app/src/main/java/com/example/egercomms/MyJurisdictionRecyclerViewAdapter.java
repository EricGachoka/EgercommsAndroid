package com.example.egercomms;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.egercomms.JurisdictionFragment.OnListFragmentInteractionListener;
import com.example.egercomms.data.DataHandler;
import com.example.egercomms.dummy.DummyContent.DummyItem;
import com.example.egercomms.eventObjects.AccountEventObject;
import com.example.egercomms.models.Account;
import com.example.egercomms.models.Jurisdiction;
import com.example.egercomms.models.Staff;
import com.example.egercomms.models.User;
import com.example.egercomms.services.staff.StaffService;
import com.example.egercomms.utils.NetworkHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyJurisdictionRecyclerViewAdapter extends RecyclerView.Adapter<MyJurisdictionRecyclerViewAdapter.ViewHolder> {

    private List<Account> accounts;
    public static final String JURISDICTION = "jurisdiction";
    public static final String TAG = "JAdapter";
    private List<Jurisdiction> list;
    private final OnListFragmentInteractionListener mListener;
    private DataHandler dataHandler = DataHandler.getInstance();
    private Staff mStaff = new Staff(new User("", ""));
    private Context context;
    private String BASE_URL = "https://gachokaeric.pythonanywhere.com";

    public MyJurisdictionRecyclerViewAdapter(Context context, List<Account> items, OnListFragmentInteractionListener listener) {
        this.context = context;
        if (accounts != null) {
            setItems(items);
        } else {
            accounts = items;
        }
        mListener = listener;
    }

    public void setItems(List<Account> newAccounts) {
        //get the current items
        int currentSize = accounts.size();
        //remove the current items
        accounts.clear();
        //add all the new items
        accounts.addAll(newAccounts);
        //tell the recycler view that all the old items are gone
        notifyItemRangeRemoved(0, currentSize);
        //tell the recycler view how many new items we added
        notifyItemRangeInserted(0, newAccounts.size());
//        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_jurisdiction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Account account = accounts.get(position);
        User user = account.getStaff().getUser();
        String url = account.getStaff().getPhoto();
        holder.mItem = account.getJurisdiction();
        holder.jurisdictionName.setText(account.getJurisdiction().getName());
        holder.staffName.setText(user.getFullName());
        if (url != null) {
            Picasso.with(context)
                    .load(BASE_URL + url)
                    .into(holder.staffImage);
        }
//        String gender = account.getStaff().getGender();
//        if (gender != null) {
//            if(gender.equals("F") && url==null){
//                holder.staffImage.setBackgroundResource(R.mipmap.woman_icon);
//            }
//        }
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

    public void filter(String text) {
        List<Account> accountsCopy = dataHandler.getAccounts();
        accounts.clear();
        if (text.isEmpty() && !accounts.isEmpty()) {
            accounts.addAll(accountsCopy);
        } else {
            text = text.toLowerCase();
            for (Account item : accountsCopy) {
                if (item.getJurisdiction().getName().toLowerCase().contains(text)) {
                    accounts.add(item);
                }
            }
        }
        AccountEventObject accountEventObject = new AccountEventObject(accounts);
        EventBus.getDefault().post(accountEventObject);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView jurisdictionName;
        private final TextView staffName;
        private final ImageView staffImage;
        private Jurisdiction mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            jurisdictionName = (TextView) view.findViewById(R.id.jurisdiction);
            staffName = (TextView) view.findViewById(R.id.staffName);
            staffImage = (ImageView) view.findViewById(R.id.staffImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + jurisdictionName.getText() + "'";
        }
    }
}
