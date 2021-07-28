package com.soccerpredict.spinthewheeltowin.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.soccerpredict.spinthewheeltowin.model.ChatMessage;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;
import com.soccerpredict.spinthewheeltowin.model.UserModel;

import java.util.List;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.LOGGED_IN_USER_TABLE;
import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.MATCH_TABLE;
import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.MESSAGES_TABLE;
import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.PREDICTION_TABLE;
import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.TICKETS_TABLE;
import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.USERS_TABLE;

@Dao
public interface DbInterface {

    @Query("SELECT * FROM " + LOGGED_IN_USER_TABLE)
    LiveData<LoggedInUserModel> get_logged_in_user();

    @Insert
    void login_user(LoggedInUserModel loggedInUserModel);


    @Query("DELETE FROM " + LOGGED_IN_USER_TABLE + " WHERE 1")
    void logout_user();


    @Query("SELECT * FROM " + USERS_TABLE + " WHERE user_id = :user_id")
    UserModel get_user(int user_id);

    @Query("SELECT * FROM " + MATCH_TABLE + " WHERE matche_id = :matche_id")
    MatchModel get_match(int matche_id);

    @Query("SELECT * FROM " + TICKETS_TABLE + " WHERE ticket_id = :ticket_id")
    TicketModel get_ticket(String ticket_id);

    @Query("SELECT * FROM " + MESSAGES_TABLE + " WHERE message_id = :message_id")
    UserModel get_message(int message_id);

    @Query("SELECT * FROM " + PREDICTION_TABLE + " WHERE match_id = :value")
    PredictionModel get_prediction_by(int value);

    @Query("SELECT * FROM " + USERS_TABLE + " ORDER BY first_name ASC")
    LiveData<List<UserModel>> get_users();

    @Query("SELECT * FROM " + PREDICTION_TABLE + " ORDER BY prediction_id DESC")
    LiveData<List<PredictionModel>> get_predictions();

    @Query("SELECT * FROM " + MATCH_TABLE + " ORDER BY match_start DESC")
    LiveData<List<MatchModel>> get_matches();


    @Query("SELECT * FROM " + TICKETS_TABLE + " WHERE user_id = :user_id ORDER BY prediction_date DESC")
    LiveData<List<TicketModel>> get_tickets(String user_id);

    @Query("DELETE FROM " + PREDICTION_TABLE + " WHERE match_id = :match_id")
    void delete_prediction(int match_id);


    @Query("DELETE FROM " + MATCH_TABLE )
    void delete_all_matches();

    @Query("DELETE FROM " + PREDICTION_TABLE )
    void clear_predictions();


    @Query("SELECT * FROM " + MESSAGES_TABLE + " WHERE ((sender = :sender AND receiver = :receiver) OR(sender = :receiver AND receiver = :sender) ) ORDER BY message_id ASC")
    LiveData<List<ChatMessage>> get_chat(int sender, int receiver);


    @Query("SELECT * FROM " + USERS_TABLE + " WHERE user_id = :user_id")
    LiveData<UserModel> get_user_by_id(int user_id);


    @Insert
    void save_message(ChatMessage chatMessage);

    @Insert
    void save_prediction(PredictionModel predictionModel);

    @Update
    void update_prediction(PredictionModel predictionModel);


    @Insert
    void save_user(UserModel userModel);

    @Insert
    void save_match(MatchModel matchModel);

    @Insert
    void save_ticket(TicketModel ticketModel);

    @Update
    void update_user(UserModel userModel);

    @Update
    void update_match(MatchModel matchModel);

    @Update
    void update_ticket(TicketModel ticketModel);

    @Update
    void update_message(ChatMessage chatMessage);


}