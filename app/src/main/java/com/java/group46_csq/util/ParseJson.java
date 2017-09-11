package com.java.group46_csq.util;

/**
 * Created by zhy on 17-9-9.
 */

/*
 * Java Xiaoxueqi - project
 * Class ParseJson Source Code
 * Created by Haoyu Zhao on Sep 8, 2017
 */

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class ParseJson {
    //this class is a simple collection for parsing the json file
    //given by the news server


    public static ArrayList<String> parseNewsList(String raw_json) {
        //this function used to get the news list from the
        //lsit json file get from the news server

        ArrayList<String> news_arr_list = new ArrayList<String>();
        //match the pattern {...}, in each {} is a piece of news
        Pattern p = Pattern.compile("\\{[^\\{]*?\\}");
        Matcher m = p.matcher(raw_json);
        int idx = 0;
        while (m.find(idx)) {
            String str = m.group();
            idx = m.start() + 1;
            news_arr_list.add(str);
        }
        return news_arr_list;
    }

    //a general method that can find the value of the given key
    private static String parseTarget(String news_json, String target) {
        String res = "";
        //match the pattern " ":" "
        String regex = "\"" + target + "\".*?\".*?\"";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(news_json);
        if (m.find()) {
            res = m.group();
        }

        String[] after_parse = res.split("\"");
        int size = after_parse.length;
        if (size == 0) {
            return "";
        }
        return after_parse[size - 1];
    }

    //a small set of abstraction of the parsing process
    public static String parseNewsTitle(String news_json) {
        return parseTarget(news_json, "news_Title");
    }

    public static String parseNewsClassTag(String news_json) {
        return parseTarget(news_json, "newsClassTag");
    }

    public static String parseNewsID(String news_json) {
        return parseTarget(news_json, "news_ID");
    }

    public static String parseNewsIntro(String news_json) {
        return parseTarget(news_json, "news_Intro");
    }

    public static String parseContent(String news_json) {
        return parseTarget(news_json, "news_Content");
    }

    public static String parseNewsPictures(String news_json) {
        return parseTarget(news_json, "news_Pictures");
    }


    //class test function
    public static void main(String[] args) {
        //News test = new News("201608090432c815a85453c34d8ca43a591258701e9b");
        String str = GetNetRes.getUrlRes("http://166.111.68.66:2042/news/action/query/search?keyword=%E6%9D%AD%E5%B7%9E&pageNo=1&pageSize=10&category=1");
        ArrayList<String> news_list = parseNewsList(str);
        String news_json = news_list.get(0);
        System.out.println(news_json);
        System.out.println(" ");
        String news_title = parseNewsTitle(news_json);
        System.out.println(news_title);

    }
}
