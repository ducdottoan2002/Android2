package com.example.filecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentUris;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private  String[] PERMISSIONS;
    private Button stopService;
    private Button startService;
    private Button nextBtn;
    private Button backBtn;
    private Button closeBtn;
    private List<Uri> imageUris;
    private ImageView imageView;
    private int currentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService = findViewById(R.id.control);
        startService.setOnClickListener(v -> clickStartService());

        stopService = findViewById(R.id.stopSvc);
        stopService.setOnClickListener(v -> clickStopService());


        nextBtn = findViewById(R.id.next);
        backBtn = findViewById(R.id.back);
        closeBtn = findViewById(R.id.close);

        PERMISSIONS = new String[] {"android.permission.READ_EXTERNAL_STORAGE"};
        Button imgview = findViewById(R.id.viewBtn);
        imageView = findViewById(R.id.image);
        imgview.setOnClickListener(v -> {
            if (hasPermissions(MainActivity.this, PERMISSIONS)) {
                nextBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                closeBtn.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);

                imageUris = getImageUris();
                displayImage(0);
            } else
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
        });

        nextBtn.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex >= imageUris.size()) {
                currentIndex = 0; // Quay lại hình ảnh đầu tiên nếu đã đến cuối danh sách
            }
            displayImage(currentIndex);
        });

        backBtn.setOnClickListener(v -> {
            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = imageUris.size() - 1; // Quay lại hình ảnh cuối cùng nếu đã đến đầu danh sách
            }
            displayImage(currentIndex);
        });

        closeBtn.setOnClickListener(v -> {
            nextBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            closeBtn.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        });

    }
    private void clickStartService() {
        Intent intent = new Intent(this, RandomService.class);
        startService(intent);
    }

    private void clickStopService() {
        Intent intent = new Intent(this, RandomService.class);
        stopService(intent);
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS){
        if (context != null && PERMISSIONS != null){
            for (String permission : PERMISSIONS){
                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission to external storage GRANTED", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission to external storage DENIED", Toast.LENGTH_SHORT).show();
        }
    }
    private void displayImage(int index) {
        Uri imageUri = imageUris.get(index);
        Picasso.get().load(imageUri).into(imageView);
    }
    private List<Uri> getImageUris() {
        List<Uri> imageUris = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(columnIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                imageUris.add(imageUri);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return imageUris;
    }


}