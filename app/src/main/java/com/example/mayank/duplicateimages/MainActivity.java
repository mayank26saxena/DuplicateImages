package com.example.mayank.duplicateimages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sefford.circularprogressdrawable.CircularProgressDrawable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    File[] listFile;
    String folder_name = "Pictures/Test";
    Uri[] uris;
    Bitmap[] bitmaps;
    Boolean b;
    Bitmap bitmap1, bitmap2;

    private String TAG = MainActivity.class.getSimpleName();

    WindowManager wm;
    View myview;

    CircularProgressDrawable c ;

    Animator animator;

    GridViewAdapter gridViewAdapter;
    GridView gridView;

    ArrayList<ImageItem> imageItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        //gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        //gridView.setAdapter(gridViewAdapter);

        imageItems = new ArrayList<>();

        getFromSdcard(folder_name);

        uris = new Uri[listFile.length];
        insertInUriArray(listFile);

        startAnimation();

        for (int i = 0; i < uris.length; i++) {
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uris[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int j = i + 1; j < uris.length; j++) {
                try {
                    bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uris[j]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                b = compareImages(bitmap1, bitmap2);
                if (b) {
                    Log.d(TAG, "Images are similar --> " + uris[i].toString() + " &&& " + uris[j].toString());
                    imageItems.add(new ImageItem(bitmap1, "Image#" + i));
                    imageItems.add(new ImageItem(bitmap2, "Image#" + j));
                }
            }
        }

        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);
        gridView.setAdapter(    gridViewAdapter);

        stopAnimation();


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

    public void insertInBitmapArray(Uri[] uris) throws IOException {
        Log.d(TAG, "Inserting bitmaps into bitmaps array.");
        for (int i = 0; i < uris.length; i++) {
            bitmaps[i] = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uris[i]);
            Log.d(TAG, bitmaps.toString());
        }
    }

    public void insertInUriArray(File[] listFile) {
        Log.d(TAG, "Inserting Uris in uris array.");
        for (int i = 0; i < listFile.length; i++) {
            //uris[i]= Uri.parse(listFile[i].getAbsolutePath().toString());
            uris[i] = Uri.fromFile(listFile[i]);
            Log.d(TAG, uris[i].toString());
        }
    }

    public void startAnimation() {

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myview = li.inflate(R.layout.loading_window, null);

        myview.setVisibility(View.VISIBLE);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                //WindowManager.LayoutParams.TYPE_INPUT_METHOD |
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,// | WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.RIGHT | Gravity.TOP;

        TextView t = (TextView) myview.findViewById(R.id.textView);

        t.setVisibility(View.VISIBLE);

        ImageView i = (ImageView) myview.findViewById(R.id.animationImage);

        c = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.drawable_ring_size))
                .setOutlineColor(getResources().getColor(android.R.color.darker_gray))
                .setRingColor(getResources().getColor(android.R.color.holo_green_light))
                .setCenterColor(getResources().getColor(android.R.color.holo_blue_dark))
                .create();

        i.setImageDrawable(c);

        animator = prepareStyle1Animation();

        animator.start();

        wm.addView(myview, params);

    }

    public void stopAnimation() {
        animator.cancel();
        wm.removeViewImmediate(myview);
    }

    private Animator prepareStyle1Animation() {
        AnimatorSet animation = new AnimatorSet();

        final Animator indeterminateAnimation = ObjectAnimator.ofFloat(c, CircularProgressDrawable.PROGRESS_PROPERTY, 0, 3600);
        indeterminateAnimation.setDuration(3600);

        Animator innerCircleAnimation = ObjectAnimator.ofFloat(c, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0f, 0.75f);
        innerCircleAnimation.setDuration(3600);
        innerCircleAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                c.setIndeterminate(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                indeterminateAnimation.end();
                //c.setIndeterminate(false);
                //c.setProgress(0);
            }
        });

        animation.playTogether(innerCircleAnimation, indeterminateAnimation);
        return animation;
    }

}

