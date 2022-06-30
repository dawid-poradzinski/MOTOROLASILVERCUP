package com.bluescript.youknowit.api;

import android.content.Context;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RequestRunner {
    static public void sendRequest(Context context, String type, String argument){
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
        CronetEngine cronetEngine = myBuilder.build();

        String url = "http://192.168.0.100:3000/healthCheck";
        Executor executor = Executors.newSingleThreadExecutor();

        if(type == "GET"){
            getSetsFromServer(executor, cronetEngine, url);
        } else if(type == "REMOVE"){
            postRemoveCallback(executor, cronetEngine, argument, url);
        } else {
            postSetToServer(executor, cronetEngine, argument, url);
        }
    }
    static private void getSetsFromServer(Executor executor, CronetEngine cronetEngine, String url){
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(url, new MyUrlRequestCallback(), executor);
        UrlRequest request = requestBuilder.build();
        request.start();
    }

    static private void postSetToServer(Executor executor, CronetEngine cronetEngine, String uuid, String url){
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(url, new MyUrlRequestCallback(), executor);

    }
    static private void postRemoveCallback(Executor executor, CronetEngine cronetEngine, String uuid, String url){
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(url, new MyUrlRequestCallback(), executor);
    }
}
