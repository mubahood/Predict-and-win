package com.soccerpredict.spinthewheeltowin.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.USERS_TABLE;

@Entity(tableName = USERS_TABLE)
public class UserModel {

    @NonNull
    @PrimaryKey
    public int user_id = 0;

    public String first_name = "";
    public long reg_date = 0;
    public long last_seen = 0;
    public String phone_number = "";
    public String gender = "";
    public String profile_photo = "";
    public String last_name = "";
    public String username = "";
    public String email = "";
    public String password = "";
    public String city = "";
    public String country = "";
    public String address = "";
    public String user_type = "";
    public String password_plain = "";
}
