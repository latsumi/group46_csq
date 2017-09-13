package com.java.group46_csq;

/**
 * Created by csq on 17/9/12.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.java.group46_csq.fileIO.FileService;

import java.io.IOException;


public class CategorySetActivity extends Activity{

    private CheckBox category1;
    private CheckBox category2;
    private CheckBox category3;
    private CheckBox category4;
    private CheckBox category5;
    private CheckBox category6;
    private CheckBox category7;
    private CheckBox category8;
    private CheckBox category9;
    private CheckBox category10;
    private CheckBox category11;
    private CheckBox category12;
    private Button button_confirm;
    private Button button_back;
    String checkState;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newscateg);
        findView();
        try {
            checkState = FileService.readFile(CategorySetActivity.this, "categorySet.txt");
        }catch (Exception e) {
            e.printStackTrace();
        }
        setAllCheckBox();

        button_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SaveCheckState();
                Toast tst = Toast.makeText(CategorySetActivity.this, "成功保存", Toast.LENGTH_SHORT);
                tst.show();
            }
        });
       button_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Toast tst = Toast.makeText(CategorySetActivity.this, "成功返回", Toast.LENGTH_LONG);
                tst.show();
            }
        });
    }

    private void findView()
    {
        category1 = (CheckBox) findViewById(R.id.checkBox1);
        category2 = (CheckBox) findViewById(R.id.checkBox2);
        category3 = (CheckBox) findViewById(R.id.checkBox3);
        category4 = (CheckBox) findViewById(R.id.checkBox4);
        category5 = (CheckBox) findViewById(R.id.checkBox5);
        category6 = (CheckBox) findViewById(R.id.checkBox6);
        category7 = (CheckBox) findViewById(R.id.checkBox7);
        category8 = (CheckBox) findViewById(R.id.checkBox8);
        category9 = (CheckBox) findViewById(R.id.checkBox9);
        category10 = (CheckBox) findViewById(R.id.checkBox10);
        category11 = (CheckBox) findViewById(R.id.checkBox11);
        category12 = (CheckBox) findViewById(R.id.checkBox12);
        button_confirm = (Button) findViewById(R.id.button_confirm);
        button_back = (Button) findViewById(R.id.button_back);

    }

    private void setAllCheckBox(){
        setCheckBox(category1,checkState.substring(0,1));
        setCheckBox(category2,checkState.substring(1,2));
        setCheckBox(category3,checkState.substring(2,3));
        setCheckBox(category4,checkState.substring(3,4));
        setCheckBox(category5,checkState.substring(4,5));
        setCheckBox(category6,checkState.substring(5,6));
        setCheckBox(category7,checkState.substring(6,7));
        setCheckBox(category8,checkState.substring(7,8));
        setCheckBox(category9,checkState.substring(8,9));
        setCheckBox(category10,checkState.substring(9,10));
        setCheckBox(category11,checkState.substring(10,11));
        setCheckBox(category12,checkState.substring(11,12));
    }
    private void setCheckBox(CheckBox checkbox, String value){
        if(value.equals("0"))
            checkbox.setChecked(false);
        else
            checkbox.setChecked(true);
    }

    private void SaveCheckState(){
        String  checkStateTemp = "";
        if(category1.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category2.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category3.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category4.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category5.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category6.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category7.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category8.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category9.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category10.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category11.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";
        if(category12.isChecked())
            checkStateTemp = checkStateTemp + "1";
        else
            checkStateTemp = checkStateTemp + "0";

        FileService.writeFile(CategorySetActivity.this, "categorySet.txt", checkStateTemp);
    }


}
