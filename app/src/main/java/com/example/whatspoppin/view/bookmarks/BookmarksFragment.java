package com.example.whatspoppin.view.bookmarks;

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
import com.example.whatspoppin.R;
import com.example.whatspoppin.adapter.BookmarkAdapter;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.view.eventlist.EventDetailsActivity;
import com.example.whatspoppin.viewmodel.BookmarksViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BookmarksFragment extends ListFragment {
    private ArrayList<Event> bookmarkArrayList = new ArrayList<Event>();
    private BookmarkAdapter bookmarkAdapter;
    private ListView eventList;
    private EditText search;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersDoc;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private BookmarksViewModel bookmarksViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookmarksViewModel = ViewModelProviders.of(this).get(BookmarksViewModel.class);
        BookmarksViewModel model = ViewModelProviders.of(this).get(BookmarksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        eventList = (ListView) root.findViewById(android.R.id.list);
        search = (EditText) root.findViewById(R.id.bookmarks_inputSearch);

        model.getBookmarkList().observe(this, new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> bookmarks) {
                bookmarkArrayList = bookmarks;
                bookmarkAdapter = new BookmarkAdapter(getActivity(), bookmarks);
                eventList.setAdapter(bookmarkAdapter);
                bookmarkAdapter.notifyDataSetChanged();
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
                BookmarksFragment.this.bookmarkAdapter.getFilter().filter(s);
                eventList.setAdapter(bookmarkAdapter);
                bookmarkAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}