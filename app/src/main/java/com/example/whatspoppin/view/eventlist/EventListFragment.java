package com.example.whatspoppin.view.eventlist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.R;
import com.example.whatspoppin.adapter.EventAdapter;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.viewmodel.EventListViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventListFragment extends ListFragment {
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private ArrayList<Event> fil_eventArrayList = new ArrayList<Event>();
    private ArrayList<Event> bookmarkArrayList = new ArrayList<Event>();
    private ArrayList<String> categories_Selected = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ListView eventList;
    private EditText search;
    private ChipGroup categoryCG;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;
    private EventListViewModel eventListViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventListViewModel = ViewModelProviders.of(this).get(EventListViewModel.class);
        final EventListViewModel model = ViewModelProviders.of(this).get(EventListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eventlist, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        categoryCG = root.findViewById(R.id.allEvent_chipgroup);
        eventList = (ListView) root.findViewById(android.R.id.list);
        search = (EditText) root.findViewById(R.id.eventlist_inputSearch);

        model.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                eventArrayList = events;
                eventAdapter = new EventAdapter(getActivity(), events);
                eventList.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
            }
        });

        model.getBookmarkList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> bookmarks) {
                bookmarkArrayList = bookmarks;
            }
        });

        model.getEventCategories().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> category) {
                // category chips
                categoryCG.removeAllViews();
                int num = 0;
                for (String s : category) {
                    final Chip newChip = new Chip(getContext());
                    newChip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    newChip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null,
                            0, R.style.Widget_MaterialComponents_Chip_Filter));
                    newChip.setText(s);
                    newChip.setId(num);

                    newChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!categories_Selected.contains(newChip.getText().toString())) {
                                categories_Selected.add(newChip.getText().toString());
                            } else {
                                categories_Selected.remove(newChip.getText().toString());
                            }

                            if(categories_Selected.isEmpty()){
                                eventAdapter = new EventAdapter(getActivity(), eventArrayList);
                                eventList.setAdapter(eventAdapter);
                                eventAdapter.notifyDataSetChanged();
                            }else{
                                model.getFilteredEventList(categories_Selected).observe(getActivity(), new Observer<ArrayList<Event>>() {
                                    @Override
                                    public void onChanged(ArrayList<Event> events) {
                                        fil_eventArrayList = events;
                                        eventAdapter = new EventAdapter(getActivity(), fil_eventArrayList);
                                        eventList.setAdapter(eventAdapter);
                                        eventAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                    categoryCG.addView(newChip);
                    num++;
                }
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event clickedEvent = (Event) adapterView.getItemAtPosition(position);
                // open event details
                try{
                    Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("EVENT", clickedEvent);
                    args.putSerializable("BOOKMARKLIST", bookmarkArrayList);
                    intent.putExtra("BUNDLE", args);
                    startActivity(intent);
                }catch(Exception e ){
                    Log.e("START ACTIVITY", e.getMessage());
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventListFragment.this.eventAdapter.getFilter().filter(s);
                eventList.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}