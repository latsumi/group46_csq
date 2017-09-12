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
import com.java.group46_csq.util.NewsList;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MainActivity extends Activity{
    NewsList listItems;
    private PullToRefreshListView mPullRefreshListView;
    private NewsAdapter mAdapter;
    private ListView actualListView;

    private ListView mLeftDrawer;


    String[] menu_array = {"新闻分类","本地新闻","夜间模式"," 设 置 "};

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

        listItems = new NewsList(keyword, category);
        mAdapter = new NewsAdapter(this,android.R.layout.simple_list_item_2,listItems);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        /*
            edit by csq
            left drawer
         */
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,menu_array);
        mLeftDrawer.setAdapter(adapter);
        mLeftDrawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent=
                                new Intent(MainActivity.this,TestActivity.class);
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
                        listItems.setCategory(2);
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new GetMoreData().execute();
                        break;
                    case 3:
                        listItems = new NewsList();
                        listItems.setCategory(3);
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

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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
    }

    private class GetNewsList extends AsyncTask<String, Void, NewsList> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected NewsList doInBackground(String... params) {
            listItems.refresh();

            return listItems;
        }

        @Override
        protected void onPostExecute(NewsList listItems) {

        }
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

}
