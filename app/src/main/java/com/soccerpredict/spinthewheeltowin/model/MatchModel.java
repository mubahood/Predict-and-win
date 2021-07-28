package com.soccerpredict.spinthewheeltowin.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.MATCH_TABLE;

@Entity(tableName = MATCH_TABLE)
public class MatchModel {

    @NonNull
    @PrimaryKey
    public int matche_id = 0;

    public String match_category = "";
    public long match_create_date = 0;
    public int match_end = 0;
    public int match_start = 0;
    public int team_1 = 0;
    public int team_1_results = 0;
    public int team_2 = 0;
    public int team_2_results = 0;
    public String match_status = "";

    public float points_team_1_win = 0;
    public float points_team_2_win = 0;
    public float points_draw = 0;
    public float points_over = 0;
    public float points_under = 0;
    public float points_specific = 0;

    @Ignore
    public TeamModel team_1_data = null;

    @Ignore
    public TeamModel team_2_data = null;

    public String team_1_data_json = null;
    public String team_2_data_json = null;

    public void init() {
        if (this.team_1_data == null && this.team_1_data_json != null) {
            if (this.team_1_data_json.length() > 10) {
                try {
                    this.team_1_data = new Gson().fromJson(this.team_1_data_json, TeamModel.class);
                } catch (Exception e) {
                    Log.d(TAG, "init: Failed to convert team json because " + e.getMessage());
                }
            }
        }

        if (this.team_2_data == null && this.team_2_data_json != null) {
            if (this.team_2_data_json.length() > 10) {
                try {
                    this.team_2_data = new Gson().fromJson(this.team_2_data_json, TeamModel.class);
                } catch (Exception e) {
                    Log.d(TAG, "init: Failed to convert team json because " + e.getMessage());
                }
            }
        }

        if (this.team_2_data != null && this.team_2_data_json == null) {
            if (this.team_2_data.team_id > 0) {
                try {
                    this.team_2_data_json = new Gson().toJson(this.team_2_data);
                } catch (Exception e) {
                    Log.d(TAG, "init: Failed to convert team to json because " + e.getMessage());
                }
            }
        }

        if (this.team_1_data != null) {
            if (this.team_1_data.team_id > 0) {
                try {
                    this.team_1_data_json = new Gson().toJson(this.team_1_data);
                } catch (Exception e) {
                    Log.d(TAG, "init: Failed to convert team to json because " + e.getMessage());
                }
            }
        }

        if (this.team_2_data == null) {
            this.team_2_data = new TeamModel();
        }
        if (this.team_1_data == null) {
            this.team_1_data = new TeamModel();
        }

    }


    private static final String TAG = "MatchModel";


}
