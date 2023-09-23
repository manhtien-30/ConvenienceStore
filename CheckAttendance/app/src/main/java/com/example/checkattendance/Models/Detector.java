package com.example.checkattendance.Models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.checkattendance.Singleton.ImageResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class Detector {
    private final FaceDetectorOptions highAccuracyOpts;
    private final FaceDetector detector;

    public Detector() {
        this.highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();
        this.detector = FaceDetection.getClient(this.highAccuracyOpts);
    }

    public Task<List<Face>> check_face_in_image(Bitmap bitmap_image) {

        InputImage image = InputImage.fromBitmap(bitmap_image, 0);
        Task<List<Face>> result = detector.process(image)
                .addOnSuccessListener( // phương thức đến từ abstract class
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                ImageResult imageResult = ImageResult.getInstance();
                                if (faces.size() == 0) {
                                    imageResult.setNotification1("No face found");
                                } else {
                                    imageResult.setNotification1("Face found");
                                }
                            }
                        })
                .addOnFailureListener( // phương thức đến từ abstract class
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ImageResult imageResult = ImageResult.getInstance();
                                imageResult.setNotification1(e.getMessage());
                            }
                        });
        return result;
    }

}
