package com.example.mayank.duplicateimages;


import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.GridView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Uri[] uris;
    Bitmap bitmap1, bitmap2;

    private String TAG = MainActivity.class.getSimpleName();


    GridViewAdapter gridViewAdapter;
    GridView gridView;

    ArrayList<ImageItem> imageItems;

    Cursor cc;
    ProgressDialog myProgressDialog = null;
    public Uri[] mUrls;
    public String[] strUrls;
    public String[] mNames;

    InputStream i1;
    InputStream i2;
    InputStream i3;
    InputStream i4;
    InputStream i5;
    InputStream i6;
    InputStream i7;
    InputStream i8;
    InputStream i9;
    InputStream i10;

    byte[] image1;
    byte[] image2;
    byte[] image3;
    byte[] image4;
    byte[] image5;
    byte[] image6;
    byte[] image7;
    byte[] image8;
    byte[] image9;
    byte[] image10;

    int len;

    Boolean b1 = false;
    Boolean b2 = false;
    Boolean b3 = false;
    Boolean b4 = false;
    Boolean b5 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        imageItems = new ArrayList<>();
        getAllImages();

    }

    public void getAllImages() {
        cc = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);

        if (cc != null) {

            myProgressDialog = new ProgressDialog(MainActivity.this);
            myProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myProgressDialog.setMessage(getResources().getString(R.string.pls_wait_txt));
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();

            new Thread() {
                public void run() {
                    try {
                        cc.moveToFirst();
                        mUrls = new Uri[cc.getCount()];
                        strUrls = new String[cc.getCount()];
                        mNames = new String[cc.getCount()];
                        for (int i = 0; i < cc.getCount(); i++) {
                            cc.moveToPosition(i);
                            mUrls[i] = Uri.parse("file://" + Uri.parse(cc.getString(1)));
                            strUrls[i] = cc.getString(1);
                            mNames[i] = cc.getString(3);
                            //Log.d(TAG, "mUrls[i] : " + mUrls[i]);
                            //Log.d(TAG, "strUrls[i] : " + strUrls[i]);
                            //Log.d(TAG, "mNames[i] : " + mNames[i]);
                        }

                        Log.d(TAG, "mUrls array size : " + mUrls.length);
                        Log.d(TAG, "strUrls array size : " + strUrls.length);
                        Log.d(TAG, "mNames array size : " + mNames.length);

                        len = mUrls.length;
                        Log.d(TAG, "Value of len : " + len);

                    } catch (Exception e) {
                        Log.d(TAG, "Exception : " + e);
                    }
                }
            }.start();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                compareImagesPart1 c1 = new compareImagesPart1();
                c1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                compareImagesPart2 c2 = new compareImagesPart2();
                c2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                compareImagesPart3 c3 = new compareImagesPart3();
                c3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                compareImagesPart4 c4 = new compareImagesPart4();
                c4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                compareImagesPart5 c5 = new compareImagesPart5();
                c5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }, 500);


    }

    public class compareImagesPart1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "In doInBackground() method for part1");
            for (int k = 0; k < len / 5; k++) {
                try {
                    i1 = getContentResolver().openInputStream(mUrls[k]);
                    image1 = IOUtils.toByteArray(i1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int l = k + 1; l < len & l != k; l++) {
                    Log.d(TAG, "Comparing image number " + k + " with image number " + l + ".");
                    try {
                        i2 = getContentResolver().openInputStream(mUrls[l]);
                        image2 = IOUtils.toByteArray(i2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k != l) {
                        if (Arrays.equals(image1, image2)) {
                            b1 = true;
                            Log.d(TAG, "Images are similar --> " + mUrls[k].toString() + " &&& " + mUrls[l].toString());
                            try {
                                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[k]);
                                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[l]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageItems.add(new ImageItem(bitmap1, "Image#" + k));
                            imageItems.add(new ImageItem(bitmap2, "Image#" + l));
                        } else
                            Log.d(TAG, "Images are not similar.");
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i("PreExecute()", "compareImagesPart1()");
        }

        protected void onPostExecute(Void result) {
            Log.d("PostExecute()", "compareImagesPart1()");
            if (b1) {
                gridViewAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridViewAdapter);
            }
            myProgressDialog.dismiss();
        }
    }

    public class compareImagesPart2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "In doInBackground() method for part2");
            for (int k = (2 * len) / 5; k < (3 * len) / 5; k++) {
                try {
                    i3 = getContentResolver().openInputStream(mUrls[k]);
                    image3 = IOUtils.toByteArray(i3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int l = 0; l < len && l != k; l++) {
                    Log.d(TAG, "Comparing image number " + k + " with image number " + l + ".");
                    try {
                        i4 = getContentResolver().openInputStream(mUrls[l]);
                        image4 = IOUtils.toByteArray(i4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k != l) {
                        if (Arrays.equals(image3, image4)) {
                            b2 = true;
                            Log.d(TAG, "Images are similar --> " + mUrls[k].toString() + " &&& " + mUrls[l].toString());
                            try {
                                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[k]);
                                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[l]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageItems.add(new ImageItem(bitmap1, "Image#" + k));
                            imageItems.add(new ImageItem(bitmap2, "Image#" + l));
                        } else
                            Log.d(TAG, "Images are not similar.");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i("PreExecute()", "compareImagesPart2()");
        }

        protected void onPostExecute(Void result) {
            if (b2) {
                gridViewAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridViewAdapter);
            }
            myProgressDialog.dismiss();
            Log.d("PostExecute()", "compareImagesPart2()");
        }

    }

    public class compareImagesPart3 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "In doInBackground() method for part2");
            for (int k = (3 * len) / 5; k < (4 * len) / 5; k++) {
                try {
                    i5 = getContentResolver().openInputStream(mUrls[k]);
                    image5 = IOUtils.toByteArray(i5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int l = 0; l < len && l != k; l++) {
                    Log.d(TAG, "Comparing image number " + k + " with image number " + l + ".");
                    try {
                        i6 = getContentResolver().openInputStream(mUrls[l]);
                        image6 = IOUtils.toByteArray(i6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k != l) {
                        if (Arrays.equals(image5, image6)) {
                            b3 = true;
                            Log.d(TAG, "Images are similar --> " + mUrls[k].toString() + " &&& " + mUrls[l].toString());
                            try {
                                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[k]);
                                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[l]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageItems.add(new ImageItem(bitmap1, "Image#" + k));
                            imageItems.add(new ImageItem(bitmap2, "Image#" + l));
                        } else
                            Log.d(TAG, "Images are not similar.");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i("PreExecute()", "compareImagesPart3()");
        }

        protected void onPostExecute(Void result) {
            if (b3) {
                gridViewAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridViewAdapter);
            }
            myProgressDialog.dismiss();
            Log.d("PostExecute()", "compareImagesPart3()");
        }

    }

    public class compareImagesPart4 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "In doInBackground() method for part2");
            for (int k = (3 * len) / 5; k < (4 * len) / 5; k++) {
                try {
                    i7 = getContentResolver().openInputStream(mUrls[k]);
                    image7 = IOUtils.toByteArray(i7);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int l = 0; l < len && l != k; l++) {
                    Log.d(TAG, "Comparing image number " + k + " with image number " + l + ".");
                    try {
                        i8 = getContentResolver().openInputStream(mUrls[l]);
                        image8 = IOUtils.toByteArray(i8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k != l) {
                        if (Arrays.equals(image7, image8)) {
                            b4 = true;
                            Log.d(TAG, "Images are similar --> " + mUrls[k].toString() + " &&& " + mUrls[l].toString());
                            try {
                                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[k]);
                                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[l]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageItems.add(new ImageItem(bitmap1, "Image#" + k));
                            imageItems.add(new ImageItem(bitmap2, "Image#" + l));
                        } else
                            Log.d(TAG, "Images are not similar.");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i("PreExecute()", "compareImagesPart4()");
        }

        protected void onPostExecute(Void result) {
            if (b4) {
                gridViewAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridViewAdapter);
            }
            myProgressDialog.dismiss();
            Log.d("PostExecute()", "compareImagesPart4()");
        }

    }

    public class compareImagesPart5 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "In doInBackground() method for part2");
            for (int k = (4 * len) / 5; k < len; k++) {
                try {
                    i9 = getContentResolver().openInputStream(mUrls[k]);
                    image9 = IOUtils.toByteArray(i9);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int l = 0; l < len; l++) {
                    Log.d(TAG, "Comparing image number " + k + " with image number " + l + ".");
                    try {
                        i10 = getContentResolver().openInputStream(mUrls[l]);
                        image10 = IOUtils.toByteArray(i10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k != l) {
                        if (Arrays.equals(image9, image10)) {
                            b5 = true;
                            Log.d(TAG, "Images are similar --> " + mUrls[k].toString() + " &&& " + mUrls[l].toString());
                            try {
                                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[k]);
                                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), mUrls[l]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageItems.add(new ImageItem(bitmap1, "Image#" + k));
                            imageItems.add(new ImageItem(bitmap2, "Image#" + l));
                        } else
                            Log.d(TAG, "Images are not similar.");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i("PreExecute()", "compareImagesPart5()");
        }

        protected void onPostExecute(Void result) {
            if (b5) {
                gridViewAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, imageItems);
                gridView.setAdapter(gridViewAdapter);
            }
            myProgressDialog.dismiss();
            Log.d("PostExecute()", "compareImagesPart5()");
        }

    }
}

