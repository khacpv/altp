package com.example.gcs.faster5.network;

import com.example.gcs.faster5.model.Topic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by khacpham on 7/16/16.
 */
public interface ServiceApi {

    @GET("topic")
    Call<List<Topic>> getTopic();
}
