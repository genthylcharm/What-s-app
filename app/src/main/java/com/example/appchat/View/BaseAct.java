package com.example.appchat.View;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appchat.Utilites.Constans;
import com.example.appchat.Utilites.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseAct extends AppCompatActivity {
    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constans.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constans.KEY_USER_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();

        documentReference.update(Constans.KEY_AVAILABILITY, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constans.KEY_AVAILABILITY, 1);

    }
}
