package com.java.group46_csq.util;

/**
 * Created by zhy on 17-9-9.
 */

/*
 * Java Xiaoxueqi - project
 * GetNetRes Source File
 * Created by Haoyu Zhao on Sep 8, 2017
 */

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
}
