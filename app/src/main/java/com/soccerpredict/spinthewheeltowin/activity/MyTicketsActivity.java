package com.soccerpredict.spinthewheeltowin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.Tools;
import com.soccerpredict.spinthewheeltowin.adapter.AdapterTickets;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {
    DatabaseRepository databaseRepository;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        context = this;
        databaseRepository = new DatabaseRepository(context);
        initToolbar();
        bind_views();
        check_login();


    }

    private ActionBar actionBar;
    private Toolbar toolbar;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("My prediction slips");
        Tools.setSystemBarColor(this, R.color.red_5);
        Tools.setSystemBarLight(this);
    }

    void bind_views() {
        no_item_container = findViewById(R.id.no_item_container);
        no_item_notice = findViewById(R.id.no_item_notice);
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
                if (loggedInUserModel == null) {
                    Toast.makeText(context, "Login before you proceed.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                } else {
                    loggedInUser = loggedInUserModel;
                }

                get_data();
            }
        });
    }

    List<TicketModel> ticketModels = new ArrayList<>();

    private void get_data() {
        databaseRepository.get_tickets(loggedInUser.user_id, databaseRepository).observe(
                (LifecycleOwner) context, new Observer<List<TicketModel>>() {
                    @Override
                    public void onChanged(List<TicketModel> ticketModel) {
                        if (ticketModel == null) {
                            ticketModels = new ArrayList<>();
                        } else {
                            ticketModels = ticketModel;
                        }
                        init_components();
                    }
                }
        );
    }

    RecyclerView recycler_vew;
    AdapterTickets adapterTickets;
    boolean adapter_initialized;
    LinearLayout no_item_container;
    TextView no_item_notice;

    void init_components() {

        if (adapterTickets == null) {
            recycler_vew = findViewById(R.id.recycler_vew);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recycler_vew.setLayoutManager(layoutManager);
            recycler_vew.setHasFixedSize(true);
            adapterTickets = new AdapterTickets(this, databaseRepository, ticketModels);
            recycler_vew.setAdapter(adapterTickets);
        }

        if (ticketModels.isEmpty()) {
            no_item_notice.setText("You have not made  any prediction.\nGo to home and make " +
                    "your predictions then proceed to here.");
            no_item_container.setVisibility(View.VISIBLE);
            recycler_vew.setVisibility(View.GONE);
        } else {
            no_item_container.setVisibility(View.GONE);
            recycler_vew.setVisibility(View.VISIBLE);
        }

        if (components_initialized) {
            adapterTickets.updateItems(ticketModels);
        }

        components_initialized = true;

    }

    boolean components_initialized = false;


    LoggedInUserModel loggedInUser;

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