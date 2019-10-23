package com.example.whatspoppin.view.editpreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class EditPreferencesFragment extends Fragment {

    private ArrayList<String> categories_Selected = new ArrayList<>();
    private boolean receiveNotification = false;
    private boolean showNearbyEvents = false;

    private EditPreferencesViewModel editPreferencesViewModel;

    private ChipGroup preferenceCG;
    private Switch notificationSwitch, locationSwitch;

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editPreferencesViewModel = ViewModelProviders.of(this).get(EditPreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);

        final EditPreferencesViewModel model = ViewModelProviders.of(this).get(EditPreferencesViewModel.class);

        // get current login user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        preferenceCG = root.findViewById(R.id.preference_chipgroup);
        notificationSwitch = root.findViewById(R.id.notification_switch);
        locationSwitch = root.findViewById(R.id.location_switch);

        // Setup Category Chips
        preferenceCG.setChipSpacing(20);
        model.getEventCategories().observe(this, new Observer<ArrayList<String>>() {
                @Override
                public void onChanged(ArrayList<String> category) {
                // category chips
                int num = 0;
                for (String s : category) {
                    final Chip newChip = new Chip(getContext());
                    newChip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    newChip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null,
                            0, R.style.Widget_MaterialComponents_Chip_Filter));
                    newChip.setText(s);
                    newChip.setId(num);
                    if (categories_Selected.contains(s)) newChip.setChecked(true);
                    newChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!categories_Selected.contains(newChip.getText().toString())) {
                                categories_Selected.add(newChip.getText().toString());
                            } else {
                                categories_Selected.remove(newChip.getText().toString());
                            }
                            updatePreferenceFirestore();
                        }
                    });
                    preferenceCG.addView(newChip);
                    num++;
                }
            }
        });

        // Set Selected Category Chips
//        model.getSelectedCategories().;

        // notification toggle
        if (receiveNotification) notificationSwitch.setChecked(true);
        else notificationSwitch.setChecked(false);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (receiveNotification) {
                    notificationSwitch.setChecked(false);
                    receiveNotification = false;
                } else {
                    notificationSwitch.setChecked(true);
                    receiveNotification = true;
                }
                model.updateReceiveNotificationFirestore(receiveNotification);
            }
        });

        // location toggle
        if (showNearbyEvents) locationSwitch.setChecked(true);
        else locationSwitch.setChecked(false);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (showNearbyEvents) {
                    locationSwitch.setChecked(false);
                    showNearbyEvents = false;
                } else {
                    locationSwitch.setChecked(true);
                    showNearbyEvents = true;
                }
                model.updateShowNearbyEventFirestore(showNearbyEvents);
            }
        });

        return root;
    }

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            usersDoc = db.collection("users").document(currentUser.getUid());
//        }
//        return root;
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        getEventCategories();
//        preferenceCG = view.findViewById(R.id.preference_chipgroup);
//        notificationSwitch = view.findViewById(R.id.notification_switch);
//        locationSwitch = view.findViewById(R.id.location_switch);
//    }

    //checks firestore for realtime updates
//    public void realtimeFireStoreData() {
//        usersDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("Listen", "Listen failed.", e);
//                    return;
//                }
//                if (snapshot != null && snapshot.exists()) {
//                    getEventCategories();
//                    getPreferenceFirestore();
//                } else {
//                    getEventCategories();
//                    getPreferenceFirestore();
//                }
//            }
//        });
//    }


    public void updatePreferenceFirestore() {
        usersDoc.update("interests", categories_Selected)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updatePreferences", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updatePreferences", "Error updating document", e);
                    }
                });

        usersDoc.update("receiveNotification", receiveNotification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updatePreferences", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updatePreferences", "Error updating document", e);
                    }
                });

        usersDoc.update("showNearbyEvents", showNearbyEvents)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updatePreferences", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updatePreferences", "Error updating document", e);
                    }
                });
    }

    public void getPreferenceFirestore() {
        categories_Selected.clear();
        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        categories_Selected.clear();
                        String b = String.valueOf(document.get("interests"));
                        if (b != "null" || b != null || b != "[]") {
                            ArrayList<String> bkm = (ArrayList<String>) document.get("interests");
                            for (String testMap : bkm) {
                                String name = testMap;
                                categories_Selected.add(name);
                            }
                        } else categories_Selected = null;
                        receiveNotification = document.getBoolean("receiveNotification");
                        showNearbyEvents = document.getBoolean("showNearbyEvents");

                        Log.d("getPreferences", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("getPreferences", "No such document");
                    }
                    //setupView();
                } else {
                    Log.d("getPreferences", "get failed with ", task.getException());
                }
            }
        });
    }
}