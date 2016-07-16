package com.example.gcs.faster5.logic;

import com.example.gcs.faster5.R;
import com.example.gcs.faster5.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khacpham on 7/16/16.
 */
public class TopicMng {

    public static List<Topic> getAllItemList() {

        List<Topic> allItems = new ArrayList<Topic>();
        allItems.add(new Topic(1, "FootBall", R.drawable.football));
        allItems.add(new Topic(2, "Art", R.drawable.art));
        allItems.add(new Topic(3, "Basic Math", R.drawable.math));
        allItems.add(new Topic(4, "Fruits", R.drawable.fruit));
        allItems.add(new Topic(5, "Music", R.drawable.music));
        allItems.add(new Topic(6, "Technology", R.drawable.tech));
        allItems.add(new Topic(0, "Lock", R.drawable.lock));

        return allItems;
    }
}
