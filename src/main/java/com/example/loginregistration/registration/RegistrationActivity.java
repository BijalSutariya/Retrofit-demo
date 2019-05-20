package com.example.loginregistration.registration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loginregistration.MainActivity;
import com.example.loginregistration.R;
import com.example.loginregistration.login.UserModel;
import com.example.loginregistration.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private EditText name, email, psw, confirmPsw, contact;
    private Snackbar snackbar;
    private View view;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        view = findViewById(R.id.layoutConstraint);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.registration);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.etRegistrationName);
        email = findViewById(R.id.etRegistrationEmail);
        psw = findViewById(R.id.etRegistrationPsw);
        confirmPsw = findViewById(R.id.etRegistrationConfirmPsw);
        contact = findViewById(R.id.etRegistrationContact);
        Button register = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.circularProgressBar);
        progressBar.setVisibility(View.GONE);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = name.getText().toString().trim();
                String strEmail = email.getText().toString().trim();
                String strPsw = psw.getText().toString().trim();
                String strConfirmPsw = confirmPsw.getText().toString().trim();
                String strContact = contact.getText().toString().trim();

                if (TextUtils.isEmpty(strName)) {
                    snackbar = Snackbar.make(view, "Please Enter Name", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!isValidEmail(strEmail)) {
                    snackbar = Snackbar.make(view, "Please Enter Valid Email", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!isValidPassword(strPsw)) {
                    snackbar = Snackbar.make(view, "Please Enter Valid Password", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!strPsw.equals(strConfirmPsw)) {
                    snackbar = Snackbar.make(view, "Password Not matching", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!isValidMobile(strContact)) {
                    snackbar = Snackbar.make(view, "Please Enter Valid Contact", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    final RegisterModel model = new RegisterModel.Builder()
                            .setName(strName)
                            .setEmail(strEmail)
                            .setPassword(strPsw)
                            .setPh_no(strContact)
                            .setDevice_token("Adroid")
                            .setDevice_type("123456")
                            .build();
                    final Call<UserModel> request = ApiClient.getRetrofit().getUserList(model);
                    Log.d("TAG", "onResponse: " + request.toString());
                    progressBar.setVisibility(View.VISIBLE);
                    request.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()){
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }

            }
        });

    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    public static boolean isValidMobile(String phone) {
        return phone.length() == 10;
    }
}
