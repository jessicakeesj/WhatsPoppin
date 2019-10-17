package com.example.whatspoppin.ui.editpreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class EditPreferencesFragment extends Fragment {

    private EditPreferencesViewModel editPreferencesViewModel;
    private ArrayList<String> eventCategories = new ArrayList<>();
    private ArrayList<String> categories_Selected = new ArrayList<>();
    private ChipGroup preferenceCG;
    private boolean receiveNotification = false;
    private boolean showNearbyEvents = false;
    private Switch notificationSwitch, locationSwitch;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editPreferencesViewModel = ViewModelProviders.of(this).get(EditPreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getEventCategories();
        preferenceCG = view.findViewById(R.id.preference_chipgroup);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        locationSwitch = view.findViewById(R.id.location_switch);
    }

    //checks firestore for realtime updates
    public void realtimeFireStoreData() {
        usersDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    getEventCategories();
                    getPreferenceFirestore();
                } else {
                    getEventCategories();
                    getPreferenceFirestore();
                }
            }
        });
    }

    public void getEventCategories() {
        eventCategories.clear();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    eventCategories.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null) {
                            String category = document.getString("category");
                            if (!eventCategories.contains(category)) {
                                eventCategories.add(category);
                            }
                        } else {
                            Log.d("", "No such document");
                        }
                        Log.d("", document.getId() + " => " + document.getData());
                    }
                    getPreferenceFirestore();
                } else {
                    Log.w("", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void setupView() {

        // category chips
        preferenceCG.setChipSpacing(20);
        int num = 0;
        for (String s : eventCategories) {
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
                updatePreferenceFirestore();
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
                updatePreferenceFirestore();
            }
        });
    }

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
                        ;
                        showNearbyEvents = document.getBoolean("showNearbyEvents");

                        Log.d("getPreferences", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("getPreferences", "No such document");
                    }
                    setupView();
                } else {
                    Log.d("getPreferences", "get failed with ", task.getException());
                }
            }
        });
    }
}