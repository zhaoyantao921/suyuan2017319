package chutang;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class HttpAsyncTask extends AsyncTask<String,String,String> {

    private TextView textView;
    public HttpAsyncTask(TextView textView) {
        super();
        this.textView = textView;
    }

    /**
     * 这里的Integer参数对应AsyncTask中的第一个参数
     * 这里的String返回值对应AsyncTask的第三个参数
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
     * ... 表示以数组的形式接收参数,即可以传递多个参数
     */
    @Override
    protected String doInBackground(String... params) {

        String strResult="NET_ERROR";
        URL url;
        String URL_PATH = params[0];//"http://10.0.1.9:8080/test07.jsp";

        Map<String, String> strMapParams = new HashMap<String, String>();
        strMapParams.put("param1", params[1]);
        strMapParams.put("param2", params[2]);
        byte[] data = getRequestData(strMapParams, "utf-8").toString().getBytes();//获得请求体

        HttpURLConnection conn=null;

        try {
            url = new URL(URL_PATH);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);  // 设置连接超时时间,5秒
            conn.setDoInput(true);         // 打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);        // 打开输出流，以便向服务器提交数据
            conn.setRequestMethod("POST"); // 设置以Post方式提交数据
            conn.setUseCaches(false);      // 使用Post方式不能使用缓存

            // 设置请求体的类型是文本类型
            //conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 设置请求体的长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));

            // 获得输出流，向服务器写入数据,会自动建立HTTP连接
            OutputStream outputStream = conn.getOutputStream();

            outputStream.write(data);

            int response = conn.getResponseCode();   //获得服务器的响应码

            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                strResult= dealResponseResult(inputStream);  //处理服务器的响应结果
            }
            else
            {
                strResult="HTTP_ERROR";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //中断连接
            }
        }

        return strResult;
    }

    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @Override
    protected void onPostExecute(String result) {
        textView.setText("操作完成！" + result);
        super.onPostExecute(result);
    }

    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
        textView.setText("开始执行....");
    }


    public StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
    /**
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     * Author    :   博客园-依旧淡然
     */
    public String dealResponseResult(InputStream inputStream) {
        String resultData;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}
