package com.example.gcs.faster5;

/**
 * Created by Kien on 07/14/2016.
 */
public class Topic {
    private int photo;
    private int idTopic;
    private String nameTopic;


    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(int idTopic) {
        this.idTopic = idTopic;
    }

    public String getNameTopic() {
        return nameTopic;
    }

    public void setNameTopic(String nameTopic) {
        this.nameTopic = nameTopic;
    }

    public Topic(int idTopic, String nameTopic , int photo) {
        this.idTopic = idTopic;
        this.nameTopic = nameTopic;

        this.photo = photo;
    }
}
