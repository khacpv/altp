package com.example.gcs.faster5.network;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by khacpham on 7/16/16.
 */
public class ServiceMng {

    public ServiceMng(){

    }

    public ServiceApi api(){

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://private-08106-imagepro.apiary-mock.com/faster5/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(logging);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Retrofit retrofit = builder.client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(ServiceApi.class);
    }
}
