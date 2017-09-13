package com.java.group46_csq;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.java.group46_csq.fileIO.FileService;
import com.java.group46_csq.util.GetNetRes;
import com.java.group46_csq.util.News;

/**
 * Created by csq on 17/9/9.
 */

public class NewsDetailActivity extends Activity{
    private TextView title;
    private TextView textsrc;
    private TextView maintext;
    private ImageView news_image;

    private Button readButton;

    private TextToSpeech mTextToSpeech = null;

    private News n;

    private boolean hasBeenRead = true;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);

        Intent i = getIntent();
        String news_ID = i.getStringExtra("news_ID");

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
        textsrc = (TextView) findViewById(R.id.textsrc);
        maintext = (TextView) findViewById(R.id.maintext);

        news_image = (ImageView) findViewById(R.id.news_image);

        readButton = (Button) findViewById(R.id.readButton);

        View parent = (View) title.getParent();
        parent.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        title.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);


        new GetNews().execute();

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

        readButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //朗读EditText里的内容
                mTextToSpeech.speak(maintext.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
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
                news_image.setImageResource(R.drawable.circlel_header);
            }
            else {
                news_image.setImageBitmap(res);
            }
        }

    }

}
