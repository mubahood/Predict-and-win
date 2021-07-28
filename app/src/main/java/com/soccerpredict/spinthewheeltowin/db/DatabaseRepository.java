package com.soccerpredict.spinthewheeltowin.db;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.soccerpredict.spinthewheeltowin.model.ChatMessage;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;
import com.soccerpredict.spinthewheeltowin.model.UserModel;
import com.soccerpredict.spinthewheeltowin.web.WebInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.BASE_URL;

public class DatabaseRepository {
    Context context;
    public Database database;
    public DbInterface dbInterface;
    private static final String TAG = "DatabaseRepository";

    //step 1
    Retrofit ret = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    //Step 2
    WebInterface webInterface = ret.create(WebInterface.class);


    public DatabaseRepository(Context context) {
        this.context = context;
        database = Database.getGetInstance(context);
        dbInterface = database.dbInterface();
    }

    public void logout_user() {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.logout_user();
                    Log.d(TAG, "run: Success logged out successfully!...");
                } catch (Exception e) {
                    Log.d(TAG, "run: Failed to logout user because...");
                }
            }
        });
    }


    public void delete_predictions(final int match_id) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    dbInterface.delete_prediction(match_id);
                    Log.d(TAG, "run: Deleted prection " + match_id + " Successfully.");
                } catch (Exception e) {
                    Log.d(TAG, "run: Failed to delete prediction because " + e.getCause() + " ==> " + e.getMessage());
                }
            }
        });
    }

    public void clear_predictions() {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    dbInterface.clear_predictions();

                } catch (Exception e) {
                    Log.d(TAG, "run: Failed to delete prediction because " + e.getCause() + " ==> " + e.getMessage());
                }
            }
        });
    }


    public void save_predictions(final List<PredictionModel> predictionModels) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                for (PredictionModel p : predictionModels) {
                    if (p == null) {
                        Log.d(TAG, "run: ==> is null");
                    } else {
                        Log.d(TAG, "run: ==> is not null");
                    }
                    PredictionModel local_prediction = dbInterface.get_prediction_by(p.match_id);
                    if (local_prediction == null) {
                        try {
                            dbInterface.save_prediction(p);
                            Log.d(TAG, "run:  INSERTED MESSAGE " + p.match_id + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to INSTERT USER " + p.match_id + " BECAUSE " + e.getMessage());
                        }
                    } else {
                        try {
                            dbInterface.update_prediction(p);
                            Log.d(TAG, "run:  UPDATED message " + p.match_id + " successfullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to UPDATE MESSA  " + p.match_id + " BECAUSE " + e.getMessage());
                        }

                    }
                }
            }
        });
    }


    public void save_messages(final List<ChatMessage> messages) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                for (ChatMessage m : messages) {
                    UserModel local_msg = dbInterface.get_message(m.message_id);
                    if (local_msg == null) {
                        try {
                            dbInterface.save_message(m);
                            Log.d(TAG, "run:  INSERTED MESSAGE " + m.body + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to INSTERT USER " + m.body + " BECAUSE " + e.getMessage());
                        }
                    } else {
                        try {
                            dbInterface.update_message(m);
                            Log.d(TAG, "run:  UPDATED message " + m.body + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to UPDATE MESSA  " + m.body + " BECAUSE " + e.getMessage());
                        }

                    }
                }
            }
        });
    }


    public void save_users(final List<UserModel> users) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                for (UserModel u : users) {
                    UserModel local_user = dbInterface.get_user(u.user_id);

                    if (local_user == null) {

                        try {
                            dbInterface.save_user(u);
                            Log.d(TAG, "run:  INSERTED USER " + u.first_name + " " + u.last_name + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to INSTERT USER " + u.first_name + " " + u.last_name + " BECAUSE " + e.getMessage());
                        }
                    } else {
                        try {
                            dbInterface.update_user(u);
                            Log.d(TAG, "run:  UPDATED USER " + u.first_name + " " + u.last_name + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to UPDATE USER " + u.first_name + " " + u.last_name + " BECAUSE " + e.getMessage());
                        }

                    }
                }
            }
        });
    }


    public void save_tickets(final List<TicketModel> items) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                for (TicketModel m : items) {
                    m.init();
                    TicketModel local_item = dbInterface.get_ticket(m.ticket_id);
                    if (local_item == null) {
                        try {
                            dbInterface.save_ticket(m);
                            Log.d(TAG, "run:  INSERTED USER " + m.ticket_id + " successfully");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to INSTERT USER " + m.ticket_id);
                        }
                    } else {
                        local_item.init();
                        try {
                            dbInterface.update_ticket(m);
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to UPDATE USER " + m.ticket_id + " Vs. " + m.ticket_id + " BECAUSE " + e.getMessage());
                        }

                    }
                }
            }
        });
    }


    public void save_matches(final List<MatchModel> items,final  boolean delete_old) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                if(delete_old){
                    try{
                        //dbInterface.delete_all_matches();
                        Log.d(TAG, "run: ALL OLD MATCHES DELETED");
                    }catch (Exception e){
                        Log.d(TAG, "run: FAILED TO DELETE OLD MATCHES ==> "+e.getMessage());
                    }
                }

                for (MatchModel m : items) {
                    m.init();
                    MatchModel local_item = dbInterface.get_match(m.matche_id);
                    if (local_item == null) {
                        try {
                            dbInterface.save_match(m);
                            Log.d(TAG, "run:  INSERTED USER " + m.team_2_data.team_name + " Vs. " + m.team_1_data.team_name + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to INSTERT USER " + m.team_2_data.team_name + " Vs. " + m.team_1_data.team_name + " BECAUSE " + e.getMessage());
                        }
                    } else {
                        local_item.init();
                        try {
                            dbInterface.update_match(m);
                            Log.d(TAG, "run:  UPDATED USER " + m.team_2_data.team_name + " Vs. " + m.team_1_data.team_name + " success fullly");
                        } catch (Exception e) {
                            Log.d(TAG, "run:  Failed to UPDATE USER " + m.team_2_data.team_name + " Vs. " + m.team_1_data.team_name + " BECAUSE " + e.getMessage());
                        }

                    }
                }
            }
        });
    }


    public void login_user(final LoggedInUserModel loggedInUserModel) {
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dbInterface.login_user(loggedInUserModel);
                    Log.d(TAG, "run: Success logged in successfully!...");
                } catch (Exception e) {
                    Log.d(TAG, "run: Failed to login user because...");
                }
            }
        });
    }

    public LiveData<LoggedInUserModel> get_logged_in_user() {
        return dbInterface.get_logged_in_user();
    }

    public LiveData<List<PredictionModel>> get_predictions() {
        return dbInterface.get_predictions();
    }

    public LiveData<List<UserModel>> get_users() {
        return dbInterface.get_users();
    }


    public LiveData<List<MatchModel>> get_matches(DatabaseRepository databaseRepository) {
        get_web_matches(databaseRepository);
        return dbInterface.get_matches();
    }

    public LiveData<List<TicketModel>> get_tickets(int user_id, DatabaseRepository databaseRepository) {
        get_web_tickets(user_id + "", databaseRepository);
        return dbInterface.get_tickets(user_id + "");
    }


    public void get_web_tickets(String user_id, DatabaseRepository databaseRepository) {
        Log.d(TAG, "onResponse ticks: GETTING" );

        //Step 3
        Call<List<TicketModel>> call = webInterface.get_tickets(user_id);
        call.enqueue(new Callback<List<TicketModel>>() {
            @Override
            public void onResponse(Call<List<TicketModel>> call, Response<List<TicketModel>> response) {
                if (response == null) {
                    Log.d(TAG, "onResponse ticks: NULL");
                    return;
                }
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse ticks: NOT SUCCESS");
                    return;
                }
                if (response.body().isEmpty()) {
                    Log.d(TAG, "onResponse ticks: EMPTY");
                }
                try {
                    databaseRepository.save_tickets(response.body());
                    Log.d(TAG, "onResponse ticks: SUCCESS SAVE ==> " + response.body().size());
                } catch (Exception e) {
                    Log.d(TAG, "onResponse ticks: FAILED TO SAVE BECAUSE" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<TicketModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed to get matches because " + t.getCause() + " ===> " + t.getMessage());
            }
        });

    }


    public void get_web_matches(DatabaseRepository databaseRepository) {
        //Step 3
        Call<List<MatchModel>> call = webInterface.get_matches();
        call.enqueue(new Callback<List<MatchModel>>() {
            @Override
            public void onResponse(Call<List<MatchModel>> call, Response<List<MatchModel>> response) {
                if (response == null) {
                    return;
                }
                if (!response.isSuccessful()) {
                    return;
                }
                if (response.body().isEmpty()) {
                    return;
                }
                try {
                    databaseRepository.save_matches(response.body(),true);
                    Log.d(TAG, "onResponse: SUCCESS SAVE ==> " + response.body().size());
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: FAILED TO SAVE BECAUSE" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<MatchModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed to get matches because " + t.getCause() + " ===> " + t.getMessage());
            }
        });

    }

    public LiveData<List<ChatMessage>> get_chat(int sender, int receiver) {
        return dbInterface.get_chat(sender, receiver);
    }

    public LiveData<UserModel> get_user_by_id(int user_id) {
        return dbInterface.get_user_by_id(user_id);
    }


}
