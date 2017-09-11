package com.java.group46_csq.util;

/**
 * Created by zhy on 17-9-9.
 */

/*
 * Java Xiaoxueqi - project
 * Class NewsList Source File
 * Created by Haoyu Zhao on Sep 9, 2017
 */

import java.util.ArrayList;

public class NewsList {
    private final static int pageSize = 20;
    private final static String queryrul = "http://166.111.68.66:2042/news/action/query/latest?";

    private ArrayList<News> news_list;

    private int pageNo;
    private int category;
    private String keyword;

    public NewsList() {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = 0;
        this.keyword = null;
    }

    public NewsList(int category) {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = category;
        this.keyword = null;
    }

    public NewsList(String keyword) {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = 0;
        this.keyword = keyword;
    }

    public NewsList(String keyword, int category) {
        this.news_list = new ArrayList<News>();
        this.pageNo = 0;
        this.category = category;
        this.keyword = keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(int code) {
        this.category = code;
    }

    public void refresh() {
        this.pageNo += 1;

        //set the parameters of the query url
        String param_url = "";
        //whether has the keywords
        if (this.keyword != null) {
            param_url = param_url + "keyword=" + this.keyword + "&";
        }

        //always have the pageNo and the pageSize parameters
        //the pageSize parameters is set as an constant in the class beginning
        param_url = param_url + "pageNo=" + this.pageNo + "&";
        param_url = param_url + "pageSize=" + NewsList.pageSize;

        if (this.category != 0) {
            param_url = param_url + "&category=" + this.category;
        }

        String url = NewsList.queryrul + param_url;
        String raw_json_list = GetNetRes.getUrlRes(url);

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

            this.news_list.add(news);
        }
    }

    public ArrayList<News> getNewsList() {
        return this.news_list;
    }

    public int length() {
        return this.news_list.size();
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