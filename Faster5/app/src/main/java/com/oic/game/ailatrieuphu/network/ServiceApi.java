package com.oic.game.ailatrieuphu.network;

import com.oic.game.ailatrieuphu.model.Question;
import com.oic.game.ailatrieuphu.model.Topic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by khacpham on 7/16/16.
 */
public interface ServiceApi {
    @GET("topic") Call<List<Topic>> getTopic();
    @GET("topic/{topic}/question") Call<List<Question>> getQuestion(@Path("topic") int topicId);
}
