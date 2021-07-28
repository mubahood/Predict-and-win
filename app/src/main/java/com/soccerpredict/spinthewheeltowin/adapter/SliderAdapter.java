package com.soccerpredict.spinthewheeltowin.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.soccerpredict.spinthewheeltowin.R;
import com.soccerpredict.spinthewheeltowin.model.SliderModel;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderModel> mSliderItems = new ArrayList<SliderModel>();
    ArrayList<SliderModel> sliderItems = new ArrayList<SliderModel>();

    public SliderAdapter(Context context, int style) {
        this.context = context;
        this.style = style;
    }

    public void renewItems(List<SliderModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderModel sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {

        View inflate;
        if (style == 1) {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_1, null);
        } else if (style == 2) {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_2, null);
        } else if (style == 3) {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_3, null);
        } else {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_1, null);
        }
        return new SliderAdapterVH(inflate);
    }

    int myCounter = 0;


    public void setSliderItem(SliderModel sliderItem) {
        this.sliderItem = sliderItem;
    }


    public interface OnItemClickListener {
        void onItemClick(SliderModel SliderModel, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    SliderModel sliderItem = new SliderModel();
    int style = 1;
    int temp_counter = 0;
    private static final String TAG = "MubhaoodSliderAdapter";
    String img_link = "";

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        sliderItem = mSliderItems.get(position);


        viewHolder.title.setText(sliderItem.title);
        viewHolder.sub_title.setText(sliderItem.subtitle);
        viewHolder.details.setText(sliderItem.details);
        viewHolder.details.setText(sliderItem.details);

        temp_counter++;
        img_link = sliderItem.image_url;
        Log.d(TAG, "onBindViewHolder: ======================> \n\n" + img_link + "\n\n\n");
        if (style == 1) {
            Glide.with(viewHolder.image_1)
                    .load(img_link)
                    .placeholder(R.drawable.test_slide_1)
                    .fitCenter()
                    .into(viewHolder.image_1);
            Log.d(TAG, "onBindViewHolder: 11 ====> " + img_link);
        } else if (style == 2 || style == 3) {

            viewHolder.title.setText((position + 1) + " of " + mSliderItems.size());
            Glide.with(context)
                    .load(img_link)
                    .placeholder(R.drawable.test_slide_1)
                    .into(viewHolder.image_1);
            Log.d("mubss", "onBindViewHolder: " + sliderItem.image_url);

        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                mOnItemClickListener.onItemClick(mSliderItems.get(position), position);
            }
        });


        myCounter++;


    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    TextView SliderModel_name, SliderModel_price;

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView image_1;
        ImageView image_2;
        TextView title;
        TextView sub_title;
        TextView details;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            image_1 = itemView.findViewById(R.id.image_1);
            image_2 = itemView.findViewById(R.id.image_2);
            title = itemView.findViewById(R.id.title);
            sub_title = itemView.findViewById(R.id.sub_title);
            details = itemView.findViewById(R.id.details);
            this.itemView = itemView;
        }
    }

}

