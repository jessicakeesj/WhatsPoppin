package com.example.whatspoppin.ui.recommendations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.R;
import com.example.whatspoppin.RecommendAdapter;

public class RecommendationsFragment extends Fragment {

    private RecommendationsViewModel recommendationsViewModel;
    private RecommendAdapter recommendAdapter;
    private ListView recommendList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recommendationsViewModel = ViewModelProviders.of(this).get(RecommendationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        return root;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        /*recommendList = (ListView) view.findViewById(R.id.list_bookmarkList);
        recommendAdapter = new RecommendAdapter(getActivity().getApplicationContext());
        recommendList.setAdapter(recommendAdapter);

        recommendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.text_recommendName);
                Toast.makeText(getActivity().getApplicationContext(), tv.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}