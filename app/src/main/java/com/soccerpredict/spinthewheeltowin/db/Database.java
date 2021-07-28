package com.soccerpredict.spinthewheeltowin.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.soccerpredict.spinthewheeltowin.model.ChatMessage;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;
import com.soccerpredict.spinthewheeltowin.model.TeamModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;
import com.soccerpredict.spinthewheeltowin.model.UserModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.DATABASE_NAME;


@androidx.room.Database(entities = {
        LoggedInUserModel.class,
        UserModel.class,
        MatchModel.class,
        TeamModel.class,
        PredictionModel.class,
        TicketModel.class,
        ChatMessage.class
}, version = 12, exportSchema = false)

public abstract class Database extends RoomDatabase {
    private static Database instance;
    private static final int NUMBER_OF_THREADS = 6;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract DbInterface dbInterface();

    public static synchronized Database getGetInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class,
                    DATABASE_NAME
            )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
