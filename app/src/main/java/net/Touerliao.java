package net;

/**
 * Created by zhao on 2017/3/7.
 */

public class Touerliao {
    public Touerliao(String poolid,String baltid,String balttime,final SuccessCallback successCallback,final FailCallback failCallback) {

        new NetConnection(Config.API_TOUERLIAO, HttpMethod.GET, new NetConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) {
                System.out.println("3.1.Result:"+result);//yunxing
                if(result.equals(Config.SUCCESS)){
                    System.out.println("4.Result:"+result);//yunxing
                    successCallback.onSuccess(result);
                }


            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                failCallback.onFail();

            }
        },"poolid",poolid,"baltid",baltid,"balttime",balttime);


    }

    public static  interface SuccessCallback{
        void onSuccess(String result);
    }
    public static interface FailCallback{
        void onFail();
    }

}
