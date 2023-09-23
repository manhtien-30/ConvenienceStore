package com.example.checkattendance;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.mlkit.vision.face.Face;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddImage extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 100;
    private static final int PERMISSION_REQUEST_CODE_ACCESS_MEDIA_LOCATION = 110;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private androidx.camera.core.Camera camera;
    private ImageFacade imageFacade;
    private Button btn1;
    private ImageResult imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.viewFinder);
        imageFacade = new ImageFacade();
        imageResult = ImageResult.getInstance();
        btn1 = findViewById(R.id.captureButton);
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

                imageCapture.takePicture(Executors.newSingleThreadExecutor(), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        Toast.makeText(getApplicationContext(), "Add Image Failed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        super.onCaptureSuccess(imageProxy);
                        imageFacade.setImageProxy(imageProxy);// mỗi lần chụp lớp imageReceiver nhận đối tượng imageProxy mới
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
                                            Thread thread1 = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    imageFacade.uploading_image_storage();
                                                }
                                            });
                                            Thread thread2 = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        thread1.join();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), imageResult.getNotification1(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                            Thread thread3 = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        thread2.join();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Waiting upload for minutes....", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            });
                                            Thread thread4 = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        thread3.join();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), " Adding Image Successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                            thread1.start();
                                            thread2.start();
                                            thread3.start();
                                            thread4.start();
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
                        imageProxy.close(); // đóng ImageProxy
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

    public void requestAccessMediaLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_MEDIA_LOCATION);//provide explannation to user that s' why they must provide permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_MEDIA_LOCATION);
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
        } else if (requestCode == PERMISSION_REQUEST_CODE_ACCESS_MEDIA_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                access_media_location();
            } else {
                Toast.makeText(this, "Access Permission has denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void access_media_location() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e("Access", "Granted");
        } else {
            requestAccessMediaLocation();
        }
    }

    private void opencamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            access_media_location();
            startCamera();
        } else {
            // Permission has not been granted, request it
            requestCameraPermission();
        }
    }

}