package com.example.loginregistration.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loginregistration.MainActivity;
import com.example.loginregistration.R;
import com.example.loginregistration.registration.RegistrationActivity;
import com.example.loginregistration.utils.ApiClient;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_CODE = 100;
    private EditText email, password;

    private static final String SHARED_PREF_NAME = "Findwitess.Local";
    private ProgressBar progressBar;
    private CallbackManager callbackManager;
    public GoogleApiClient googleApiClient;
    private LoginButton loginButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * onCreate view initialize
     * @param savedInstanceState saveInstance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        email = findViewById(R.id.etLoginEmail);
        password = findViewById(R.id.etLoginPassword);
        Button login = findViewById(R.id.btnLogin);
        Button registration = findViewById(R.id.btnLoginRegister);
        loginButton = findViewById(R.id.login_button);
        SignInButton signInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.circularProgressBar);

        login.setOnClickListener(this);
        registration.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean("logged", false) ||
                sharedPreferences.getBoolean("facebook",false) ||
                sharedPreferences.getBoolean("google",false)) {
            gotoMainActivity();
        }
    }

    /**
     * onClick
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                progressBar.setVisibility(View.VISIBLE);
                validateData();
                break;
            case R.id.btnLoginRegister:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.login_button:
                progressBar.setVisibility(View.VISIBLE);
                loginWithFacebook();
                break;
            case R.id.signInButton:
                progressBar.setVisibility(View.VISIBLE);
                signInWithGoogle();
                break;
        }
    }

    /**
     * signInWithGoogle
     */
    private void signInWithGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * loginWithFacebook
     */
    private void loginWithFacebook() {

        loginButton.setReadPermissions(Collections.singletonList("email"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        displayUserInfo(object);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name, last_name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("Login Activity", error.getCause().toString());
            }
        });
    }


    /**
     * displayUserInfo
     * @param object json Object
     */
    private void displayUserInfo(JSONObject object) {
        editor = sharedPreferences.edit();
        try {
            editor.putString("email", object.getString("email"));
            editor.putBoolean("facebook", true);
            editor.apply();
            gotoMainActivity();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * validateData validation
     */
    private void validateData() {
        String strEmail = email.getText().toString().trim();
        String strPsw = password.getText().toString().trim();

        Call<UserModel> request = ApiClient.getRetrofit().getUser(strEmail, strPsw, "123456", "android");
        request.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                UserModel model = response.body();
                Log.d("TAG", "onResponse: " + response.body());
                if (model.getStatus().equals("1")) {
                    progressBar.setVisibility(View.GONE);
                    editor = sharedPreferences.edit();
                    editor.putString("email", model.getData().getEmail());
                    editor.putBoolean("logged", true);
                    editor.apply();

                    gotoMainActivity();

                    email.setText("");
                    password.setText("");
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Invalid Email or Password", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("TAG", "onFailure: "+t.getMessage());

                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    /**|
     * onActivityResult
     * @param requestCode google
     * @param resultCode facebook
     * @param data data from intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    editor = sharedPreferences.edit();
                    editor.putString("email", account.getEmail());
                    editor.putBoolean("google", true);
                    editor.apply();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
                gotoMainActivity();
            }
        } else if (callbackManager != null) {
            progressBar.setVisibility(View.GONE);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * goto mainActivity
     */
    private void gotoMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }


    /**
     * onConnectionFailed
     * @param connectionResult onConnectionFailed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
