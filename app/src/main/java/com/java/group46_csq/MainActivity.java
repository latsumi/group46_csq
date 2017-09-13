package com.java.group46_csq;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;


import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.widget.Toast;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.java.group46_csq.util.News;
import com.java.group46_csq.util.NewsList;
import com.java.group46_csq.fileIO.FileService;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.TreeSet;

public class MainActivity extends Activity{
    final static String[] mapping = {"科技","教育","军事","国内","社会","文化","汽车","国际","体育","财经","健康","娱乐"};
    NewsList listItems;
    private PullToRefreshListView mPullRefreshListView;
    private NewsAdapter mAdapter;
    private ListView actualListView;

    private ArrayAdapter<String> adapter;

    private ListView mLeftDrawer;

    String categoryStr;
    int[] categoryArr;
    String[] menu_array;

    private String keyword;
    private int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        //given the parameters to set up the list
        //Intent i = getIntent();
        //keyword = i.getStringExtra("keyword");
        //category = Integer.parseInt(i.getStringExtra("category"));
        //FileService.writeFile(MainActivity.this, "categorySet.txt","111111110000");
        listItems = new NewsList(keyword, category);
        mAdapter = new NewsAdapter(this,android.R.layout.simple_list_item_2,listItems);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        /*
            edit by csq
            left drawer
         */
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);

        new ReadCategory().execute();

        mLeftDrawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent=
                                new Intent(MainActivity.this,CategorySetActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new LoadDataFromLocal().execute("localnews");
                        break;
                    case 2:
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new LoadLikeDataFromLocal().execute("likes");
                        break;
                    case 3:
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new GetMoreData().execute();
                        break;
                    case 4:
                        listItems = new NewsList();
                        listItems.setCategory(1);
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new GetMoreData().execute();
                        break;
                    default:
                        listItems = new NewsList();
                        listItems.setCategory(getCategoryNumber(position));
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new GetMoreData().execute();
                        break;
                }


            }
        });
        //end edit by csq , left drawer


        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetMoreData().execute();
            }
        });

        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //need to be modified
                //need to add a if-else clause
                //important
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                String news_ID = listItems.getNewsList().get(position-1).getNewsID();

                //add the statement that the news has benn read
                listItems.getNewsList().get(position-1).isRead = true;


                Log.d("----tag-put-string----", news_ID);
                intent.putExtra("news_ID", news_ID);
                startActivity(intent);


                /*
                Intent test = new Intent(MainActivity.this, MainActivity.class);
                test.putExtra("keyword", "北京");
                test.putExtra("category", "0");
                startActivity(test);
                */
            }
        });

        // Add an end-of-list listener
        mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(MainActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
            }
        });

        actualListView = mPullRefreshListView.getRefreshableView();

        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        actualListView.setAdapter(mAdapter);

        new GetMoreData().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        //取得ActionBar
        ActionBar actionBar = getActionBar();
        //设置不显示标题
        actionBar.setDisplayShowTitleEnabled(false);
        //设置使用logo
        actionBar.setDisplayUseLogoEnabled(true);
        //设置logo
        actionBar.setLogo(R.drawable.news);
        //设置ActionBar背景
        Drawable background = getResources().getDrawable(R.drawable.top_bar_background);
        actionBar.setBackgroundDrawable(background);
        //设置是将应用程序图标转变成可点击图标,并添加返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();

        // Call onRefreshComplete when the list has been refreshed.
        mPullRefreshListView.onRefreshComplete();

        //refresh the menu list
        new ReadCategory().execute();

    }

    private class GetMoreData extends AsyncTask<Void, Void, NewsList> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected NewsList doInBackground(Void... params) {
            listItems.refresh();
            return listItems;
        }

        @Override
        protected void onPostExecute(NewsList listItems) {
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();
        }
    }

    private class LoadDataFromLocal extends AsyncTask<String, Void, String> {
        FileInputStream fis;
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            ObjectInputStream ois = null;
            try {
                fis = openFileInput(params[0]);
            }
            catch (Exception e) {
                Log.d("---Exception---", "Exception happends in LoadDataFromLocal AsyncTast");
            }
            try {
                listItems.loadFromLocal(fis);
                fis.close();
            }
            catch (Exception e) {}
            finally {
                Log.d("--Counts--", "number of news loaded: " + listItems.length());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String str) {
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();
        }
    }

    private class LoadLikeDataFromLocal extends AsyncTask<String, Void, String> {
        FileInputStream fis;
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            ObjectInputStream ois = null;
            try {
                fis = openFileInput(params[0]);
                ois = new ObjectInputStream(fis);
            }
            catch (Exception e) {
                Log.d("---Exception---", "Exception happends in LoadLikeDataFromLocal AsyncTast");
            }
            try {
                Log.d("----tag----","enter try clause");
                TreeSet<News> like = new TreeSet<News>();
                like = (TreeSet<News>)ois.readObject();
                Iterator<News> iter = like.iterator();
                while (iter.hasNext()) {
                    Log.d("---count---","----counting----");
                    listItems.getNewsList().add(iter.next());
                }
                ois.close();
                fis.close();
            }
            catch (Exception e) {
                Log.d("---Exception---", "Exception happends in LoadDataFromLocal ReadObjects");
            }
            finally {
                Log.d("--Counts--", "number of news loaded: " + listItems.getNewsList().size());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String str) {
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();
        }
    }

    private class ReadCategory extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... params) {
            String res = "";
            try {
                res = FileService.readFile(MainActivity.this, "categorySet.txt");
            }
            catch (IOException e) {
                res = "111111110000";
                FileService.writeFile(MainActivity.this, "categorySet.txt", res);
            }
            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            categoryStr = res;
            initMenuCategory();
            adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,menu_array);
            mLeftDrawer.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //mLeftDrawer.NotifyDataSetChanged();
        }
    }

    private void initMenuCategory() {
        categoryArr = new int[12];
        for (int i = 0; i < categoryStr.length(); i++) {
            categoryArr[i] = Integer.parseInt(categoryStr.substring(i,i+1));
        }
        int k = 0;
        for (int i = 0; i < 12; i++) {
            k += categoryArr[i];
        }
        menu_array = new String[5 + k];
        menu_array[0] = "新闻分类";
        menu_array[1] = "本地新闻";
        menu_array[2] = "收藏";
        menu_array[3] = "最新";
        menu_array[4] = "推荐";
        int count = 4;
        int temp = 0;
        while (temp < 12) {
            if (categoryArr[temp] == 1) {
                count++;
                menu_array[count] = mapping[temp];
            }
            temp++;
        }
    }

    private int getCategoryNumber(int i) {
        int t = i - 4;
        int temp = 0;
        for (int j = 0; j < 12; j++) {
            temp += categoryArr[j];
            if (temp == t) {
                return j+1;
            }
        }
        return 0;
    }



}
