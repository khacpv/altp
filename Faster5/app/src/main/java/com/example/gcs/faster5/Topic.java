package com.example.gcs.faster5;

/**
 * Created by Kien on 07/14/2016.
 */
public class Topic {
    private int mPhoto;
    private int mTopicId;
    private String mTopicName;

    public int getPhoto() {
        return mPhoto;
    }

    public void setPhoto(int photo) {
        this.mPhoto = photo;
    }

    public int getIdTopic() {
        return mTopicId;
    }

    public void setIdTopic(int idTopic) {
        this.mTopicId = idTopic;
    }

    public String getNameTopic() {
        return mTopicName;
    }

    public void setNameTopic(String topicName) {
        this.mTopicName = topicName;
    }

    public Topic(int topicId, String topicName, int photo) {
        this.mTopicId = topicId;
        this.mTopicName = topicName;

        this.mPhoto = photo;
    }
}
