package com.example.examchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etLogin, etPassword, etName;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        if(prefManager.isLoggedIn()){
            startActivity(new Intent(this, ChatActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        tvError = findViewById(R.id.tvError);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(v -> signIn());
        btnSignUp.setOnClickListener(v -> signUp());
    }

    private void signIn(){
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(login.isEmpty() || password.isEmpty()){
            tvError.setText("Заполните все поля");
            return;
        }

        AuthRequest authRequest = new AuthRequest("", login, password);

        RetrofitClient.getApiService().signIn(authRequest).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                // ВАЖНО: response.isSuccessful() вернет false для кодов 400, 500 и т.д.
                if (response.isSuccessful() && response.body() != null) {
                    // Успешная регистрация (200 OK)
                    SharedPrefManager.getInstance(LoginActivity.this).saveUserData(login, "");
                    startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                    finish();
                } else {
                    // Ошибка (400, 500 и т.д.)
                    String errorMessage = "Неизвестная ошибка";
                    if (response.errorBody() != null) {
                        try {
                            // Пытаемся распарсить тело ошибки как JSON
                            SimpleResponse errorResponse = new Gson().fromJson(response.errorBody().charStream(), SimpleResponse.class);
                            errorMessage = errorResponse.getMessage();
                        } catch (Exception e) {
                            // Если не JSON, читаем как текст
                            try {
                                errorMessage = response.errorBody().string();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    } else {
                        errorMessage = "Ошибка " + response.code();
                    }
                    tvError.setText(errorMessage); // Показываем ошибку пользователю
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                tvError.setText("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void signUp(){
        String name = etName.getText().toString().trim();
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || login.isEmpty() || password.isEmpty()) {
            tvError.setText("Заполните все поля");
            return;
        }

        AuthRequest authRequest = new AuthRequest(name, login, password);

        RetrofitClient.getApiService().signUp(authRequest).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                // ВАЖНО: response.isSuccessful() вернет false для кодов 400, 500 и т.д.
                if (response.isSuccessful() && response.body() != null) {
                    // Успешная регистрация (200 OK)
                    SharedPrefManager.getInstance(LoginActivity.this).saveUserData(login, name);
                    startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                    finish();
                } else {
                    // Ошибка (400, 500 и т.д.)
                    String errorMessage = "Неизвестная ошибка";
                    if (response.errorBody() != null) {
                        try {
                            // Пытаемся распарсить тело ошибки как JSON
                            SimpleResponse errorResponse = new Gson().fromJson(response.errorBody().charStream(), SimpleResponse.class);
                            errorMessage = errorResponse.getMessage();
                        } catch (Exception e) {
                            // Если не JSON, читаем как текст
                            try {
                                errorMessage = response.errorBody().string();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    } else {
                        errorMessage = "Ошибка " + response.code();
                    }
                    tvError.setText(errorMessage); // Показываем ошибку пользователю
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                tvError.setText("Ошибка сети: " + t.getMessage());
            }
        });
    }
}