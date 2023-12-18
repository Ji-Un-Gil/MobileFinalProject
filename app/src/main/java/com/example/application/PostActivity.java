package com.example.application;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private String apiUrl = "https://wldns2577.pythonanywhere.com/api/posts/";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView postImageView;
    private static final int REQUEST_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        requestQueue = Volley.newRequestQueue(this);

        EditText postTitle = findViewById(R.id.postTitle);
        EditText postText = findViewById(R.id.postText);
        EditText postCreatedDate = findViewById(R.id.postCreatedDate);
        TextView postLocation = findViewById(R.id.postLocation);
        Button postButton = findViewById(R.id.postButton);
        postImageView = findViewById(R.id.postImageView);
        Button postButtonChooseImage = findViewById(R.id.postButtonChooseImage);
        Button locationButton = findViewById(R.id.locationButton);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermission(postLocation);
            }
        });

        postButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("title", postTitle.getText().toString());
                    postData.put("text", postText.getText().toString());
                    postData.put("location", postLocation.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // POST API
                MultipartRequest multipartRequest = new MultipartRequest(apiUrl, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("title", postTitle.getText().toString());
                        params.put("text", postText.getText().toString());
                        params.put("location", postLocation.getText().toString());
                        return params;
                    }

                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        params.put("image", new DataPart("image.jpg", getFileDataFromDrawable(imageUri)));
                        return params;
                    }
                };

                requestQueue.add(multipartRequest);
            }
        });
    }

    private byte[] getFileDataFromDrawable(Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지 선택"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            postImageView.setImageURI(imageUri);
        }
    }

    private void checkAndRequestPermission(TextView textView) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용되지 않았을 경우 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            // 권한이 이미 허용되었을 경우 위치 가져오기 실행
            getLocation(textView, this);
        }
    }

    @Nullable
    private List<Address> getAddress(double lat, double lng, Context context) {
        List<Address> address = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.KOREA);
            address = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            Toast.makeText(context, "주소를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    @SuppressLint("MissingPermission")
    private void getLocation(TextView textView, Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(success -> {
                    if (success != null) {
                        List<Address> addressList = getAddress(success.getLatitude(), success.getLongitude(), context);
                        if (addressList != null && !addressList.isEmpty()) {
                            Address address = addressList.get(0);
                            textView.setText(address.getAdminArea() + " " + address.getLocality() + " " + address.getThoroughfare());
                        }
                    }
                })
                .addOnFailureListener(fail -> {
                    if (fail != null) {
                        textView.setText(fail.getLocalizedMessage());
                    }
                });
    }
}
