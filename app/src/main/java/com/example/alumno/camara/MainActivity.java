package com.example.alumno.camara;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_REQ = 0;
    private static final int MY_PERMISSION_CAMERA = 1;

    Uri fileUri = null;
    ImageView photoView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoView = (ImageView)findViewById(R.id.imageView);
        btn = (Button)findViewById(R.id.button);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera is required", Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    public File getOutputFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());

        if(!directory.exists()) {
            if(!directory.mkdirs()) {
                Log.e("ERROR", "While creating directory");
            }
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return new File(directory.getPath() + File.separator + "IMG" + timestamp + ".jpg");
    }

    public void takePhoto(View w) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getOutputFile();
        //fileUri = Uri.fromFile(file);
        fileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(i, CAPTURE_IMAGE_REQ);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_REQ) {
            if(resultCode == RESULT_OK) {
                Uri photoUri = null;
                if(data == null) {
                    Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
                    photoUri = fileUri;
                }
                else {
                    Toast.makeText(this, "Image saved to: " + data.getData(), Toast.LENGTH_SHORT).show();
                    photoUri = data.getData();
                }

            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Error while trying to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera permission allowed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void showPhoto(Uri photoUri) {
        File imgFile = new File(photoUri.getPath());
        if(imgFile.exists()) {
            Drawable old = photoView.getDrawable();
            if (old != null) {
                ((BitmapDrawable)old).getBitmap().recycle();
            }
            Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            BitmapDrawable drawable = new BitmapDrawable(this.getResources(), bm);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setImageDrawable(drawable);
        }
    }

}
