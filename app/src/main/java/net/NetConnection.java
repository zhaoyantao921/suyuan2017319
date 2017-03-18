package net;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhao on 2017/2/28.
 */

public class NetConnection {

    /*url 接口地址
* method 请求方式
* successCallback 请求成功时的回调函数
* failCallback  请求失败时的回调
* kvs 参数 字符串
* */
    public NetConnection(final String url, final HttpMethod method, final SuccessCallback successCallback, final FailCallback failCallback, final String ... kvs) {

        new AsyncTask<Void,Void,String >(){

            @Override
            protected String doInBackground(Void... voids) {

                StringBuffer paramStr=new StringBuffer();
                for(int i=0;i<kvs.length;i+=2){
                    paramStr.append(kvs[i]).append("=").append(kvs[i+1]).append("&");
                }
                //paramStr=paramStr.deleteCharAt(paramStr.length()-1);
                try {
                    HttpURLConnection urlConnection = null;
                    switch (method){
                        case POST:
                            urlConnection=(HttpURLConnection) new URL(url).openConnection();
                            urlConnection.setDoOutput(true);
                            urlConnection.setDoInput(true);
                            urlConnection.setRequestMethod(HttpMethod.POST.toString());
                            urlConnection.setUseCaches(false);
                            urlConnection.setConnectTimeout(3000);
                            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            urlConnection.setRequestProperty("Content-Length", String.valueOf(paramStr.length()));
                            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(),Config.CHARSET));
                            bufferedWriter.write(paramStr.toString());
                            bufferedWriter.flush();
                            break;
                        case GET:
                            urlConnection=(HttpURLConnection) new URL(url+"?"+paramStr.toString()).openConnection();
                            break;
                        default:
                            break;
                    }
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),Config.CHARSET));

                    String line = null;
                    StringBuffer result = new StringBuffer();
                    while((line=bufferedReader.readLine())!=null){
                        result.append(line);
                    }
                    System.out.println("1.Result:"+result);//yunxing
                    String str=result.toString();
                    System.out.println("2.Result:"+result.toString());//yunxing
                    return str;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute( String result) {

                if (result!=null) {
                    System.out.println("3.Result:"+result);//yunxing
                    if (successCallback!=null) {
                        successCallback.onSuccess(result);
                    }
                }else{
                    if (failCallback!=null) {
                        failCallback.onFail();
                    }
                }
                super.onPostExecute(result);
            }
        }.execute();
    }
    public static interface  SuccessCallback{
        void onSuccess(String result);//result 暴露给调用者
    }
    public  static interface  FailCallback{
        void onFail();
    }

}
