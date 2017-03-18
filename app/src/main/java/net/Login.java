package net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import dao.Pool;

/**
 * Created by zhao on 2017/2/28.
 */

public class Login {
    public Login(String name,String password,final SuccessCallback successCallback,final FailCallback failCallback) {
        new NetConnection(Config.API_LOGIN,HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(result==Config.ERROR){
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }
                else {
                    try {
                        Gson gson = new Gson();
                        List<Pool> poolList = gson.fromJson(result, new TypeToken<List<Pool>>() {
                        }.getType());
                        if(successCallback!=null){
                        successCallback.onSuccess(poolList);}

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
                if (failCallback!=null){
                    failCallback.onFail();
                }
            }
        }, "name",name,"password",password
        );
    }

    public static interface SuccessCallback{
        void onSuccess(List<Pool> poolList);
    }
    public static  interface FailCallback{
        void onFail();
    }
}
