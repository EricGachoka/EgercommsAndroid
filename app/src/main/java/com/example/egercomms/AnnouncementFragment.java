package com.example.egercomms;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.egercomms.data.DataHandler;
import com.example.egercomms.eventObjects.AnnouncementEventObject;
import com.example.egercomms.models.Announcement;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AnnouncementFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String TAG = "AnnFrag";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private DataHandler dataHandler = DataHandler.getInstance();
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private MyAnnouncementRecyclerViewAdapter adapter;
    private List<Announcement> announcements = Arrays.asList(new Announcement("No announcements","please connect to the internet"));

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AnnouncementFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AnnouncementFragment newInstance(int columnCount) {
        AnnouncementFragment fragment = new AnnouncementFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        EventBus.getDefault().register(this);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyAnnouncementRecyclerViewAdapter(announcements, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Announcement announcement);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshAnnouncementsAdapter(AnnouncementEventObject announcementEventObject){
        List<Announcement> announcements = announcementEventObject.getAnnouncements();
        //save state
//        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        this.announcements = announcements;
        recyclerView.setAdapter(new MyAnnouncementRecyclerViewAdapter(announcements, mListener));

        //restore state
//        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }
}
