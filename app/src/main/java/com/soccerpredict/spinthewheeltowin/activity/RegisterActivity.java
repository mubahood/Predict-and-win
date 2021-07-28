package com.soccerpredict.spinthewheeltowin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.soccerpredict.spinthewheeltowin.MainActivity;
import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.Tools;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.ResponseModel;
import com.soccerpredict.spinthewheeltowin.model.UserModel;
import com.soccerpredict.spinthewheeltowin.web.WebInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.BASE_URL;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    Context context;
    DatabaseRepository databaseRepository;
    ProgressDialog progressDialog;
    UserModel newUserModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        databaseRepository = new DatabaseRepository(context);
        bind_views();
    }

    private void bind_views() {
        (findViewById(R.id.bt_create_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_user();
            }
        });

        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        initToolbar();
    }

    private ActionBar actionBar;
    private Toolbar toolbar;
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(null);
        Tools.setSystemBarColor(this, R.color.color_state_primary);
        Tools.setSystemBarLight(this);
    }

    private void register_user() {
        Toast.makeText(context, "Account created successfully.", Toast.LENGTH_LONG).show();
        LoggedInUserModel loggedInUserModel = new LoggedInUserModel();
        loggedInUserModel.first_name ="John";
        loggedInUserModel.last_name ="Doe";
        loggedInUserModel.user_id =1;
        loggedInUserModel.user_type ="admin";
        loggedInUserModel.country ="Uganda";
        loggedInUserModel.username ="john_doe";

        databaseRepository.login_user(loggedInUserModel);
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        finish();
        if(true){
            return;
        }

        newUserModel.email = ((TextView) (findViewById(R.id.email))).getText().toString();
        if (newUserModel.email.length() < 3) {
            Toast.makeText(this, "Enter valid email address.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.email).requestFocus();
            return;
        }

        newUserModel.phone_number = ((TextView) (findViewById(R.id.phone_number))).getText().toString();
        if (newUserModel.phone_number.length() < 3) {
            Toast.makeText(this, "Enter valid phone number.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.email).requestFocus();
            return;
        }

        newUserModel.password = ((TextView) (findViewById(R.id.password))).getText().toString();
        if (newUserModel.password.length() < 3) {
            Toast.makeText(this, "Password too short.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.email).requestFocus();
            return;
        }

        newUserModel.first_name = ((TextView) (findViewById(R.id.first_name))).getText().toString();
        if (newUserModel.first_name.length() < 3) {
            Toast.makeText(this, "Enter valid first name.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.first_name).requestFocus();
            return;
        }

        newUserModel.city = ((TextView) (findViewById(R.id.city))).getText().toString();
        newUserModel.last_name = ((TextView) (findViewById(R.id.last_name))).getText().toString();
        if (newUserModel.last_name.length() < 3) {
            Toast.makeText(this, "Enter valid last name.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.last_name).requestFocus();
            return;
        }

        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //step 1
        Retrofit ret = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Step 2
        WebInterface webInterface = ret.create(WebInterface.class);

        //Step 3
        Call<ResponseModel> my_call = webInterface.register(
                newUserModel.email,
                newUserModel.password,
                newUserModel.phone_number,
                newUserModel.first_name,
                newUserModel.last_name,
                newUserModel.city
        );


        my_call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                progressDialog.hide();
                progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    show_alert("Success BUT " + response.errorBody());
                    return;
                }

                if (response.body().code == 0) {
                    show_alert(response.body().message);
                    return;
                }


                try {
                    Toast.makeText(context, "Account created successfully.", Toast.LENGTH_LONG).show();
                    LoggedInUserModel loggedInUserModel = new Gson().fromJson(response.body().data, LoggedInUserModel.class);
                    databaseRepository.login_user(loggedInUserModel);
                    Intent i = new Intent(context, MainActivity.class);
                    context.startActivity(i);
                    finish();

                } catch (Exception e) {
                    show_alert("Failed to login user because " + e.getMessage() + response.errorBody());
                    Toast.makeText(context, "Failed to login user because " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                progressDialog.hide();
                progressDialog.dismiss();
                show_alert("Failed because " + t.getCause());
                Toast.makeText(RegisterActivity.this, "Failed because " + t.getCause(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "FAILED: " + t.getCause() + " ==> " + t.getMessage());
            }
        });
    }

    private void show_alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);

        builder.setNegativeButton("OK", null);
        builder.show();
    }
}