package com.example.whatspoppin.ui.editpreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.R;

public class EditPreferencesFragment extends Fragment {

    private EditPreferencesViewModel editPreferencesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editPreferencesViewModel =
                ViewModelProviders.of(this).get(EditPreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        editPreferencesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}