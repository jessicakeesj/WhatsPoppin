package com.example.whatspoppin.ui.editpreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.whatspoppin.Event;
import com.example.whatspoppin.EventAdapter;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EditPreferencesFragment extends Fragment {

    private EditPreferencesViewModel editPreferencesViewModel;
    private ArrayList<String> eventCategories = new ArrayList<String>();
    private ArrayList<String> categories_Selected = new ArrayList<String>();
    private ChipGroup preferenceCG;
    private Chip chip;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editPreferencesViewModel =
                ViewModelProviders.of(this).get(EditPreferencesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_editpreferences, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

//        final TextView textView = root.findViewById(R.id.text_editpreferences);
//        editPreferencesViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        // Populate Preferences Category
/*
        String[] categories = {"Attractions", "Conference", "Convention", "Expo", "Festival", "Gala", "Game",
                "Network", "Party", "Performance", "Race", "Rally", "Retreat", "Screening", "Seminar", "Tour", "Tournament"};
*/

/*        for (int i = 0; i < categories.length; i++) {
            Chip newChip = new Chip(getContext());
            newChip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            newChip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null,
                    0, R.style.Widget_MaterialComponents_Chip_Filter));
            newChip.setText(categories[i]);
            preferenceCG.addView(newChip);
        }*/

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        getFireStoreData();
        //realtimeFireStoreData();
        preferenceCG = view.findViewById(R.id.preference_chipgroup);
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
                    getFireStoreData();
                } else {
                    getFireStoreData();
                }
            }
        });
    }

    public void getFireStoreData() {
        eventCategories.clear();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    eventCategories.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null) {
                            String category = document.getString("category");
                            if(!eventCategories.contains(category)){
                                eventCategories.add(category);
                            }
                        } else {
                            Log.d("", "No such document");
                        }
                        Log.d("", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w("", "Error getting documents.", task.getException());
                }
                setupview();
            }
        });
    }

    public void setupview(){
        preferenceCG.setChipSpacing(20);

        int num = 0;
        for(String s : eventCategories){
            final Chip newChip = new Chip(getContext());
            newChip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            newChip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null,
                    0, R.style.Widget_MaterialComponents_Chip_Filter));
            newChip.setText(s);
            newChip.setId(num);

            newChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!categories_Selected.contains(newChip.getText().toString())){
                        categories_Selected.add(newChip.getText().toString());
                    }else{
                        categories_Selected.remove(newChip.getText().toString());
                    }
                    updatePrefereceFirestore();
                    Toast.makeText(getActivity(), "Chip is "+ newChip.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            preferenceCG.addView(newChip);
            num++;
        }
    }

    public void updatePrefereceFirestore(){
        usersDoc
                .update("preferences", categories_Selected)
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
}