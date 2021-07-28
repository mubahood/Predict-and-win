package com.soccerpredict.spinthewheeltowin.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.TICKETS_TABLE;

@Entity(tableName = TICKETS_TABLE)
public class TicketModel {

    @NonNull
    @PrimaryKey
    public String ticket_id = "";

    public String user_first_name = "";
    public String user_last_name = "";
    public String user_id = "";
    public String items_list_json = null;
    public long prediction_date = 0;
    public float total_points = 0;
    public boolean is_active = false;
    public boolean is_success = false;
    public int number_of_items = 0;
    private static final String TAG = "TicketModel";

    @Ignore
    public List<PredictionModel> items_list_data = null;


    public void init() {
        if (items_list_data == null && items_list_json != null) {
            if (items_list_json.length() > 10) {
                try {
                    items_list_data = Arrays.asList(new Gson().fromJson(this.items_list_json, PredictionModel[].class));
                } catch (Exception e) {
                    items_list_data = null;
                    Log.d(TAG, "init: Failed to desrialize ticked intems because " + e.getMessage() + " " + e.getCause());
                }
            }
        }

        if (items_list_data != null) {
            if (!items_list_data.isEmpty()) {
                try {
                    items_list_json = new Gson().toJson(items_list_data);
                } catch (Exception e) {
                    items_list_data = null;
                    Log.d(TAG, "init: Failed to srialize ticket items because " + e.getMessage() + " " + e.getCause());
                }
            }
        }

    }

}
