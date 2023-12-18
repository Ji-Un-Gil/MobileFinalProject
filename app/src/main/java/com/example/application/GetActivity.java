package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

public class GetActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private String apiUrl = "https://wldns2577.pythonanywhere.com/api/posts/";
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);

        Intent intent = getIntent();
        id = intent.getStringExtra("postId");
        requestQueue = Volley.newRequestQueue(this);

        TextView titleView = findViewById(R.id.getTitleView);
        TextView textView = findViewById(R.id.getTextView);
        ImageView imageView = findViewById(R.id.getImageView);
        TextView createdDateView = findViewById(R.id.getCreatedDateView);
        TextView locationView = findViewById(R.id.getLocationView);
        TextView publishedDateView = findViewById(R.id.getPublishedDateView);
        Button getButton = findViewById(R.id.getButton);

        // GET API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl + id + "/", null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.length() > 0) {
                        String title = response.getString("title");
                        String text = response.getString("text");
                        String createdDate = response.getString("created_date");
                        String location = response.getString("location");
                        String publishedDate = response.getString("published_date");
                        String imageUrl = response.getString("image");

                        titleView.setText(title);
                        textView.setText(text);
                        createdDateView.setText("Created Date : " + createdDate);
                        locationView.setText(location);
                        publishedDateView.setText("Published Date : " + publishedDate);
                        Glide.with(GetActivity.this).load(imageUrl).into(imageView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest jsonObjectDeleteRequest = new JsonObjectRequest(Request.Method.DELETE, apiUrl + id + "/", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = new Intent(GetActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError e) {
                                e.printStackTrace();
                            }
                        });
                requestQueue.add(jsonObjectDeleteRequest);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
