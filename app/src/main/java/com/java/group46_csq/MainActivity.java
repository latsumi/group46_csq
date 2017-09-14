package com.java.group46_csq;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.SearchView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.java.group46_csq.util.GetRandCat;
import com.java.group46_csq.util.News;
import com.java.group46_csq.util.NewsList;
import com.java.group46_csq.fileIO.FileService;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.TreeSet;

public class MainActivity extends Activity{
    final static String[] mapping = {"科技","教育","军事","国内","社会","文化","汽车","国际","体育","财经","健康","娱乐"};
    NewsList listItems;
    private PullToRefreshListView mPullRefreshListView;
    private NewsAdapter mAdapter;
    private ListView actualListView;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;
    private boolean isNightMode = false;
    private ListView mLeftDrawer;

    private NewsList[] NewsListArr;


    String categoryStr;
    int[] categoryArr;
    String[] menu_array;

    private int[] readCategory;
    private int[] pointers = {0,0,0,0,0,0,0,0,0,0,0,0};

    private boolean isRecommending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //given the parameters to set up the list
        //Intent i = getIntent();
        //keyword = i.getStringExtra("keyword");
        //category = Integer.parseInt(i.getStringExtra("category"));
        //FileService.writeFile(MainActivity.this, "categorySet.txt","111111110000");
        listItems = new NewsList();
        NewsListArr = new NewsList[12];
        for (int i = 0; i < 12; i++) {
            NewsListArr[i] = new NewsList();
            NewsListArr[i].setCategory(i+1);
        }
        mAdapter = new NewsAdapter(this,android.R.layout.simple_list_item_2,listItems);
        mAdapter.setNightMode(isNightMode);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        /*
            edit by csq
            left drawer
         */
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);

        new ReadCategory().execute();

        new ReadReadData().execute();

        mLeftDrawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        isRecommending = false;
                        Intent intent=
                                new Intent(MainActivity.this,CategorySetActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        isRecommending = false;
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        mAdapter.setNightMode(isNightMode);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new LoadDataFromLocal().execute("localnews");
                        break;
                    case 2:
                        isRecommending = false;
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        mAdapter.setNightMode(isNightMode);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new LoadLikeDataFromLocal().execute("likes");
                        break;
                    case 3:
                        isRecommending = false;
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        mAdapter.setNightMode(isNightMode);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new GetMoreData().execute();
                        break;
                    case 4:
                        isRecommending = true;
                        listItems = new NewsList();
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        mAdapter.setNightMode(isNightMode);
                        actualListView = mPullRefreshListView.getRefreshableView();
                        registerForContextMenu(actualListView);
                        actualListView.setAdapter(mAdapter);
                        new GetMoreRecommend().execute();
                        break;
                    default:
                        listItems = new NewsList();
                        listItems.setCategory(getCategoryNumber(position));
                        mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                        mAdapter.setNightMode(isNightMode);
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
                if (isRecommending) {
                    new GetMoreRecommend().execute();
                }
                else {
                    new GetMoreData().execute();
                }
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
                News e = listItems.getNewsList().get(position-1);
                e.isRead = true;
                if (e.getNewsCategory() != 0) {
                    Log.d("---Read Data---", "Read category: " + e.getNewsCategory());
                    readCategory[e.getNewsCategory()-1]++;
                }

                new WriteReadData().execute();


                Log.d("----tag-put-string----", news_ID);
                intent.putExtra("news_ID", news_ID);
                intent.putExtra("isNightMode", isNightMode);

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

        actualListView.setBackgroundColor(Color.parseColor("#FFFAFA"));
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
        actionBar.setLogo(R.drawable.ic_news1);
        //设置ActionBar背景
        Drawable background = getResources().getDrawable(R.drawable.top_bar_background);
        actionBar.setBackgroundDrawable(background);
        //设置是将应用程序图标转变成可点击图标,并添加返回按钮
        actionBar.setHomeButtonEnabled(true);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        //配置SearchView的属性
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast t = Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                t.show();

                listItems = new NewsList();
                listItems.setKeyword(query);
                mAdapter = new NewsAdapter(MainActivity.this,android.R.layout.simple_list_item_2,listItems);
                mAdapter.setNightMode(isNightMode);
                actualListView = mPullRefreshListView.getRefreshableView();
                registerForContextMenu(actualListView);
                actualListView.setAdapter(mAdapter);
                new GetMoreData().execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.night_mode:
                isNightMode = !isNightMode;
                mAdapter.setNightMode(isNightMode);
                mAdapter.notifyDataSetChanged();
                mPullRefreshListView.onRefreshComplete();
                if(isNightMode)
                    actualListView.setBackgroundColor(Color.parseColor("#191919"));
                else
                    actualListView.setBackgroundColor(Color.parseColor("#FFFAFA"));

                //recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
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

    private class GetMoreRecommend extends AsyncTask<Void, Void, NewsList> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected NewsList doInBackground(Void...params) {
            for (int i = 0; i < 20; i++) {
                int t = GetRandCat.getRandCat(readCategory);
                if (t != 0) {
                    t--;
                    if (pointers[t] > NewsListArr[t].length()-5) {
                        NewsListArr[t].refresh();
                    }
                    listItems.addNews(NewsListArr[t].getNews(pointers[t]));
                    pointers[t]++;
                }
            }
            return listItems;
        }

        @Override
        protected void onPostExecute(NewsList listItems) {
            mAdapter.notifyDataSetChanged();
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

    private class WriteReadData extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void...params) {
            try {
                FileOutputStream fos = openFileOutput("read", Context.MODE_PRIVATE);
                FileService.writeIntFile(fos, readCategory);
                fos.close();
            }
            catch (IOException e) {
                Log.d("---Exception---", "IO Exception at write Read Data");
            }
            return "write";
        }
    }

    private class ReadReadData extends AsyncTask<Void,Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void...params) {
            try {
                FileInputStream fis = openFileInput("read");
                readCategory = FileService.readIntFile(fis);
                for (int i = 0; i < 12; i++) {
                    Log.d("---Read Data---", " " + readCategory[i]);
                }
                fis.close();
            }
            catch (IOException e) {
                Log.d("---Exception---", "IO Exception in ReadReadDate");
                Log.d("---Set the Arr---","...");
                readCategory = new int[12];
                for (int i = 0; i < 12; i++) {
                    readCategory[i] = 5;
                }
            }
            return "Read";
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
