package com.soccerpredict.spinthewheeltowin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.soccerpredict.spinthewheeltowin.activity.SplashActivity;
import com.soccerpredict.spinthewheeltowin.activity.StartActivity;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;

public class MainActivity extends AppCompatActivity {

    DatabaseRepository databaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent i = new Intent(MainActivity.this, StartActivity.class);
//        MainActivity.this.startActivity(i);
//        finish();

        databaseRepository = new DatabaseRepository(this);
        check_login();
    }


    private void check_login() {
        databaseRepository.get_logged_in_user().observe(this, new Observer<LoggedInUserModel>() {
            @Override
            public void onChanged(LoggedInUserModel loggedInUserModel) {
                if (loggedInUserModel == null) {
                    Toast.makeText(MainActivity.this, "Not Logged in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MainActivity.this, SplashActivity.class);
                    MainActivity.this.startActivity(i);
                    finish();
                    return;
                } else {

                    Intent i = new Intent(MainActivity.this, StartActivity.class);
                    MainActivity.this.startActivity(i);
                    finish();
                    return;
                }
            }
        });
    }
}