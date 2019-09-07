package com.example.whatspoppin.ui.eventlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.EventAdapter;
import com.example.whatspoppin.R;

public class EventListFragment extends ListFragment {

    private EventListViewModel eventListViewModel;
    private EventAdapter eventAdapter;
    private ListView eventList;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventListViewModel = ViewModelProviders.of(this).get(EventListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eventlist, container, false);
        return root;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        eventList = (ListView) view.findViewById(R.id.list_eventList);
        eventAdapter = new EventAdapter(getActivity().getApplicationContext());
        eventList.setAdapter(eventAdapter);
    }
}