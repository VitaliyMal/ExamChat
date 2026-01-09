package com.example.examchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        //testDataDisplay();
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        loadMessages();

        swipeRefresh.setOnRefreshListener(this::loadMessages);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void testDataDisplay() {
        // Создаем тестовые данные
        List<Message> testMessages = new ArrayList<>();

        Message testMsg1 = new Message();
        testMsg1.setOwner("Test");
        testMsg1.setText("Тестовое сообщение 1");
        testMsg1.setTime("2026-01-09 00:00:00");

        Message testMsg2 = new Message();
        testMsg2.setOwner("Test2");
        testMsg2.setText("Тестовое сообщение 2");
        testMsg2.setTime("2026-01-09 00:01:00");

        testMessages.add(testMsg1);
        testMessages.add(testMsg2);

        // Пробуем отобразить
        adapter.updateMessages(testMessages);
        Log.d("ChatActivity", "Тестовые данные добавлены: " + testMessages.size() + " сообщений");

        // Проверяем, виден ли RecyclerView
        if (rvMessages.getVisibility() != View.VISIBLE) {
            rvMessages.setVisibility(View.VISIBLE);
        }
    }

    private void loadMessages() {
        swipeRefresh.setRefreshing(true);

        RetrofitClient.getApiService().getAllMessages(userLogin).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                swipeRefresh.setRefreshing(false);

                Log.d("ChatActivity", "Ответ от сервера получен, код: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Message> messageList = response.body();
                    Log.d("ChatActivity", "Получено сообщений с сервера: " + messageList.size());

                    // ВАЖНО: Проверяем, что список не пустой
                    if (messageList != null && !messageList.isEmpty()) {
                        // Обновляем адаптер
                        adapter.updateMessages(messageList);

                        // Прокручиваем к последнему сообщению
                        rvMessages.scrollToPosition(messageList.size() - 1);

                        // Логируем для отладки
                        Log.d("ChatActivity", "Адаптер обновлен с " + messageList.size() + " сообщениями");
                        for (int i = 0; i < Math.min(3, messageList.size()); i++) {
                            Message msg = messageList.get(i);
                            Log.d("ChatActivity", "Сообщение " + i + ": " + msg.getOwner() + ": " + msg.getText());
                        }
                    } else {
                        Log.d("ChatActivity", "Список сообщений с сервера пуст");
                        Toast.makeText(ChatActivity.this, "Нет сообщений", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ChatActivity", "Ошибка ответа: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("ChatActivity", "Тело ошибки: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(ChatActivity.this, "Ошибка загрузки: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.e("ChatActivity", "Сетевая ошибка: " + t.getMessage());
                Toast.makeText(ChatActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
            // 1. Выход из системы (очистка данных)
            SharedPrefManager.getInstance(this).logout();

            // 2. Возвращаемся на экран логина, закрывая текущий
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Закрываем ChatActivity

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
