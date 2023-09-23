package com.example.checkattendance.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

public class ImagePreprocessor{
    private ImageProxy imageProxy;
    public ImagePreprocessor()
    {
        this.imageProxy = null;
    }

    public ImageProxy getImageProxy() {
           return imageProxy;
    }
    public void setImageProxy(ImageProxy imageProxy) {
           this.imageProxy = imageProxy;
    }
    public Bitmap preprocess()
    {
        Image image = imageProxy.getImage();
        // Convert the Image to a Bitmap
        assert image != null;
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        Matrix matrix = new Matrix(); //create matrix to store pixel of image
        matrix.postRotate(270);
        // Rotate the bitmap by 90 degrees
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageProxy.close();
        image.close();
        return rotatedBitmap;
    }

}
