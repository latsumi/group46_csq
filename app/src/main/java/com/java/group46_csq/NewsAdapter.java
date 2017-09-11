package com.java.group46_csq;

/**
 * Created by zhy on 17-9-10.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.group46_csq.util.NewsList;


/**
 * Created by csq on 17/9/9.
 */
public class NewsAdapter extends BaseAdapter {
    private final Context context;
    private final int layout;
    private NewsList listItems;

    public NewsAdapter(Context context, int layout, NewsList listItems) {
        this.context = context;
        this.layout = layout;
        this.listItems = listItems;

    }

    @Override
    public int getCount(){
        return listItems.length();
    }

    @Override
    public Object getItem(int i) {
        return listItems.getNewsList().get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,parent,false);

        }
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text1.setText(listItems.getNewsList().get(position).getNewsTitle());
        text2.setText(listItems.getNewsList().get(position).getNewsIntro());
        return view;

    }
}
