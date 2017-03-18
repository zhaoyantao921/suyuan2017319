package net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import dao.Breed;

/**
 * Created by zhao on 2017/3/1.
 *
 *
 http://202.121.66.53:8080/MySpringMybatis/typeInfoByPoolidApi?poolid=1
 */

public class GetTypeByID {

    public GetTypeByID(String poolid,final SuccessCallback successCallback,final FailCallback failCallback) {
        new NetConnection(Config.API_GET_TYPE_LIST, HttpMethod.GET,
                new NetConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if(result==Config.ERROR){
                            if (failCallback != null) {
                                failCallback.onFail();
                            }
                        }else {

                            try {
                                Gson gson=new Gson();
                                List<Breed> breedlist=gson.fromJson(result,new TypeToken<List<Breed>>(){}.getType());

                                if(successCallback!=null){
                                    successCallback.onSuccess(breedlist);}
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                if (failCallback != null) {
                                    failCallback.onFail();
                                }
                            }
                        }
                    }
                },
                new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        },"poolid",poolid

        );


    }




   public  static  interface  SuccessCallback {
       void onSuccess(List<Breed> breedlist);
   }

    public static  interface FailCallback{
        void onFail();
    }
}
