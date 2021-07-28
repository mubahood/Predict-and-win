package com.soccerpredict.spinthewheeltowin.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.MATCH_TABLE;
import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.TEAM_TABLE;

@Entity(tableName = TEAM_TABLE)
public class TeamModel {

    @NonNull
    @PrimaryKey
    public int team_id = 0;

    public String team_name = "";
    public String team_abbr = "";
    public String team_category = "";
    public String team_description = "";
    public String team_logo	 = "";


}
