package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private String apiUrl = "https://wldns2577.pythonanywhere.com/api/posts/";
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Post> postList = new ArrayList<>();

        Button mainButton = findViewById(R.id.mainButton);

        // GET LIST API
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject postObject = response.getJSONObject(i);
                        postList.add(new Post(postObject.getString("id"), postObject.getString("title"), postObject.getString("published_date")));
                    }
                    postAdapter = new PostAdapter(postList);

                    recyclerView.setAdapter(postAdapter);

                    postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Post selectedPost = postList.get(position);
                            Intent intent = new Intent(MainActivity.this, GetActivity.class);
                            intent.putExtra("postId", selectedPost.id);
                            startActivity(intent);
                        }
                    });
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

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}

