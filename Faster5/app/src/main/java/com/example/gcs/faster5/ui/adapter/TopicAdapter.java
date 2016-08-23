package com.example.gcs.faster5.ui.adapter;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.example.gcs.faster5.R;
import com.example.gcs.faster5.ui.activity.SearchOpponent;
import com.example.gcs.faster5.model.Topic;

import java.util.List;

/**
 * Created by Kien on 07/14/2016.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private List<Topic> imageList;

    public TopicAdapter(List<Topic> imageList) {
        this.imageList = imageList;
    }

    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_list, parent, false);
        return new TopicAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.iImageButton.setImageResource(imageList.get(position).getPhoto());
        holder.setTopicId(imageList.get(position).getIdTopic());
        holder.setTopicName(imageList.get(position).getNameTopic());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageButton iImageButton;
        public int topicId = -1;
        public String topicName;

        public ViewHolder(View view) {
            super(view);
            iImageButton = (ImageButton) view.findViewById(R.id.footballButton);
            view.setOnClickListener(this);
        }

        public void setTopicId(int topicId) {
            this.topicId = topicId;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }


        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), SearchOpponent.class);
        //    intent.putExtra(SearchOpponent.EXTRA_ID, topicId);
          //  intent.putExtra(SearchOpponent.EXTRA_NAME, topicName);
            if (topicId != 0) {
                view.getContext().startActivity(intent);
            }
        }
    }
}