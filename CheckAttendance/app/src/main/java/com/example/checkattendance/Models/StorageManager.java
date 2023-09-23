package com.example.checkattendance.Models;

import com.example.checkattendance.Singleton.ImageResult;
import com.example.checkattendance.Singleton.StaffNote;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class StorageManager {
    private final FirebaseStorage storage;

    public StorageManager() {
        this.storage = FirebaseStorage.getInstance();
    }

    public Task<ListResult> checkfacedata() {
        ImageResult imageResult = ImageResult.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference folderRef = storageRef.child(StaffNote.getInstance().getID().trim());
        return folderRef.listAll();
    }
}
