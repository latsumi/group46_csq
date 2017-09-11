package com.java.group46_csq;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ListActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
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

public class MainActivity extends Activity {
    NewsList listItems;
    private PullToRefreshListView mPullRefreshListView;
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        listItems = new NewsList();
        mAdapter = new NewsAdapter(this,android.R.layout.simple_list_item_2,listItems);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

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

        // Add an end-of-list listener
        mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(MainActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
            }
        });

        ListView actualListView = mPullRefreshListView.getRefreshableView();

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

    //这个地方需要修改
    //@Override
    protected void onListItemClick(ListView l, View v,int position, long id){
     //   super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, ProfileActivity.class);
        String news_ID = listItems.getNewsList().get(position-1).getNewsID();
        Log.d("----tag-put-string----", news_ID);
        intent.putExtra("news_ID", news_ID);
        startActivity(intent);
    }

}
