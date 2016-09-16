package com.oic.game.ailatrieuphu.logic;

import com.oic.game.ailatrieuphu.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khacpham on 7/16/16.
 */
public class TopicMng {

    private static List<Topic> sTopics = new ArrayList<>();

    public static void updateTopicList(List<Topic> topics){
        sTopics.clear();
        sTopics.addAll(topics);
    }

    public static List<Topic> getAllItemList() {


        List<Topic> allItems = new ArrayList<Topic>();
      // allItems.add(new Topic(1, "FootBall", R.drawable.football));
//        allItems.add(new Topic(2, "Art", R.drawable.art));
//        allItems.add(new Topic(3, "Basic Math", R.drawable.math));
//        allItems.add(new Topic(4, "Fruits", R.drawable.fruit));
//        allItems.add(new Topic(5, "Music", R.drawable.music));
//        allItems.add(new Topic(6, "Technology", R.drawable.tech));
//        allItems.add(new Topic(0, "Lock", R.drawable.lock));

        return sTopics;
    }
}
