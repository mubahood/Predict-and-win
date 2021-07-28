package com.soccerpredict.spinthewheeltowin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.Tools;
import com.soccerpredict.spinthewheeltowin.db.DatabaseRepository;
import com.soccerpredict.spinthewheeltowin.model.MatchModel;
import com.soccerpredict.spinthewheeltowin.model.TicketModel;

import java.util.List;

public class AdapterTickets extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AdapterCheckout";
    private final Context ctx;
    DatabaseRepository databaseRepository;
    private OnItemClickListener mOnItemClickListener;
    List<TicketModel> ticketModelList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterTickets(Context context, DatabaseRepository databaseRepository1, List<TicketModel> ticketModels) {
        this.ctx = context;
        this.databaseRepository = databaseRepository1;
        this.ticketModelList = ticketModels;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tickets, parent, false);
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
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final TicketModel t = ticketModelList.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            vItem.title.setText(Tools.toTimeAgo(t.prediction_date + ""));
            if (t.is_active) {
                vItem.title.setText(Tools.toTimeAgo("Pending..."));
            } else {
                if (t.is_success) {
                    vItem.title.setText(Tools.toTimeAgo("Win"));
                    vItem.title.setBackgroundColor(R.color.green_800);
                } else {
                    vItem.title.setBackgroundColor(R.color.red_800);
                    vItem.title.setText(Tools.toTimeAgo("Loss"));
                }

            }


        }
    }

    int x = 0;

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.ticketModelList.size();
    }

    TextView total_view;

    public void updateItems(List<TicketModel> ticketModels) {

        this.ticketModelList = ticketModels;

        update_total_view();
        notifyDataSetChanged();
    }

    float tot = 0;

    private void update_total_view() {
        tot = 0;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, MatchModel obj, int position);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView total_points;
        public TextView status;
        public ImageView close;
        public LinearLayout lyt_parent;


        public ItemViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            close = v.findViewById(R.id.close);
            status = v.findViewById(R.id.status);
            total_points = v.findViewById(R.id.total_points);
            lyt_parent = v.findViewById(R.id.lyt_parent);

        }
    }

}
