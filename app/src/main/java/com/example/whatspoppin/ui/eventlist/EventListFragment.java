package com.example.whatspoppin.ui.eventlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.EventAdapter;
import com.example.whatspoppin.R;

import java.util.ArrayList;

public class EventListFragment extends ListFragment {

    private EventListViewModel eventListViewModel;
    private EventAdapter eventAdapter;
    private ListView simpleList;
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventListViewModel = ViewModelProviders.of(this).get(EventListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eventlist, container, false);

        return root;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        simpleList = getListView();
        //ArrayAdapter<Event> arrayAdapter = new ArrayAdapter<Event>(getActivity(), R.layout.eventlist_item, R.id.textView, eventArrayList);
        eventAdapter = new EventAdapter(getActivity().getApplicationContext());
        simpleList.setAdapter(eventAdapter);
    }

    /*public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventListViewModel =
                ViewModelProviders.of(this).get(EventListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eventlist, container, false);
        final TextView textView = root.findViewById(R.id.text_eventlist);
        eventListViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }*/
}