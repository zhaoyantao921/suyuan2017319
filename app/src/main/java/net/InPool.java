package net;

import android.util.Log;

/**
 * Created by zhao on 2017/3/3.
 *   //1.出塘：参数poolid,typeid,time
 *   http://202.121.66.53:8080/MySpringMybatis/sendPoolTypeApi?poolid=1&typeid=1&time=2017-03-02
 *   出塘操作接口
 */

public class InPool {
    public InPool(String poolid,String typeid,String time,final SuccessCallback successCallback,final FailCallback failCallback) {

        new NetConnection(Config.API_POST_IN_POOL, HttpMethod.GET,new NetConnection.SuccessCallback() {
            @Override

            public void onSuccess(String result) {
                Log.d("出塘结果",result);
                if(result.equals(Config.SUCCESS)){
                    if(successCallback!=null){
                        successCallback.onSuccess();
                    }
                }else {
                    if(failCallback!=null){
                        failCallback.onFail();
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
        },"poolid",poolid,"typeid",typeid,"time",time);

    }

    public  static  interface  SuccessCallback {

        void onSuccess();

    }
    public static  interface FailCallback{
        void onFail();
    }


}
