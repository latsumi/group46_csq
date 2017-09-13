package com.java.group46_csq.util;

/**
 * Created by zhy on 17-9-9.
 */

/*
 * Java Xiaoxueqi - project
 * GetNetRes Source File
 * Created by Haoyu Zhao on Sep 8, 2017
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetNetRes {
    //this is a simple method which aims to get the html/json from the given url
    public static String getUrlRes(String urlstr) {
        String res = "";
        try {
            URL u = new URL(urlstr);
            URLConnection uc = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                res = res + inputLine;
            }
        }
        catch (Exception e) {
            System.out.println("Exception happens");
        }
        return res;
    }

    public static Bitmap getUrlImg(String urlstr) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(urlstr);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream();
            // 将InputStream转换成Bitmap
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            Log.d("-----Exception-----","[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("-----Exception-----","[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return bitmap;
    }
}
