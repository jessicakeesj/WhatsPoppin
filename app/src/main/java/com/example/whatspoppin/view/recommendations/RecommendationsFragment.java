package com.example.whatspoppin.view.recommendations;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.adapter.RecommendAdapter;
import com.example.whatspoppin.view.eventlist.EventDetailsFragment;
import com.example.whatspoppin.viewmodel.RecommendationsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class RecommendationsFragment extends ListFragment {
    private ArrayList<Event> bookmarkArrayList = new ArrayList<>();
    private RecommendAdapter recommendAdapter;
    private ListView eventList;
    private EditText search;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;
    private RecommendationsViewModel recommendationsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recommendationsViewModel = ViewModelProviders.of(this).get(RecommendationsViewModel.class);
        RecommendationsViewModel model = ViewModelProviders.of(this).get(RecommendationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recommendations, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        eventList = (ListView) root.findViewById(android.R.id.list);
        search = (EditText) root.findViewById(R.id.rec_inputSearch);

        model.getRecommendationList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> recommendations) {
                recommendAdapter = new RecommendAdapter(getActivity(), recommendations);
                eventList.setAdapter(recommendAdapter);
                recommendAdapter.notifyDataSetChanged();
            }
        });

        model.getBookmarkList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> bookmarks) {
                bookmarkArrayList = bookmarks;
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
                    Intent intent = new Intent(getContext(), EventDetailsFragment.class);
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
                RecommendationsFragment.this.recommendAdapter.getFilter().filter(s);
                eventList.setAdapter(recommendAdapter);
                recommendAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}