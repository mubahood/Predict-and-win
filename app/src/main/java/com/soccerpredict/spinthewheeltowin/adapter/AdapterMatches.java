package com.soccerpredict.spinthewheeltowin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.Tools;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;

import java.util.ArrayList;
import java.util.List;

import static com.soccerpredict.spinthewheeltowin.activity.StartActivity.BASE_URL;

public class AdapterMatches extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MatchModel> items = new ArrayList<MatchModel>();
    DatabaseRepository databaseRepository;

    private final Context ctx;
    private OnItemClickListener mOnItemClickListener;
    List<PredictionModel> myPredictions = new ArrayList<>();

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterMatches(Context context, DatabaseRepository databaseRepository1, List<PredictionModel> predictionModels) {
        this.ctx = context;
        this.databaseRepository = databaseRepository1;
        this.myPredictions = predictionModels;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_1, parent, false);
        vh = new ItemViewHolder(v);
        /*if (items.get(viewType).equals("pending")) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_1, parent, false);
            vh = new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_1, parent, false);
            vh = new ItemViewHolder(v);
        }*/
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final MatchModel m = items.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            m.init();
            vItem.team_1_name.setText(m.team_1_data.team_name);
            vItem.team_2_name.setText(m.team_2_data.team_name);
            vItem.team_1_results.setText(m.team_1_results + "");
            vItem.team_2_results.setText(m.team_2_results + "");

            for (PredictionModel p : myPredictions) {
                if (m.matche_id == p.match_id) {
                    if (p.team_1_win) {
                        vItem.button_draw.setChecked(false);
                        vItem.button_home.setChecked(true);
                        vItem.button_away.setChecked(false);
                    } else if (p.is_draw) {
                        vItem.button_draw.setChecked(true);
                        vItem.button_home.setChecked(false);
                        vItem.button_away.setChecked(false);
                    } else if (p.team_2_win) {
                        vItem.button_draw.setChecked(false);
                        vItem.button_home.setChecked(false);
                        vItem.button_away.setChecked(true);
                    }
                    break;
                }
            }

            vItem.button_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PredictionModel predictionModel = new PredictionModel();
                    predictionModel.match_id = m.matche_id;
                    predictionModel.predicted_by = 0;
                    predictionModel.team_1_win = true;
                    predictionModel.team_2_win = false;
                    predictionModel.is_draw = false;
                    predictionModel.is_under = false;
                    predictionModel.is_over = false;
                    predictionModel.prediction_type = "predict";
                    predictionModel.prediction_points_to_win = m.points_team_1_win;
                    predictionModel.prediction_points_to_win = 2;
                    final List<PredictionModel> predictionModels = new ArrayList<>();
                    predictionModels.add(predictionModel);
                    databaseRepository.save_predictions(predictionModels);
                }
            });

            vItem.button_draw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PredictionModel predictionModel = new PredictionModel();
                    predictionModel.match_id = m.matche_id;
                    predictionModel.predicted_by = 0;
                    predictionModel.team_1_win = false;
                    predictionModel.team_2_win = false;
                    predictionModel.is_draw = true;
                    predictionModel.is_under = false;
                    predictionModel.is_over = false;
                    predictionModel.prediction_type = "predict";
                    predictionModel.prediction_points_to_win = m.points_draw;
                    predictionModel.prediction_points_to_win = 2;
                    final List<PredictionModel> predictionModels = new ArrayList<>();
                    predictionModels.add(predictionModel);
                    databaseRepository.save_predictions(predictionModels);
                }
            });

            vItem.button_away.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PredictionModel predictionModel = new PredictionModel();
                    predictionModel.match_id = m.matche_id;
                    predictionModel.predicted_by = 0;
                    predictionModel.team_1_win = false;
                    predictionModel.team_2_win = true;
                    predictionModel.is_draw = false;
                    predictionModel.is_under = false;
                    predictionModel.is_over = false;
                    predictionModel.prediction_type = "predict";
                    predictionModel.prediction_points_to_win = m.points_team_2_win;
                    predictionModel.prediction_points_to_win = 3;
                    final List<PredictionModel> predictionModels = new ArrayList<>();
                    predictionModels.add(predictionModel);
                    databaseRepository.save_predictions(predictionModels);
                }
            });


            String mybuttontext = "Man U" + System.getProperty("line.separator") + "3.0";


            if (m.match_status.equals("done")) {
                vItem.results_container.setVisibility(View.VISIBLE);
                vItem.vs_container.setVisibility(View.GONE);
            } else {
                vItem.vs_container.setVisibility(View.VISIBLE);
                vItem.results_container.setVisibility(View.GONE);
            }

            Tools.display_web_image(
                    vItem.team_1_logo,
                    BASE_URL + m.team_1_data.team_logo,
                    ctx
            );

            Tools.display_web_image(
                    vItem.team_2_logo,
                    BASE_URL + m.team_2_data.team_logo,
                    ctx
            );

            /*if (m.receive_time == null) {
                vItem.message_status.setImageResource(R.drawable.ic_processing);
            } else if (m.receive_time.equals("not_sent")) {
                vItem.message_status.setImageResource(R.drawable.ic_processing);
            } else if (m.receive_time.equals("sent")) {
                vItem.message_status.setImageResource(R.drawable.ic_check);
            } else if (m.receive_time.equals("downloaded")) {
                vItem.message_status.setImageResource(R.drawable.ic_done);
            } else if (m.receive_time.equals("seen")) {
                vItem.message_status.setImageResource(R.drawable.ic_complete);
            }
            vItem.text_time.setText(toTimeAgo(m.sent_time + "")
            );*/

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, m, position);
                    }
                }
            });
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (this.items.get(position).match_status.equals("pending"))
            return 1;
        else
            return 2;

    }

    public void insertItem(MatchModel item) {
        this.items.add(item);
        notifyItemInserted(getItemCount());
    }

    public void updateItems(List<MatchModel> _items) {
        this.items = _items;
        notifyItemInserted(getItemCount());
    }

    public void setItems(List<MatchModel> items) {
        this.items = items;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, MatchModel obj, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView team_1_name;
        public TextView team_2_name;
        public TextView team_1_results;
        public TextView team_2_results;
        public CircularImageView team_1_logo;
        public CircularImageView team_2_logo;
        public View results_container;
        public MaterialButton button_home;
        public MaterialButton button_draw;
        public MaterialButton button_away;
        public View vs_container;
        public View lyt_parent;

        public ItemViewHolder(View v) {
            super(v);
            team_1_name = v.findViewById(R.id.team_1_name);
            team_2_name = v.findViewById(R.id.team_2_name);
            team_1_logo = v.findViewById(R.id.team_1_logo);
            team_2_logo = v.findViewById(R.id.team_2_logo);
            team_1_results = v.findViewById(R.id.team_1_results);
            team_2_results = v.findViewById(R.id.team_2_results);
            results_container = v.findViewById(R.id.results_container);
            vs_container = v.findViewById(R.id.vs_container);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            button_draw = v.findViewById(R.id.button_draw);
            button_away = v.findViewById(R.id.button_away);
            button_home = v.findViewById(R.id.button_home);
        }
    }

}
