package com.soccerpredict.spinthewheeltowin.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.PREDICTION_TABLE;

@Entity(tableName = PREDICTION_TABLE)
public class PredictionModel {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int prediction_id = 0;

    public int match_id = 0;
    public String ticket_id = "";
    public int predicted_by = 0;
    public String team_1_name = "";
    public String team_2_name = "";
    public boolean team_1_win = false;
    public boolean team_2_win = false;
    public boolean is_draw = false;
    public boolean is_under = false;
    public boolean is_over = false;
    public String prediction_type = "";
    public long prediction_date = 0;
    public float prediction_points_to_win = 0;

}
