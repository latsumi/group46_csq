package com.java.group46_csq.util;

/**
 * Created by zhy on 17-9-9.
 */

/*
 * Java Xiaoxueqi - project
 * Class NewsList Source File
 * Created by Haoyu Zhao on Sep 9, 2017
 */

import android.util.Log;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.TreeSet;

public class NewsList {
    private final static int pageSize = 20;
    private final static String queryrul = "http://166.111.68.66:2042/news/action/query/";

    private ArrayList<News> news_list;

    private int pageNo;
    private int category;
    private String keyword;

    private boolean isLoadFromLocal = false;

    private TreeSet<String> localNewsID;

    public NewsList() {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = 0;
        this.keyword = null;
        localNewsID = new TreeSet<String>();
    }

    public NewsList(int category) {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = category;
        this.keyword = null;
        localNewsID = new TreeSet<String>();
    }

    public NewsList(String keyword) {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = 0;
        this.keyword = keyword;
        localNewsID = new TreeSet<String>();
    }

    public NewsList(String keyword, int category) {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = category;
        this.keyword = keyword;
        localNewsID = new TreeSet<String>();
    }

    public void addNews(News e) {
        this.news_list.add(e);
    }

    public News getNews(int i) {
        return this.news_list.get(i);
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(int code) {
        this.category = code;
    }

    public void loadIDFromLocal(FileInputStream fis) {
        isLoadFromLocal = true;
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (true) {
                News news1 = new News();
                Log.d("--point--", "1");
                news1 = (News) ois.readObject();
                Log.d("--point--", "2");
                byte[] buf = new byte[4];
                fis.read(buf);
                Log.d("--point--", "3");
                this.localNewsID.add(news1.getNewsID());
                Log.d("---Counts---", "Counts the number of news");
            }
        } catch (Exception e) {
            Log.d("----Exception-----", "exception in function loadIDFromLocal");
        }
    }

    public void loadFromLocal(FileInputStream fis) {
        isLoadFromLocal = true;
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (true) {
                News news1 = new News();
                Log.d("--point--", "1");
                news1 = (News) ois.readObject();
                Log.d("--point--", "2");
                byte[] buf = new byte[4];
                fis.read(buf);
                Log.d("--point--", "3");
                this.news_list.add(news1);
                Log.d("---Counts---", "Counts the number of news");
            }
        } catch (Exception e) {
            Log.d("----Exception-----", "exception in function loadFromLocal");
        }
    }
    public void refresh() {
        if (isLoadFromLocal) {
            return;
        }
        this.pageNo += 1;

        //set the parameters of the query url
        String param_url = "";
        //whether has the keywords
        if (this.keyword != null && (!this.keyword.equals(""))) {
            try {
                param_url = param_url + "search?" + "keyword=" + URLEncoder.encode(this.keyword, "utf-8") + "&";
            }
            catch (UnsupportedEncodingException e) {
                Log.d("--Encoding Exception--", "Exception happens when encoding the chinese into utf-8");
            }
        }
        else {
            param_url = param_url + "latest?";
        }

        //always have the pageNo and the pageSize parameters
        //the pageSize parameters is set as an constant in the class beginning
        param_url = param_url + "pageNo=" + this.pageNo + "&";
        param_url = param_url + "pageSize=" + NewsList.pageSize;

        if (this.category != 0) {
            param_url = param_url + "&category=" + this.category;
        }

        String url = NewsList.queryrul + param_url;
        Log.d("----See the url----", url);
        String raw_json_list = GetNetRes.getUrlRes(url);
        Log.d("--See the raw json--", raw_json_list);

        //this is some test code
        /*
        System.out.println("some test below");
        System.out.println(url);
        System.out.println("");
        System.out.println(raw_json_list);
        System.out.println("end some test");
        */

        ArrayList<String> news_list_json = ParseJson.parseNewsList(raw_json_list);
        for (int i = 0; i < news_list_json.size(); i++) {
            News news = new News();
            String news_json = news_list_json.get(i);
            news.setNewsID(ParseJson.parseNewsID(news_json));
            news.setNewsTitle(ParseJson.parseNewsTitle(news_json));
            news.setNewsIntro(ParseJson.parseNewsIntro(news_json));
            news.setNewsClassTag(ParseJson.parseNewsClassTag(news_json));
            news.setNewsCategory(strCatToInt(news.getNewsClassTag()));
            Log.d("----See the Category-", " " + news.getNewsCategory());

            this.news_list.add(news);
        }
    }

    public ArrayList<News> getNewsList() {
        return this.news_list;
    }

    public int length() {
        return this.news_list.size();
    }

    public static int strCatToInt(String str) {
        if (str.equals("科技")){
            return 1;
        }
        else if (str.equals("教育")) {
            return 2;
        }
        else if (str.equals("军事")) {
            return 3;
        }
        else if (str.equals("国内")) {
            return 4;
        }
        else if (str.equals("社会")) {
            return 5;
        }
        else if (str.equals("文化")) {
            return 6;
        }
        else if (str.equals("汽车")) {
            return 7;
        }
        else if (str.equals("国际")) {
            return 8;
        }
        else if (str.equals("体育")) {
            return 9;
        }
        else if (str.equals("财经")) {
            return 10;
        }
        else if (str.equals("健康")) {
            return 11;
        }
        else if (str.equals("娱乐")) {
            return 12;
        }
        else {
            return 0;
        }
    }


    public static void main(String[] args) {
        NewsList nl = new NewsList();
        nl.setCategory(1);
        nl.refresh();
        ArrayList<News> newsl = nl.getNewsList();

        int list_size = newsl.size();
        System.out.println("begin test\r\n");

        for (int i = 0; i < list_size; i++) {
            News e = newsl.get(i);
            System.out.println(e.getNewsTitle());
            System.out.println(e.getNewsID());
            System.out.println(e.getNewsIntro());
            System.out.println("\r\n");
        }
        System.out.println("end test\r\n\r\n");

        nl.refresh();
        newsl = nl.getNewsList();
        list_size = newsl.size();
        System.out.println("begin test\r\n");

        for (int i = 0; i < list_size; i++) {
            News e = newsl.get(i);
            System.out.println(e.getNewsTitle());
            System.out.println(e.getNewsID());
            System.out.println(e.getNewsIntro());
            System.out.println("\r\n");
        }
        System.out.println("end test\r\n\r\n");

        nl.refresh();
        newsl = nl.getNewsList();
        list_size = newsl.size();
        System.out.println("begin test\r\n");

        for (int i = 0; i < list_size; i++) {
            News e = newsl.get(i);
            System.out.println(e.getNewsTitle());
            System.out.println(e.getNewsID());
            System.out.println(e.getNewsIntro());
            System.out.println("\r\n");
        }
        System.out.println("end test\r\n\r\n");
    }
}