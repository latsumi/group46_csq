package com.java.group46_csq.util;

/**
 * Created by zhy on 17-9-9.
 */
import java.io.Serializable;

/*
 * Java Xiaoxueqi - Project
 * Class News Source Codes
 * Created by Haoyu Zhao on Sep 5, 2017
 */

public class News implements Serializable {
    private final static String queryurl = "http://166.111.68.66:2042/news/action/query/detail?newsId=";

    private String news_ID;
    private String news_Title;
    private String news_Intro;
    private String newsClassTag;
    private String news_Content;
    private String[] news_Pictures;
    private int temp;

    public News() {
        this.news_ID = "";
        this.news_Title = "";
        this.newsClassTag = "";
        this.news_Intro = "";
        this.news_Content = "";
        this.news_Pictures = new String[0];
        this.temp = 0;
    }

    public News(String ID) {
        this.news_ID = ID;
        this.news_Title = "";
        this.newsClassTag = "";
        this.news_Intro = "";
        this.news_Content = "";
        this.news_Pictures = new String[0];
    }

    public void setNewsID(String ID) {
        this.news_ID = ID;
    }

    public String getNewsID() {
        return this.news_ID;
    }

    public void setNewsTitle(String title) {
        this.news_Title = title;
    }

    public String getNewsTitle() {
        return this.news_Title;
    }

    public void setNewsClassTag(String newsClassTag) {
        this.newsClassTag = newsClassTag;
    }

    public String getNewsClassTag() {
        return this.newsClassTag;
    }

    public void setNewsIntro(String news_Intro) {
        this.news_Intro = news_Intro;
    }

    public String getNewsIntro() {
        return this.news_Intro;
    }

    public String getNewsContent() {
        String res = "";
        /*
        String[] temp = this.news_Content.split("\\s+");
        for (int i = 0; i < temp.length; i++) {
            res += temp[i];
            res += "\r\n";
        }
        */

        return this.news_Content;
    }

    public String[] getNewsPictures() {
        return this.news_Pictures;
    }

    public void getNewsDetail() {
        String urlstr = News.queryurl + this.news_ID;
        String json_detail = GetNetRes.getUrlRes(urlstr);
        this.news_Content = ParseJson.parseContent(json_detail);
        this.newsClassTag = ParseJson.parseNewsClassTag(json_detail);
        this.news_Intro = ParseJson.parseNewsIntro(json_detail);
        this.news_Title = ParseJson.parseNewsTitle(json_detail);
        String pictures = ParseJson.parseNewsPictures(json_detail);
        String temp[] = pictures.split("\\s+");
        this.news_Pictures = temp;
    }

    //edited by csq on 17/9/12
    public boolean equals(News news1, News news2){
        return ((news1==null)||(news2==null))?false:(news1.news_ID.equals(news2.news_ID));
    }


    public static void main(String[] args) {
        News test = new News("201512240713c08eddbabe4947969f0fa348f5fa6b92");
        test.getNewsDetail();
        System.out.println(test.getNewsContent());

        for (int i = 0; i < test.news_Pictures.length; i++) {
            System.out.println(test.news_Pictures[i]);
        }
    }

}