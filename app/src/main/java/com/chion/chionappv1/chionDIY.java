package com.chion.chionappv1;

import android.util.Log;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.chion.chionappv1.MainActivity.*;

public class chionDIY {

    static {
        System.loadLibrary("chion");
    }


    public String get_key(){
        String str = "";
        for (int i = 0; i < 8; i++) {
            char temp = 0;
            int key = (int) (Math.random() * 2);
            switch (key) {
                case 0:
                    temp = (char) (Math.random() * 10 + 48);//产生随机数字
                    break;
                case 1:
                    temp = (char) (Math.random()*6 + 'a');//产生a-f
                    break;
                default:
                    break;
            }
            str = str + temp;
        }
        local_key = str+"12345678";
        return local_key;
    };


    public native String encode(String a,String raw);

}
