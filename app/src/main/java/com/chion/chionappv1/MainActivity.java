package com.chion.chionappv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.chion.chionappv1.chionDIY;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public static String local_log = "CHIONTAG";
    public static String local_key;
    public static final String FILE_NAME = "chion_data";
    public static SharedPreferences sp;
    public SharedPreferences.Editor editor;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.sample_text);

//        sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        //想再sp里面写东西
//        editor = sp.edit();

        Button btn1 = (Button) findViewById(R.id.c_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Example of a call to a native method
                try {
                    String req_token_str = new chionDIY().get_key();
//                    Log.i(local_log,"key:"+req_token_str);
//                    String encode_str = new chionDIY().encode(req_token_str,"123");
//                    Log.i(local_log,""+encode_str);
//                    editor.putString("key", (String)req_token_str);
//                    editor.commit();
                    post();

                } catch (Exception e) {
                    Log.i(local_log,""+e.getMessage());
                    e.printStackTrace();
                }

            }
        });

    }
    public static String sort(TreeMap<String, String> map) {
        if (map == null || map.size() < 1) {
            return "";
        }
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            String key = iter.next();
            sb.append(key);
            sb.append("=");
            sb.append(map.get(key));
        }
        return sb.toString();
    }

    public void post(){
        // 第一步创建okhttpclinent
        OkHttpClient client = new OkHttpClient.Builder().build();
        // 第二步创建RequestBody
        TreeMap<String, String> m = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 降序排序
                        return obj2.compareTo(obj1);
                    }
                });
        long timecurrentTimeMillis = System.currentTimeMillis();
        m.put("id",local_key);
        m.put("t",String.valueOf(timecurrentTimeMillis));
        m.put("data","花生瓜子啤酒饮料有需要的吗？");
        String ss = sort(m);
        StringBuffer buf = new StringBuffer(ss);
        buf = buf.reverse();
        Log.i("CHIONTAG",""+buf.toString());
        String chion_sign = new chionDIY().encode(local_key,buf.toString());
        m.put("chion-sign",chion_sign);
        JSONObject jsonObject2 = new JSONObject(m);
        String jsonStr = jsonObject2.toString();

        RequestBody reqBodyJson = RequestBody.create(MediaType.parse(
                "application/json;charset=utf-8"),jsonStr);
        // 第三部创建Request
        Request request = new Request.Builder().url("http://test.chion.xyz/submit").addHeader(
                "contentType", "application/json;charset=utf-8").post(reqBodyJson).build();

        //第四步 创建call回调对象
        final Call call = client.newCall(request);
        //第五步 发起请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("onFilure", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(res);
                    }
                });

            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}
