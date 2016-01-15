package com.example.mayank.duplicateimages;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    File[] listFile;
    String folder_name = "Pictures/Test";

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFromSdcard(folder_name);

        ImageView i = (ImageView) findViewById(R.id.imageView);

        Uri uri = Uri.fromFile(listFile[0]);
        Uri uri2 = Uri.fromFile(listFile[listFile.length-1]);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri2);
            Boolean b = compareImages(bitmap, bitmap2);
            Log.d(TAG, "Boolean b is : " + b);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getFromSdcard(String folder) {
        File file = new File(android.os.Environment.getExternalStorageDirectory(), folder);

        Log.d(TAG, file.toString());

        Log.d(TAG, "In getFromSdCard() method. ");

        if (file.isDirectory()) {
            listFile = file.listFiles();

            Log.d(TAG, "File is directory. ");

            for (int i = 0; i < listFile.length; i++) {

                f.add(listFile[i].getAbsolutePath());

                Log.d(TAG, listFile[i].getName());
            }
        }

        Log.d(TAG, "Outside loop.");
    }

    public static boolean compareImages(Bitmap bitmap1, Bitmap bitmap2) {
        if (bitmap1.getWidth() != bitmap2.getWidth() ||
                bitmap1.getHeight() != bitmap2.getHeight()) {
            return false;
        }

        for (int y = 0; y < bitmap1.getHeight(); y++) {
            for (int x = 0; x < bitmap1.getWidth(); x++) {
                if (bitmap1.getPixel(x, y) != bitmap2.getPixel(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }
}

