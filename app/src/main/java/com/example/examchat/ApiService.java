package com.example.examchat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/all-messages")
    Call<List<Message>> getAllMessages(@Query("login") String login);

    @POST("/all-messages")
    Call<List<Message>> getMessagesAfterTime(@Body MessagesAfterTimeRequest request);

    @POST("/sign-in")
    Call<SimpleResponse> signIn(@Body AuthRequest authRequest);

    @POST("/sign-up")
    Call<SimpleResponse> signUp(@Body AuthRequest authRequest);

    @POST("/send-message")
    Call<SimpleResponse> sendMessage(@Body MessageRequest messageRequest);
}
