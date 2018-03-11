package com.example.prem.findimage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prem.findimage.dataobjects.Image;
import com.example.prem.findimage.R;
import com.example.prem.findimage.util.RecyclerViewClickListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prem on 09-Mar-18.
 */

/**
 * Custom Recycler view adapter
 */
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {
    private List<Image> dataSet = new ArrayList<>();
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;
    public CustomRecyclerViewAdapter(Context context, List<Image> myDataSet, RecyclerViewClickListener listener){
        this.dataSet = myDataSet;
        this.context = context;
        this.recyclerViewClickListener = listener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_title;
        public SimpleDraweeView simpleDraweeView;
        private RecyclerViewClickListener mListener;
        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener =listener;
            simpleDraweeView = itemView.findViewById(R.id.image_view);
            tv_title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view,getAdapterPosition());
        }
    }
    @Override
    public CustomRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,recyclerViewClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomRecyclerViewAdapter.ViewHolder holder, int position) {
        final Image image = dataSet.get(position);
        SimpleDraweeView simpleDraweeView = holder.simpleDraweeView;
        simpleDraweeView.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
        simpleDraweeView.setImageURI(image.getUrl());
        holder.tv_title.setText(image.getTitle());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
