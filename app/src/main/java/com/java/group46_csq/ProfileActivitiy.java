package com.java.group46_csq;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.java.group46_csq.util.News;

/**
 * Created by csq on 17/9/9.
 */

public class ProfileActivitiy extends Activity{
    private TextView title;
    private TextView author;
    private TextView textsrc;
    private TextView maintext;

    private News n;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relative_example);

        Intent i = getIntent();
        String news_ID = i.getStringExtra("news_ID");

        Log.d("----------tag--------", news_ID);

        n = new News(news_ID);

        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        textsrc = (TextView) findViewById(R.id.textsrc);
        maintext = (TextView) findViewById(R.id.maintext);

        View parent = (View) title.getParent();
        parent.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        title.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);


        new GetNews().execute();
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
            return n.getNewsContent();
        }

        @Override
        protected void onPostExecute(String res) {
            title.setText(n.getNewsTitle());
            maintext.setText(n.getNewsContent());

        }
    }

}
