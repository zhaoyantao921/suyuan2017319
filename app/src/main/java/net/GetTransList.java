package net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import dao.TransBase;

/**
 * Created by zhao on 2017/3/8.
 * http://202.121.66.53:8080/MySpringMybatis/allSeletCenterApi
 */

public class GetTransList {
    public GetTransList(final SuccessCallback successCallback,final FailCallback failCallback) {
        new NetConnection(Config.API_GET_allSeletCenterApi, HttpMethod.GET,new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                if(result!=null){
                    try {
                        Gson gson=new Gson();
                        List<TransBase> transBaseList=gson.fromJson(result,new TypeToken<List<TransBase>>(){}.getType());
                        if(successCallback!=null){
                            successCallback.onSuccess(transBaseList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (failCallback != null) {
                            failCallback.onFail();
                        }
                    }
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                if(failCallback!=null){
                    failCallback.onFail();
                }
            }
        });


    }

    public static interface  SuccessCallback{
        void  onSuccess(List<TransBase> transBaseList);
    }
    public static  interface FailCallback{
        void onFail();
    }
}
