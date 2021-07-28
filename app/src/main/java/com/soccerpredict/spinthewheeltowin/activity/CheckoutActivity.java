package com.soccerpredict.spinthewheeltowin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.Tools;
import com.soccerpredict.spinthewheeltowin.adapter.AdapterCheckout;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;
import com.soccerpredict.spinthewheeltowin.model.ResponseModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;
import com.soccerpredict.spinthewheeltowin.web.WebInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.BASE_URL;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    Context context;
    DatabaseRepository databaseRepository;
    //step 1
    Retrofit ret = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    //Step 2
    WebInterface webInterface = ret.create(WebInterface.class);
    LoggedInUserModel loggedInUser;
    List<PredictionModel> ticket_list = new ArrayList<>();
    ProgressDialog progressDialog;
    AdapterCheckout adapterCheckout = null;
    boolean adapter_initialized = false;
    List<MatchModel> matches = new ArrayList<>();
    List<PredictionModel> predictionModels = null;
    RecyclerView checkout_recycler_vew;
    TextView no_item_notice;
    TextView total_view;
    LinearLayout no_item_container;
    AppCompatButton submit_button;
    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        context = this;
        databaseRepository = new DatabaseRepository(this);
        check_login();
        bind_views();
        get_predictions();
    }

    void submit_data() {
        if (
                predictionModels == null ||
                        matches == null ||
                        predictionModels.isEmpty() ||
                        matches.isEmpty()
        ) {
            Toast.makeText(context, "You have not made any prediction.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (PredictionModel p : predictionModels) {
            for (MatchModel m : matches) {
                if (p.match_id == m.matche_id) {
                    m.init();
                    p.predicted_by = loggedInUser.user_id;
                    p.team_1_name = m.team_1_data.team_name;
                    p.team_2_name = m.team_2_data.team_name;
                    ticket_list.add(p);
                }
            }
        }
        final String data = new Gson().toJson(ticket_list);
        Log.d(TAG, "submit_data: " + data);
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Step 3
        Call<ResponseModel> call = webInterface.add_prediction(data);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                progressDialog.dismiss();
                if (response == null) {
                    show_alert("Failed to submit, please try again.");
                    return;
                }

                if (response.body().code == 0) {
                    show_alert(response.body().message);
                    return;
                }

                try {
                    final TicketModel ticketModel = new Gson().fromJson(response.body().data, TicketModel.class);
                    databaseRepository.clear_predictions();
                } catch (Exception e) {
                    Log.d(TAG, "on_response: FAILED " + e.getCause() + " ==> " + e.getMessage());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(response.body().message);
                builder.setCancelable(false);
                builder.setTitle("Success");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, StartActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
                builder.show();


            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                show_alert("Failed to submit, please try again.");
            }
        });

    }

    private void show_alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setNegativeButton("OK", null);
        builder.show();
    }

    void get_logged_in_user() {
        databaseRepository.get_logged_in_user().observe((LifecycleOwner) context, new Observer<LoggedInUserModel>() {
            @Override
            public void onChanged(LoggedInUserModel loggedInUserModel) {
                if (loggedInUserModel == null) {
                    Toast.makeText(context, "You are not logged in.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loggedInUser = loggedInUserModel;
            }
        });
    }

    void init_components() {

        if (adapterCheckout == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            checkout_recycler_vew.setLayoutManager(layoutManager);
            checkout_recycler_vew.setHasFixedSize(true);
            adapterCheckout = new AdapterCheckout(this, databaseRepository);
        }

        if (!adapter_initialized) {
            checkout_recycler_vew.setAdapter(adapterCheckout);
        }

        if (!adapter_initialized) {
            adapterCheckout.updateItems(matches, predictionModels, total_view);
        }

        adapter_initialized = true;

        if (predictionModels.isEmpty()) {
            no_item_notice.setText("You have not made  any predicion.\nGo to home and make " +
                    "your predictions then proceed to here.");
            no_item_container.setVisibility(View.VISIBLE);
            checkout_recycler_vew.setVisibility(View.GONE);
        } else {
            no_item_container.setVisibility(View.GONE);
            checkout_recycler_vew.setVisibility(View.VISIBLE);
        }
    }

    void get_predictions() {
        databaseRepository.get_predictions().observe((LifecycleOwner) context, new Observer<List<PredictionModel>>() {
            @Override
            public void onChanged(List<PredictionModel> predictionModel) {
                if (predictionModel == null) {
                    get_matches();
                    return;
                }
                predictionModels = predictionModel;

                get_matches();

            }
        });
    }

    void get_matches() {

        databaseRepository.get_matches(databaseRepository).observe((LifecycleOwner) context, new Observer<List<MatchModel>>() {
            @Override
            public void onChanged(List<MatchModel> _matchModels) {
                if (_matchModels == null) {
                    init_components();
                    return;
                }

                matches = _matchModels;

                Log.d(TAG, "init_components: _matchModels : " + _matchModels.size());
                Log.d(TAG, "init_components: predictionModels : " + predictionModels.size());

                init_components();
            }
        });

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("PREDICTION SLIP");
        Tools.setSystemBarColor(this, R.color.red_5);
        Tools.setSystemBarLight(this);
    }

    private void bind_views() {
        initToolbar();
        checkout_recycler_vew = findViewById(R.id.checkout_recycler_vew);
        submit_button = findViewById(R.id.submit_button);
        total_view = findViewById(R.id.total_view);
        no_item_notice = findViewById(R.id.no_item_notice);
        no_item_container = findViewById(R.id.no_item_container);
        no_item_container.setVisibility(View.GONE);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_data();
            }
        });
    }

    private void check_login() {
        databaseRepository.get_logged_in_user().observe(this, new Observer<LoggedInUserModel>() {
            @Override
            public void onChanged(LoggedInUserModel loggedInUserModel) {
                if (loggedInUserModel == null) {
                    Toast.makeText(context, "Not Logged in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(context, RegisterActivity.class);
                    context.startActivity(i);
                    finish();
                    return;
                } else {
                    get_logged_in_user();
//                    databaseRepository.login_user(loggedInUserModel);
//                    Intent i = new Intent(StartActivity.this, MainActivity.class);
//                    StartActivity.this.startActivity(i);
//                    finish();
                }
            }
        });
    }


}