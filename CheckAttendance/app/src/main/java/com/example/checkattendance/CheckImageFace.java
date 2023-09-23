package com.example.checkattendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.checkattendance.Facade.ImageFacade;
import com.example.checkattendance.Singleton.ImageResult;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.ListResult;
import com.google.mlkit.vision.face.Face;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CheckImageFace extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 100;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private androidx.camera.core.Camera camera;
    private Button btn1;
    private ImageFacade imageFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_face);
        previewView = findViewById(R.id.viewFinder);
        btn1 = findViewById(R.id.checkButton);
        imageFacade = new ImageFacade();
        opencamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        takePhoto();
    }

    private void takePhoto() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFacade.check_face_data_user().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        if (task.isComplete()) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getItems().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "You have not provided face image for us!", Toast.LENGTH_SHORT).show();

                                } else {
                                    imageCapture.takePicture(Executors.newSingleThreadExecutor(), new ImageCapture.OnImageCapturedCallback() {
                                        @Override
                                        public void onError(@NonNull ImageCaptureException exception) {
                                            super.onError(exception);
                                            Toast.makeText(getApplicationContext(), "Add Image Failed!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                                            super.onCaptureSuccess(imageProxy);
                                            imageFacade.setImageProxy(imageProxy);
                                            imageFacade.process_full_inputdata();
                                            imageFacade.check_validation_input_data().addOnCompleteListener(Executors.newSingleThreadExecutor(), new OnCompleteListener<List<Face>>() {
                                                @Override
                                                public void onComplete(@NonNull Task<List<Face>> task) {
                                                    if (task.isComplete()) {
                                                        ImageResult imageResult = ImageResult.getInstance();
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().size() == 0) {
                                                                Log.e("Notice", imageResult.getNotification1());
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), imageResult.getNotification1(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                Log.e("Notice", imageResult.getNotification1());
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(getApplicationContext(), imageResult.getNotification1(), Toast.LENGTH_SHORT).show();
                                                                        // lấy bounding box
                                                                        Rect coordinates_face = task.getResult().get(0).getBoundingBox();
                                                                        imageFacade.cropped_image(coordinates_face);
                                                                        Toast.makeText(getApplicationContext(), imageFacade.predict_input_face_data(getApplicationContext()), Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        } else {
                                                            Log.e("Notice", imageResult.getNotification1());
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), imageResult.getNotification1(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        Log.e("Notice", "Task is not yet complete");
                                                    }
                                                }
                                            }).addOnCanceledListener(Executors.newSingleThreadExecutor(), new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    Log.e("Notice", "Task is canceled");
                                                }
                                            });
                                            imageProxy.close();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });

    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            Preview preview = new Preview.Builder().build();
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
            imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setFlashMode(ImageCapture.FLASH_MODE_AUTO).build();
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, getExecuter());
    }

    private Executor getExecuter() {
        return ContextCompat.getMainExecutor(this);
    }

    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);//provide explannation to user that s' why they must provide permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
        }  // Permission has already been granted

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                opencamera();
            } else {
                Toast.makeText(this, "Camera Permission has denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void opencamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            // Permission has not been granted, request it
            requestCameraPermission();
        }
    }

}