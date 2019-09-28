/*
package com.example.whatspoppin.ui.bookmarks;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.BookmarkAdapter;
import com.example.whatspoppin.Event;
import com.example.whatspoppin.EventAdapter;
import com.example.whatspoppin.R;
import com.example.whatspoppin.ui.bookmarks.BookmarksViewModel;
import com.example.whatspoppin.ui.eventlist.EventDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

public class BookmarkListFragment extends ListFragment {
    private ArrayList<Event> bookmarkArrayList = new ArrayList<Event>();
    private BookmarksViewModel bookmarksListViewModel;
    private BookmarkAdapter bookmarkAdapter;
    private ListView eventList;
    private EditText search;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersDoc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookmarksListViewModel = ViewModelProviders.of(this).get(BookmarksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        eventList = (ListView) view.findViewById(R.id.bookmarks_eventList);
        search = (EditText) view.findViewById(R.id.bookmarks_inputSearch);

        getBookmarksFirestore();

        bookmarkAdapter = new BookmarkAdapter(getActivity().getApplicationContext(), bookmarkArrayList);
        eventList.setAdapter(bookmarkAdapter);
        bookmarkAdapter.notifyDataSetChanged();

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.text_eventName);
                String[] eventName = tv.getText().toString().split("\n");
                //Toast.makeText(getActivity().getApplicationContext(), eventName[0], Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), EventDetailsFragment.class);
                Bundle args = new Bundle();
                args.putSerializable("EVENTLIST", (Serializable) bookmarkArrayList);
                intent.putExtra("BUNDLE", args);
                intent.putExtra("eventName", eventName[0].trim());
                startActivity(intent);
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BookmarkListFragment.this.bookmarkAdapter.getFilter().filter(s);
                eventList.setAdapter(bookmarkAdapter);
                bookmarkAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void getBookmarksFirestore(){
        bookmarkArrayList.clear();

        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getString("userEmail");
                        bookmarkArrayList = (ArrayList<Event>) document.get("bookmarks");
                        Log.d("getBookmarks", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("getBookmarks", "No such document");
                    }
                } else {
                    Log.d("getBookmarks", "get failed with ", task.getException());
                }
            }
        });
    }
}
*/
