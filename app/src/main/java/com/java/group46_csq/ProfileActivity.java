package com.java.group46_csq;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.java.group46_csq.fileIO.FileService;
import com.java.group46_csq.util.News;

/**
 * Created by csq on 17/9/9.
 */

public class ProfileActivity extends Activity{
    private TextView title;
    private TextView textsrc;
    private TextView maintext;

    private Button readButton;

    private TextToSpeech mTextToSpeech = null;

    private News n;
    private FileService fileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relative_example);

        Intent i = getIntent();
        String news_ID = i.getStringExtra("news_ID");

        Log.d("----------tag--------", news_ID);

        n = new News(news_ID);
        try {
            if((fileService.findIfSaved("savedNews.txt", n))==null)
            fileService.saveNews("savedNews.txt", n);

        } catch (Exception e) {
        }

        title = (TextView) findViewById(R.id.title);
        textsrc = (TextView) findViewById(R.id.textsrc);
        maintext = (TextView) findViewById(R.id.maintext);

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
                        Toast.makeText(ProfileActivity.this, "不支持当前语言！", 1).show();
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
            n.getNewsDetail();
            try {
                n = fileService.findIfSaved("savedNews.txt", n);
                title.setText(n.getNewsTitle());
                maintext.setText(n.getNewsContent());
            } catch (Exception e) {
            }
            return n.getNewsContent();

        }

        @Override
        protected void onPostExecute(String res) {
            title.setText(n.getNewsTitle());
            maintext.setText(n.getNewsContent());


        }
    }

}
