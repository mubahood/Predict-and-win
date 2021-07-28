package com.soccerpredict.spinthewheeltowin.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.Tools;
import com.soccerpredict.spinthewheeltowin.adapter.AdapterMatches;
import com.soccerpredict.spinthewheeltowin.adapter.SliderAdapter;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.LoggedInUserModel;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;
import com.soccerpredict.spinthewheeltowin.model.SliderModel;
import com.soccerpredict.spinthewheeltowin.model.wheel.LuckyItem;
import com.soccerpredict.spinthewheeltowin.model.wheel.LuckyWheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "whatsapp_clone_db";
    public static final String LOGGED_IN_USER_TABLE = "LOGGED_IN_USER_TABLE";
    public static final String USERS_TABLE = "USERS_TABLE";
    public static final String PREDICTION_TABLE = "PREDICTION_TABLE";
    public static final String TICKETS_TABLE = "TICKETS_TABLE";
    public static final String MESSAGES_TABLE = "MESSAGES_TABLE";
    public static final String MATCH_TABLE = "MATCH_TABLE";
    public static final String TEAM_TABLE = "TEAM_TABLE";
    public static final String BASE_URL = "https://app.ugnews24.info/api/";
    //public static final String BASE_URL = "http://10.0.2.2:8080/spin/";
    private static final String TAG = "StartActivity";
    Context context;
    DatabaseRepository databaseRepository;
    BottomNavigationView navigation;
    ConstraintLayout home_layout;
    LinearLayout spin_layout, winners_layout, user_layout;
    PredictionModel predictionModel = new PredictionModel();
    List<LuckyItem> data = new ArrayList<>();
    boolean matches_initialized = false;
    TextView matches_status;
    AdapterMatches adapterMatches = null;
    TextView predictions_count_view;
    private BottomSheetBehavior mBehavior;
    //private View bottom_sheet;
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = this;
        databaseRepository = new DatabaseRepository(this);
        check_login();
        bind_views();
        init_wheel();
        init_thumbnail_slider();
        initNavigationMenu();


    }


    private Toolbar toolbar;
    ConstraintLayout open_tickets_view;

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nav_view.findViewById(R.id.menu_tickets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: CLICKED ON ....");
                Intent i = new Intent(context, MyTicketsActivity.class);
                context.startActivity(i);
            }
        });

        nav_view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RegisterActivity.class);
                context.startActivity(i);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        // open drawer at start
        // drawer.openDrawer(GravityCompat.START);
    }

    private void showPredictBottomSheet(MatchModel matchModel) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_predict, null);

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

        final MaterialCardView card_team_1_wins = view.findViewById(R.id.card_team_1_wins);
        final MaterialCardView card_team_2_wins = view.findViewById(R.id.card_team_2_wins);
        final MaterialCardView card_draw = view.findViewById(R.id.card_draw);
        final MaterialCardView card_over = view.findViewById(R.id.card_over);
        final MaterialCardView card_specific = view.findViewById(R.id.card_specific);
        final MaterialCardView card_under = view.findViewById(R.id.card_under);

        card_team_1_wins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                predictionModel = new PredictionModel();
                predictionModel.prediction_type = "win";
                predictionModel.team_1_win = true;
                predictionModel.team_2_win = false;
                predictionModel.prediction_points_to_win = matchModel.points_team_1_win;

                card_team_1_wins.setChecked(true);
                card_team_2_wins.setChecked(false);
                card_draw.setChecked(false);
                card_over.setChecked(false);
                card_specific.setChecked(false);
                card_under.setChecked(false);
            }
        });

        card_team_2_wins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                predictionModel = new PredictionModel();
                predictionModel.prediction_type = "win";
                predictionModel.team_1_win = false;
                predictionModel.team_2_win = true;
                predictionModel.prediction_points_to_win = matchModel.points_team_2_win;


                card_team_1_wins.setChecked(false);
                card_team_2_wins.setChecked(true);
                card_draw.setChecked(false);
                card_over.setChecked(false);
                card_specific.setChecked(false);
                card_under.setChecked(false);
            }
        });

        card_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                predictionModel = new PredictionModel();
                predictionModel.prediction_type = "draw";
                predictionModel.team_1_win = false;
                predictionModel.team_2_win = false;
                predictionModel.prediction_points_to_win = matchModel.points_draw;

                card_team_1_wins.setChecked(false);
                card_team_2_wins.setChecked(false);
                card_draw.setChecked(true);
                card_over.setChecked(false);
                card_specific.setChecked(false);
                card_under.setChecked(false);
            }
        });

        card_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                predictionModel = new PredictionModel();
                predictionModel.prediction_type = "over";
                predictionModel.team_1_win = false;
                predictionModel.team_2_win = false;
                predictionModel.prediction_points_to_win = matchModel.points_over;

                card_team_1_wins.setChecked(false);
                card_team_2_wins.setChecked(false);
                card_draw.setChecked(false);
                card_over.setChecked(true);
                card_specific.setChecked(false);
                card_under.setChecked(false);
            }
        });


        card_under.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                predictionModel = new PredictionModel();
                predictionModel.prediction_type = "under";
                predictionModel.team_1_win = false;
                predictionModel.team_2_win = false;
                predictionModel.prediction_points_to_win = matchModel.points_under;


                card_team_1_wins.setChecked(false);
                card_team_2_wins.setChecked(false);
                card_draw.setChecked(false);
                card_over.setChecked(false);
                card_specific.setChecked(false);
                card_under.setChecked(true);
            }
        });


        ((TextView) (view.findViewById(R.id.team_1_name))).setText(matchModel.team_1_data.team_name);
        ((TextView) (view.findViewById(R.id.team_2_name))).setText(matchModel.team_2_data.team_name);
        ((TextView) (view.findViewById(R.id.win_team_1_name))).setText(matchModel.team_1_data.team_name);
        ((TextView) (view.findViewById(R.id.win_team_2_name))).setText(matchModel.team_2_data.team_name);


        ((TextView) (view.findViewById(R.id.points_team_1_win))).setText("This Team Wins | " + matchModel.points_team_1_win + "");
        ((TextView) (view.findViewById(R.id.points_team_2_win))).setText("This Team Wins | " + matchModel.points_team_2_win + "");
        ((TextView) (view.findViewById(R.id.points_draw))).setText("This Is Draw Match | " + matchModel.points_draw + "");
        ((TextView) (view.findViewById(R.id.points_over))).setText("This match will have more than 2 goals | " + matchModel.points_over + "");
        ((TextView) (view.findViewById(R.id.points_under))).setText("This match will have more than 2 goals | " + matchModel.points_under + "");
        ((TextView) (view.findViewById(R.id.points_specific))).setText("Specify goals for each team | " + matchModel.points_specific + "");


        Tools.display_web_image(
                ((CircularImageView) ((view.findViewById(R.id.team_1_logo)))),
                BASE_URL + matchModel.team_1_data.team_logo,
                context
        );

        Tools.display_web_image(
                ((CircularImageView) ((view.findViewById(R.id.team_2_logo)))),
                BASE_URL + matchModel.team_2_data.team_logo,
                context
        );
    }

    /*private void showSpecificPredictionDialog(
            MatchModel matchModel
    ) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_specific_results);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final EditText specific_team_1_results = (EditText) dialog.findViewById(R.id.specific_team_1_results);
        final EditText specific_team_2_results = (EditText) dialog.findViewById(R.id.specific_team_2_results);


        ((TextView) (dialog.findViewById(R.id.specific_team_1))).setText(matchModel.team_1_data.team_name);
        ((TextView) (dialog.findViewById(R.id.specific_team_2))).setText(matchModel.team_2_data.team_name);

        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        specific_team_1_results.getText().toString().trim().isEmpty() ||
                                specific_team_2_results.getText().toString().trim().isEmpty()
                ) {
                    Toast.makeText(getApplicationContext(), "Please enter specific prediction", Toast.LENGTH_LONG).show();
                    return;
                }
                predictionModel = new PredictionModel();
                predictionModel.prediction_type = "specific";
                predictionModel.prediction_points_to_win = matchModel.points_specific;


                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }*/

    private void bind_views() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigation = findViewById(R.id.navigation);
        predictions_count_view = findViewById(R.id.predictions_count_view);
        home_layout = findViewById(R.id.home_layout);
        spin_layout = findViewById(R.id.spin_layout);
        winners_layout = findViewById(R.id.winners_layout);
        user_layout = findViewById(R.id.user_layout);
        matches_status = findViewById(R.id.matches_status);
        open_tickets_view = findViewById(R.id.open_tickets_view);

        open_tickets_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CheckoutActivity.class);
                context.startActivity(i);
            }
        });
        //bottom_sheet = findViewById(R.id.bottom_sheet_predict);

        //mBehavior = BottomSheetBehavior.from(bottom_sheet);


        show_home();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        show_home();
                        return true;
                    case R.id.navigation_spin:
                        show_spinner();
                        return true;
                    case R.id.navigation_winners:
                        show_winners();
                        break;
                    default:
                        return false;
                    //show_Cart();

                }
                return false;
            }
        });
    }

    void init_matches() {
        if (matches_initialized) {
            return;
        }


        RecyclerView matches_recycler_view = findViewById(R.id.matches_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        matches_recycler_view.setLayoutManager(layoutManager);
        matches_recycler_view.setHasFixedSize(true);

        if (adapterMatches == null) {
            adapterMatches = new AdapterMatches(this, databaseRepository, myPredictions);
        }


        databaseRepository.get_matches(databaseRepository).observe((LifecycleOwner) context, new Observer<List<MatchModel>>() {
            @Override
            public void onChanged(List<MatchModel> matchModels) {


                if (matchModels == null) {
                    matches_status.setText("No Matches Found.");
                    matches_status.setVisibility(View.VISIBLE);
                    return;
                }
                if (matchModels.isEmpty()) {
                    matches_status.setText("No Matches Available.");
                    matches_status.setVisibility(View.VISIBLE);
                } else {
                    matches_status.setVisibility(View.GONE);
                }

                if (!matches_initialized) {
                    matches_recycler_view.setAdapter(adapterMatches);
                    adapterMatches.setOnItemClickListener(new AdapterMatches.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, MatchModel obj, int position) {
                            //showPredictBottomSheet(obj);
                            obj.init();
                        }
                    });
                    matches_initialized = true;
                }
                adapterMatches.updateItems(matchModels);
            }
        });

    }

    List<PredictionModel> myPredictions = new ArrayList<>();

    void show_home() {
        if (matches_initialized) {
            init_matches();
        } else {
            try {
                databaseRepository.get_predictions().observe((LifecycleOwner) context, new Observer<List<PredictionModel>>() {
                    @Override
                    public void onChanged(List<PredictionModel> predictionModel) {
                        if (predictionModel == null) {
                            init_matches();
                            return;
                        }
                        myPredictions = predictionModel;
                        if (myPredictions.size() < 10) {
                            predictions_count_view.setText(myPredictions.size() + "");
                        } else {
                            predictions_count_view.setText(myPredictions.size() + "+");
                        }

                        init_matches();

                    }
                });
            } catch (Exception e) {
                init_matches();
            }
        }
        home_layout.setVisibility(View.VISIBLE);
        spin_layout.setVisibility(View.GONE);
        winners_layout.setVisibility(View.GONE);
        user_layout.setVisibility(View.GONE);
    }

    void show_spinner() {
        databaseRepository.get_matches(databaseRepository);
        home_layout.setVisibility(View.GONE);
        spin_layout.setVisibility(View.VISIBLE);
        winners_layout.setVisibility(View.GONE);
        user_layout.setVisibility(View.GONE);
    }

    void show_winners() {
        home_layout.setVisibility(View.GONE);
        spin_layout.setVisibility(View.GONE);
        winners_layout.setVisibility(View.VISIBLE);
        user_layout.setVisibility(View.GONE);

    }

    private void init_wheel() {
        final LuckyWheelView luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "100";
        luckyItem1.icon = R.drawable.test1;
        luckyItem1.color = 0xffFFF3E0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "200";
        luckyItem2.icon = R.drawable.test2;
        luckyItem2.color = 0xffFFE0B2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "300";
        luckyItem3.icon = R.drawable.test3;
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "400";
        luckyItem4.icon = R.drawable.test4;
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "500";
        luckyItem5.icon = R.drawable.test5;
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "600";
        luckyItem6.icon = R.drawable.test6;
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);
        //////////////////

        //////////////////////
        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "700";
        luckyItem7.icon = R.drawable.test7;
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "800";
        luckyItem8.icon = R.drawable.test8;
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);


        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.topText = "900";
        luckyItem9.icon = R.drawable.test9;
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);
        ////////////////////////

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.topText = "1000";
        luckyItem10.icon = R.drawable.test10;
        luckyItem10.color = 0xffFFE0B2;
        data.add(luckyItem10);

        LuckyItem luckyItem11 = new LuckyItem();
        luckyItem11.topText = "2000";
        luckyItem11.icon = R.drawable.test10;
        luckyItem11.color = 0xffFFE0B2;
        data.add(luckyItem11);

        LuckyItem luckyItem12 = new LuckyItem();
        luckyItem12.topText = "3000";
        luckyItem12.icon = R.drawable.test10;
        luckyItem12.color = 0xffFFE0B2;
        data.add(luckyItem12);


        luckyWheelView.setData(data);
        luckyWheelView.setRound(8);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = getRandomIndex();
                luckyWheelView.startLuckyWheelWithTargetIndex(index);
            }
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                Toast.makeText(getApplicationContext(), data.get(index).topText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1) + 0;
    }

    private void init_thumbnail_slider() {

        SliderView sliderView;
        sliderView = findViewById(R.id.imageSlider);
        SliderAdapter adapter;
        adapter = new SliderAdapter(context, 2);

        for (int i = 0; i < 4; i++) {
            SliderModel sliderModel = new SliderModel();
            sliderModel.image_url = "https://e0.365dm.com/16/03/2048x1152/la-liga-premier-league-graphic-messi-rooney_3431189.jpg";
            if (i == 0) {
                sliderModel.image_url = "https://www.bt.com/content/dam/bt/portal/images/articles/sport/football/PLFixturesWhen.jpg";
            } else if (i == 1) {
                sliderModel.image_url = "https://www.si.com/.image/t_share/MTc1MzA2MTU2MjEyNzU4MDQ2/premier-league-season-preview-liverpool-title.jpg";
            } else if (i == 2) {
                sliderModel.image_url = "https://insider-voice.com/wp-content/uploads/skysports-graphic-premier-league_5379844.png";
            }
            adapter.addItem(sliderModel);
        }


        sliderView.setIndicatorEnabled(false);
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setInfiniteAdapterEnabled(true);
        adapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SliderModel sliderModel, int position) {
                //show_full_image(position);
                Toast.makeText(context, "Clicked on ==> " + position, Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "Clicked on ==> " + sliderModel.image_url, Toast.LENGTH_SHORT).show();
            }

        });


    }


    private void check_login() {
        databaseRepository.get_logged_in_user().observe(this, new Observer<LoggedInUserModel>() {
            @Override
            public void onChanged(LoggedInUserModel loggedInUserModel) {
                if (loggedInUserModel == null) {
                    Toast.makeText(StartActivity.this, "Not Logged in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(StartActivity.this, RegisterActivity.class);
                    StartActivity.this.startActivity(i);
                    finish();
                    return;
                } else {

//                    databaseRepository.login_user(loggedInUserModel);
//                    Intent i = new Intent(StartActivity.this, MainActivity.class);
//                    StartActivity.this.startActivity(i);
//                    finish();
                }
            }
        });
    }
}