package com.chion.chionappv1;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;
import okio.Buffer;

import static com.chion.chionappv1.MainActivity.*;

public class chionintercept implements Interceptor {
    private String newHost = "127.0.0.1";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //字符集
        Charset charset = Charset.forName("UTF-8");
        //不是Get和Delete请求时，则请求数据在请求体中
        RequestBody requestBody = request.body();
        //判断类型
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
            /*如果是二进制上传  则不进行加密*/
            if (contentType.type().toLowerCase().equals("multipart")) {
                return chain.proceed(request);
            }
        }
        // 获取请求的数据
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        String requestData = URLDecoder.decode(buffer.readString(charset).trim(), "utf-8");

        //这里调用加密的方法，自行修改
        String sign = "";
        try {
            sign = new chionDIY().encode(local_key,requestData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //构建新的请求体
        RequestBody newRequestBody = RequestBody.create(contentType, sign);
        //构建新的requestBuilder
        Request.Builder newRequestBuilder = request.newBuilder();
        //根据请求方式构建相应的请求
        newRequestBuilder.post(newRequestBody);

        request = newRequestBuilder.build();
        return chain.proceed(request);
    }
}
