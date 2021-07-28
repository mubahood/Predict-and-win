package com.soccerpredict.spinthewheeltowin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.PredictionModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterCheckout extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AdapterCheckout";
    private final Context ctx;
    DatabaseRepository databaseRepository;
    int x = 0;
    TextView total_view;
    float tot = 0;
    private List<PredictionModel> myPredictions = new ArrayList<PredictionModel>();
    private List<MatchModel> matchModels = new ArrayList<MatchModel>();
    private OnItemClickListener mOnItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterCheckout(Context context, DatabaseRepository databaseRepository1) {
        this.ctx = context;
        this.databaseRepository = databaseRepository1;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
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
            final PredictionModel p = myPredictions.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;

            vItem.points_to_win.setText(p.prediction_points_to_win + "");

            for (MatchModel m : matchModels) {
                m.init();
                vItem.title.setText(m.team_1_data.team_name + " Vs. " + m.team_2_data.team_name);
                if (m.matche_id == p.match_id) {
                    if (p.team_1_win) {
                        vItem.your_choice.setText("Home");
                    } else if (p.is_draw) {
                        vItem.your_choice.setText("Draw");
                    } else if (p.team_2_win) {
                        vItem.your_choice.setText("Away");
                    }
                    break;
                }
            }

            vItem.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    x = 0;

                    for (PredictionModel _p : myPredictions) {
                        if (_p.match_id == p.match_id) {
                            databaseRepository.delete_predictions(p.match_id);
                            myPredictions.remove(x);
                            notifyItemRemoved(x);
                            x++;
                            update_total_view();
                            break;
                        }
                    }
                }
            });

        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myPredictions.size();
    }

    public void updateItems(List<MatchModel> matchModels, List<PredictionModel> _items, TextView total_view) {
        this.matchModels.clear();
        this.myPredictions.clear();
        this.total_view = total_view;
        this.matchModels = matchModels;
        this.myPredictions = _items;
        update_total_view();
        notifyDataSetChanged();
    }

    private void update_total_view() {
        tot = 0;
        for (PredictionModel p : this.myPredictions) {
            tot += p.prediction_points_to_win;
        }
        if (total_view != null) {
            total_view.setText(tot + "");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, MatchModel obj, int position);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView your_choice;
        public TextView points_to_win;
        public ImageView close;
        public LinearLayout lyt_parent;


        public ItemViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            close = v.findViewById(R.id.close);
            your_choice = v.findViewById(R.id.your_choice);
            points_to_win = v.findViewById(R.id.points_to_win);
            lyt_parent = v.findViewById(R.id.lyt_parent);

        }
    }

}
