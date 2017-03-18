package net;

import android.util.Log;

/**
 * Created by zhao on 2017/3/8.
 * 参数
 * baseid
 * poolid
 * typeid
 * flowCenterid
 * inTime
 * ouTime
 * http://202.121.66.53:8080/MySpringMybatis/addProduceApi?baseid=3&poolid=1&typeid=1&flowCenterid=014&inTime=2017-03-07&ouTime=2017-03-08
 */

public class OutPool {

    public OutPool(String baseid, String poolid, String typeid, String flowCenterid, String inTime, String ouTime,String sum, final SuccessCallback successCallback, final FailCallback failCallback) {

        new NetConnection(Config.API_POST_OUT_POOL, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override

            public void onSuccess(String result) {
                Log.d("出塘结果返回值",result);
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
                if (failCallback!=null){
                    failCallback.onFail();
                }

            }
        },"baseid",baseid,"poolid",poolid,"typeid",typeid,"flowCenterid",flowCenterid,"inTime",inTime,"ouTime",ouTime,"sum",sum);

    }


    public static interface SuccessCallback{
        void onSuccess();
    }
    public static  interface FailCallback{
        void onFail();
    }


}
