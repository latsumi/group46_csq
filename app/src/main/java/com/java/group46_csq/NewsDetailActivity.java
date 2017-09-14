package com.java.group46_csq;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.TreeSet;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Button;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.java.group46_csq.fileIO.FileService;
import com.java.group46_csq.util.GetNetRes;
import com.java.group46_csq.util.News;
import com.java.group46_csq.util.NewsList;

/**
 * Created by csq on 17/9/9.
 */

public class NewsDetailActivity extends Activity{
    private TextView title;
    private TextView maintext;
    private ImageView news_image;
    private ScrollView scrollView;
    private boolean isNightMode;
    private TextToSpeech mTextToSpeech = null;

    private News n;

    private boolean hasBeenRead = true;

    private TreeSet<News> likes;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);

        Intent i = getIntent();
        String news_ID = i.getStringExtra("news_ID");
        isNightMode = i.getBooleanExtra("isNightMode",false);
        //Log.d("----------tag--------", news_ID);

        n = new News(news_ID);
        /*
        try {
            Log.d("----tag----", context.getFilesDir().toString());
            if((FileService.findIfSaved("savedNews", n))==null)
            FileService.saveNews("savedNews", n);

        } catch (Exception e) {
            Log.d("----Exception----", e.getStackTrace().toString());
        }
        */

        title = (TextView) findViewById(R.id.title);
        maintext = (TextView) findViewById(R.id.maintext);
        news_image = (ImageView) findViewById(R.id.news_image);
        scrollView = (ScrollView) findViewById(R.id.scrollview);

        View parent = (View) title.getParent();
        if(isNightMode)
        {
            title.setTextColor(getResources().getColor(R.color.text_color_night));
            maintext.setTextColor(getResources().getColor(R.color.text_color_night));
            parent.setBackgroundColor(Color.parseColor("#191919"));
            news_image.setImageResource(R.drawable.loading_night);
        }
        else
        {
            title.setTextColor(getResources().getColor(R.color.text_color));
            maintext.setTextColor(getResources().getColor(R.color.text_color));
            parent.setBackgroundColor(Color.parseColor("#FFFAFA"));
            news_image.setImageResource(R.drawable.loading);
        }
        likes = new TreeSet<News>();


        title.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);


        new GetNews().execute();

        new LoadLikesDataFromLocal().execute("likes");

        mTextToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS) {
                    //设置朗读语言
                    int supported=mTextToSpeech.setLanguage(Locale.US);
                    if ((supported!=TextToSpeech.LANG_AVAILABLE)&&(supported!=TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(NewsDetailActivity.this, "不支持当前语言！", 1).show();
                    }
                }

            }
        });


        news_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetImage().execute();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_newsdetail, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_news1);
        Drawable background = getResources().getDrawable(R.drawable.top_bar_background);
        actionBar.setBackgroundDrawable(background);
        actionBar.setDisplayHomeAsUpEnabled(true);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider provider = (ShareActionProvider) shareItem.getActionProvider();
        provider.setShareIntent(getDefaultIntent());






        return super.onCreateOptionsMenu(menu);
    }
    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");  //修改此处来决定分享类型
        return intent;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                /*Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                finish();
                return true;
            case R.id.action_read:
                mTextToSpeech.speak(maintext.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                return true;
            case R.id.action_favo:
                new SaveLike().execute("likes");
                return true;
            case R.id.action_unfavo:
                new DelLike().execute("likes");
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
    protected void onDestroy() {
        super.onDestroy();

        if (mTextToSpeech!=null) {
            mTextToSpeech.shutdown();//关闭TTS
        }
    }

    /*
     Edit by Haoyu Zhao on Sep 11, 2017
     */
    private class GetNews extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... params) {
            //n.getNewsDetail();
            News res = new News();
            String filename = "localnews";

            try {
                Log.d("----Try---", "---enter try---");

                FileInputStream fis = null;
                try {
                    fis = openFileInput(filename);
                    res = FileService.findIfSaved(fis, n);
                }
                catch (Exception e) {
                    Log.d("--Whether Use Network--", "Exception in catch clause, Use Network");
                    n.getNewsDetail();
                    res = new News();
                }
                Log.d("----Try---", "----before findIfSaved");

                Log.d("----Try---", "----after findIfSaved");
                if (res.getNewsID().equals("")) {
                    hasBeenRead = false;
                    Log.d("--Whether Use Network--", "Use Network");
                    n.getNewsDetail();
                }
                else {
                    n = res;
                }
            } catch (Exception e) {
                Log.d("---Exception---", "Exception in GetNews");
            }

            FileOutputStream fos = null;
            if (!n.getNewsContent().equals("") && !hasBeenRead) {
                try {
                    fos = openFileOutput(filename, Context.MODE_APPEND);
                    n.isRead = true;
                    FileService.saveNews(fos, n);
                    fos.close();
                } catch (Exception e) {
                    Log.d("----Exception----", "exception while open the target file");
                }
            }


            return n.getNewsContent();

        }

        @Override
        protected void onPostExecute(String res) {
            title.setText(n.getNewsTitle());
            maintext.setText(n.getNewsContent());
            if(isNightMode)
            {
                title.setTextColor(getResources().getColor(R.color.text_color_night));
                maintext.setTextColor(getResources().getColor(R.color.text_color_night));
            }
            else
            {
                title.setTextColor(getResources().getColor(R.color.text_color));
                maintext.setTextColor(getResources().getColor(R.color.text_color));
            }
            //news_image.setImageResource(R.mipmap.loading);

        }
    }

    private class GetImage extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String[] arr = n.getNewsPictures();
            int len = arr.length;
            if (len == 0) {
                return null;
            }
            else if (arr[0].equals("") && len == 1) {
                return null;
            }
            else if (!arr[0].equals("") && !(arr[0]==null)) {
                return GetNetRes.getUrlImg(arr[0]);
            }
            else if (!arr[1].equals("") && !(arr[1]==null)) {
                return GetNetRes.getUrlImg(arr[1]);
            }
            else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap res) {
            if (res == null) {
                news_image.setImageResource(R.drawable.news_not_found);
            }
            else {
                news_image.setImageBitmap(res);
            }
        }

    }

    private class LoadLikesDataFromLocal extends AsyncTask<String, Void, String> {
        FileInputStream fis;
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            ObjectInputStream ois = null;
            try {
                fis = openFileInput(params[0]);
                ois = new ObjectInputStream(fis);
            }
            catch (Exception e) {
                Log.d("---Exception---", "Exception happends in LoadLikesDataFromLocal AsyncTast");
                return "exp";
            }
            try {
                likes = (TreeSet<News>)ois.readObject();
                ois.close();
                fis.close();
            }
            catch (Exception e) {
                return "exp";
            }
            finally {
                Log.d("--Counts--", "number of news loaded: " + likes.size());
            }
            return "suc";
        }

        @Override
        protected void onPostExecute(String str) {
            if (str.equals("exp")) {
                likes = new TreeSet<News>();
            }
        }
    }

    private class SaveLike extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String...params) {
            if (likes.add(n)) {
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(params[0], Context.MODE_PRIVATE);
                    n.isRead = true;
                    FileService.saveNewsSet(fos, likes);
                    fos.close();
                } catch (Exception e) {
                    Log.d("----Exception----", "exception while open the target file");
                }
                return "add";
            }
            return "notadd";
        }

        @Override
        protected void onPostExecute(String res) {
            if (res.equals("add")) {
                Toast.makeText(NewsDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(NewsDetailActivity.this, "已经收藏", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DelLike extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String...params) {
            if (likes.remove(n)) {
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(params[0], Context.MODE_PRIVATE);
                    FileService.saveNewsSet(fos, likes);
                    fos.close();
                } catch (Exception e) {
                    Log.d("----Exception----", "exception while open the target file");
                }
                return "del";
            }
            return "notdel";
        }

        @Override
        protected void onPostExecute(String res) {
            if (res.equals("del")) {
                Toast.makeText(NewsDetailActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(NewsDetailActivity.this, "没有收藏", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
