package com.example.checkattendance.Facade;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.example.checkattendance.Models.Detector;
import com.example.checkattendance.Models.FaceRecognizer;
import com.example.checkattendance.Models.FirebaseCloudManager;
import com.example.checkattendance.Models.ImagePreprocessor;
import com.example.checkattendance.Models.StorageManager;
import com.example.checkattendance.Singleton.StaffNote;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.face.Face;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class ImageFacade {
    private final ImagePreprocessor imagePreprocessor;
    private ImageProxy imageProxy;
    private Bitmap preprocessed_image_bitmap;
    private final Detector detector;
    private Bitmap only_face_image_bitmap;
    private final StorageManager storageManager;
    private final FaceRecognizer faceRecognizer;

    public ImageFacade() {
        this.imagePreprocessor = new ImagePreprocessor();
        this.detector = new Detector();
        this.imageProxy = null;
        this.preprocessed_image_bitmap = null;
        this.only_face_image_bitmap = null;
        this.storageManager = new StorageManager();
        this.faceRecognizer = new FaceRecognizer();
    }

    public void process_full_inputdata() {
        imagePreprocessor.setImageProxy(imageProxy);
        preprocessed_image_bitmap = imagePreprocessor.preprocess();
        imageProxy.close();
    }

    public String predict_input_face_data(Context context) {
        FirebaseCloudManager firebaseCloudManager = new FirebaseCloudManager();
        int m = faceRecognizer.predict(context, only_face_image_bitmap);
        Log.d("chỉ số mục", String.valueOf(m));
        if (m == StaffNote.getInstance().getIndexing()) {
            firebaseCloudManager.adding_data();
            return "CheckAttedance Successfully";
        } else {
            return "CheckAttendance Failed";
        }
    }

    public void uploading_image_storage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference folderRef = storageRef.child(StaffNote.getInstance().getID().trim());
        StorageReference imageRef = folderRef.child(String.valueOf(System.currentTimeMillis()).concat(".jpg"));
        // Convert bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preprocessed_image_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        imageRef.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("Notice", "Uploading Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Notice", "Uploading Failed");
            }

        });
    }

    public Task<List<Face>> check_validation_input_data() {
        return detector.check_face_in_image(preprocessed_image_bitmap);
    }

    public void setImageProxy(ImageProxy imageProxy) {
        this.imageProxy = imageProxy;
    }

    public Task<ListResult> check_face_data_user() {
        return storageManager.checkfacedata();

    }

    public void cropped_image(Rect rect) {
        //tạo matrix
        Matrix matrix = new Matrix();
        only_face_image_bitmap = Bitmap.createBitmap(preprocessed_image_bitmap, rect.left, rect.top, rect.width(), rect.height(), matrix, true);

    }
}
