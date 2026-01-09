package com.example.examchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity{
    private RecyclerView rvMessages;
    private EditText etMessage;
    private SwipeRefreshLayout swipeRefresh;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private String userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        userLogin = prefManager.getLogin();

        if(userLogin == null){
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        Button btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        loadMessages();

        swipeRefresh.setOnRefreshListener(this::loadMessages);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages(){
        RetrofitClient.getApiService().getAllMessages(userLogin).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                swipeRefresh.setRefreshing(false);
                if(response.isSuccessful() && response.body() != null){
                    adapter.updateMessages(response.body().getMessages());
                    rvMessages.scrollToPosition(messages.size()-1);
                } else {
                    Toast.makeText(ChatActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ChatActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(){
        String text = etMessage.getText().toString().trim();
        if(text.isEmpty()){
            return;
        }

        MessageRequest request = new MessageRequest(userLogin, text);

        RetrofitClient.getApiService().sendMessage(request).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    etMessage.setText("");
                    loadMessages();
                } else if (response.code() == 400) {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Ошибка авторизации";
                        Toast.makeText(ChatActivity.this, errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(ChatActivity.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Ошибка " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            SharedPrefManager.getInstance(this).clearUserData();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
