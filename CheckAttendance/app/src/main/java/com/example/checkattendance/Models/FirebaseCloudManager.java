package com.example.checkattendance.Models;

import android.util.Log;

import com.example.checkattendance.Singleton.StaffNote;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCloudManager {
    private static final String TAG = "DEBUG";
    private final FirebaseFirestore db;
    private final LocalTime localTime;
    private final DateTimeFormatter dateTimeFormatter;

    public FirebaseCloudManager() {
        this.db = FirebaseFirestore.getInstance();
        this.localTime = LocalTime.now();
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    public void adding_data() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put(StaffNote.getInstance().getID(), dateTimeFormatter.format(localTime));
        // Add a new document with a generated ID
        db.collection("Attendance")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
