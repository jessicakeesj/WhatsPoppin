package com.example.whatspoppin.view.editpreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.R;
import com.example.whatspoppin.viewmodel.EditPreferencesViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class EditPreferencesFragment extends Fragment {

    private ArrayList<String> categories_Selected = new ArrayList<>();
    private boolean receiveNotificationSwitchValue = false;
    private boolean showNearbyEventsSwitchValue = false;
    private ChipGroup preferenceCG;
    private Switch notificationSwitch, locationSwitch;
    private ProgressBar progressBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);
        final EditPreferencesViewModel model = ViewModelProviders.of(this).get(EditPreferencesViewModel.class);

        progressBar = root.findViewById(R.id.pBar);
        progressBar.setVisibility(View.VISIBLE);
        preferenceCG = root.findViewById(R.id.preference_chipgroup);
        notificationSwitch = root.findViewById(R.id.notification_switch);
        locationSwitch = root.findViewById(R.id.location_switch);

        model.getSelectedCategories().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> selected) {
                categories_Selected = selected;
            }
        });

        // Setup Category Chips
        preferenceCG.setChipSpacing(20);
        model.getEventCategories().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> category) {

                // category chips
                preferenceCG.removeAllViews();
                //categories_Selected = model.getSelectedCategories().getValue();
                int num = 0;
                for (String s : category) {
                    final Chip newChip = new Chip(getContext());
                    newChip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    newChip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null,
                            0, R.style.Widget_MaterialComponents_Chip_Filter));
                    newChip.setText(s);
                    newChip.setId(num);

                    if (categories_Selected.contains(s)){
                        newChip.setChecked(true);
                    }

                    newChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!categories_Selected.contains(newChip.getText().toString())) {
                                categories_Selected.add(newChip.getText().toString());
                            } else {
                                categories_Selected.remove(newChip.getText().toString());
                            }
                            model.updateCategoryFireStore(categories_Selected);
                        }
                    });
                    preferenceCG.addView(newChip);
                    num++;
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // notification toggle
        notificationSwitch.setChecked(receiveNotificationSwitchValue);
        model.getReceiveNotification().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean receiveNotification) {
                notificationSwitch.setChecked(receiveNotification);
            }
        });
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                receiveNotificationSwitchValue = !receiveNotificationSwitchValue;
                model.updateReceiveNotificationFireStore(receiveNotificationSwitchValue);
            }
        });

        // location toggle
        locationSwitch.setChecked(showNearbyEventsSwitchValue);
        model.getShowNearbyEvents().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean showNearbyEvents) {
                locationSwitch.setChecked(showNearbyEvents);
            }
        });
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showNearbyEventsSwitchValue = !showNearbyEventsSwitchValue;
                model.updateShowNearbyEventFireStore(showNearbyEventsSwitchValue);
            }
        });

        return root;
    }
}