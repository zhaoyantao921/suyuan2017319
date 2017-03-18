package net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import dao.Type;

/**
 * Created by haohao on 2017/3/1.
 *
 * http://202.121.66.53:8080/MySpringMybatis/alltypeApi
 *
 */

public class GetRypeList   {
    public GetRypeList(final SuccessCallback successCallback,final FailCallback failCallback) {
        new NetConnection(Config.API_GET_ALL_LIST, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                if(result==Config.ERROR){
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }else {

                    try {
                        Gson gson=new Gson();
                        List<Type> typeList=gson.fromJson(result,new TypeToken<List<Type>>(){}.getType());

                        if(successCallback!=null){
                            successCallback.onSuccess(typeList);}
                    } catch (JsonSyntaxException e) {
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
        }


        );



    }
   public static interface  SuccessCallback{
       void  onSuccess(List<Type> typeList);
   }
    public static  interface FailCallback{
        void onFail();
    }

}
