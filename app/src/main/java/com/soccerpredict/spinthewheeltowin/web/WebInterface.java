package com.soccerpredict.spinthewheeltowin.web;

import com.soccerpredict.spinthewheeltowin.model.ChatMessage;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.ResponseModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;
import com.soccerpredict.spinthewheeltowin.model.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebInterface {

    @FormUrlEncoded
    @POST("add_prediction.php")
    Call<ResponseModel> add_prediction(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseModel> register(
            @Field("email") String email,
            @Field("password") String name,
            @Field("phone_number") String phone_number,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("city") String city
    );


    @GET("users.php")
    Call<List<UserModel>> get_user(
            @Query("phone_number") String phone_number
    );


    @GET("users.php")
    Call<List<UserModel>> get_users();


    @GET("matches.php")
    Call<List<MatchModel>> get_matches();

    @GET("tickets.php")
    Call<List<TicketModel>> get_tickets(
            @Query("user_id") String user_id
    );


    @GET("get_messages.php")
    Call<List<ChatMessage>> get_chats(
            @Query("user_id") String user_id,
            @Query("min_time") String min_time
    );


    @FormUrlEncoded
    @POST("send_message.php")
    Call<ResponseModel> send_message(
            @Field("sender") String sender,
            @Field("receiver") String receiver,
            @Field("body") String body
    );

}
