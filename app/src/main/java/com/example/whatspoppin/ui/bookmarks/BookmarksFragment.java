package com.example.whatspoppin.ui.bookmarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.BookmarkAdapter;
import com.example.whatspoppin.R;

public class BookmarksFragment extends Fragment {

    private BookmarksViewModel bookmarksViewModel;
    private BookmarkAdapter bookmarkAdapter;
    private ListView bookmarkList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookmarksViewModel = ViewModelProviders.of(this).get(BookmarksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        return root;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        bookmarkList = (ListView) view.findViewById(R.id.list_bookmarkList);
        bookmarkAdapter = new BookmarkAdapter(getActivity().getApplicationContext());
        bookmarkList.setAdapter(bookmarkAdapter);
    }
}