package com.example.loginregistration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loginregistration.home.HomeActivity;
import com.example.loginregistration.map.MapsActivity;
import com.example.loginregistration.utils.ApiClient;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MainModel.DataBean>    list = new ArrayList<>();
    private MainAdapter adapter;
    private ProgressBar progressBar;
    private static final String SHARED_PREF_NAME = "Findwitess.Local";
    private SharedPreferences preferences;
    private boolean logged, facebook, google;

    /**
     * onCreate view initialize and set adapter
     * @param savedInstanceState save instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        logged = preferences.getBoolean("logged", false);
        facebook = preferences.getBoolean("facebook", false);
        google = preferences.getBoolean("google", false);
        Button btnNotoficationList = findViewById(R.id.btNotificationList);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnMap = findViewById(R.id.btnMap);

        recyclerView = findViewById(R.id.recyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.circularProgressBar);
        progressBar.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        btnNotoficationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotificationList();
            }
        });



        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.setting:

                break;
            case R.id.logout:
                SharedPreferences.Editor editor = preferences.edit();

                if (logged) {
                    editor.clear();
                    editor.apply();
                    finish();
                } else if (facebook) {
                    LoginManager.getInstance().logOut();
                    editor.clear();
                    editor.apply();
                    finish();
                } else if (google) {
                    editor.clear();
                    editor.apply();
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * getNotificationList get List of notification
     */
    private void getNotificationList() {
        Call<MainModel> call = ApiClient.getRetrofit().getNotificationList(51);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MainModel>() {
            @Override
            public void onResponse(Call<MainModel> call, Response<MainModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    MainModel model = response.body();
                    list = new ArrayList<>(model.getData());
                    adapter = new MainAdapter(list, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<MainModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "InValid request", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
