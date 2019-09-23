package com.example.whatspoppin.ui.editpreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class EditPreferencesFragment extends Fragment {

    private EditPreferencesViewModel editPreferencesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editPreferencesViewModel =
                ViewModelProviders.of(this).get(EditPreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);
//        final TextView textView = root.findViewById(R.id.text_editpreferences);
//        editPreferencesViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        // Populate Preferences Category
        ChipGroup preferenceCG = root.findViewById(R.id.preference_chipgroup);
        String[] categories = {"Attractions", "Conference", "Convention", "Expo", "Festival", "Gala", "Game",
                "Network", "Party", "Performance", "Race", "Rally", "Retreat", "Screening", "Seminar", "Tour", "Tournament"};

        for (int i = 0; i < categories.length; i++) {
            Chip newChip = new Chip(getContext());
            newChip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            newChip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null,
                    0, R.style.Widget_MaterialComponents_Chip_Filter));
            newChip.setText(categories[i]);
            preferenceCG.addView(newChip);

        }

        return root;
    }
}