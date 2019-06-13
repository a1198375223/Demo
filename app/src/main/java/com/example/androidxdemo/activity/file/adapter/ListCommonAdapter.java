package com.example.androidxdemo.activity.file.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidxdemo.R;
import com.example.androidxdemo.base.bean.MediaItem;
import com.example.androidxdemo.base.callback.OnItemClickListener;
import com.example.androidxdemo.base.image.ImageFactory;
import com.example.androidxdemo.utils.FrescoUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class ListCommonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MediaItem> models;
    private OnItemClickListener mListener;

    public ListCommonAdapter() {
        models = new ArrayList<>();
    }

    public void setListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<MediaItem> models) {
        this.models = models;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_common_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MyHolder) {
            ((MyHolder) viewHolder).position = i;
            ((MyHolder) viewHolder).text1.setText(models.get(i).getPath());
            ((MyHolder) viewHolder).text2.setText(models.get(i).getTitle());
            if (models.get(i).getCoverUrl() != null) {
                FrescoUtils.loadImage(((MyHolder) viewHolder).image,
                        ImageFactory.newLocalImage(models.get(i)
                                .getCoverUrl())
                                .setAutoPlayAnimation(true)
                                .setScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                                .build());
            }
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        SimpleDraweeView image;
        int position;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            text1= (TextView) itemView.findViewById(R.id.text);
            text2 = (TextView) itemView.findViewById(R.id.text1);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClickListener(v, position);
                    }
                }
            });
        }
    }
}

